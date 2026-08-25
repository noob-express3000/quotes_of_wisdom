package com.shipaton.quotesofwisdom.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

sealed interface TtsState {
    data object Initializing : TtsState
    data object Ready : TtsState
    data object Speaking : TtsState
    data class Error(val message: String) : TtsState
    data object Shutdown : TtsState
}

class TtsController(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    private val _state = MutableStateFlow<TtsState>(TtsState.Initializing)
    val state: StateFlow<TtsState> = _state.asStateFlow()

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            _state.value = TtsState.Error("Text-to-speech could not initialize on this device.")
            return
        }

        val languageResult = engine.setLanguage(Locale.US)
        if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
            languageResult == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            _state.value = TtsState.Error("An English text-to-speech voice is not installed.")
            return
        }

        engine.setSpeechRate(1.0f)
        engine.setPitch(1.0f)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.value = TtsState.Speaking
            }

            override fun onDone(utteranceId: String?) {
                _state.value = TtsState.Ready
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.value = TtsState.Error("Text-to-speech playback failed.")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _state.value = TtsState.Error("Text-to-speech playback failed ($errorCode).")
            }
        })
        _state.value = TtsState.Ready
    }

    fun speak(text: String) {
        val engine = tts ?: return
        if (_state.value is TtsState.Error || _state.value == TtsState.Shutdown) return
        val result = engine.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "quote-${UUID.randomUUID()}"
        )
        if (result == TextToSpeech.ERROR) {
            _state.value = TtsState.Error("Text-to-speech playback could not start.")
        }
    }

    fun stop() {
        tts?.stop()
        if (_state.value !is TtsState.Error && _state.value != TtsState.Shutdown) {
            _state.value = TtsState.Ready
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _state.value = TtsState.Shutdown
    }
}
