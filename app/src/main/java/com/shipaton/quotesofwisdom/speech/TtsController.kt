package com.shipaton.quotesofwisdom.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
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

data class VoiceOption(
    val name: String,
    val label: String
)

class TtsController(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var trialVoiceName: String? = null

    private val _state = MutableStateFlow<TtsState>(TtsState.Initializing)
    val state: StateFlow<TtsState> = _state.asStateFlow()

    private val _voices = MutableStateFlow<List<VoiceOption>>(emptyList())
    val voices: StateFlow<List<VoiceOption>> = _voices.asStateFlow()

    private val _selectedVoiceName = MutableStateFlow("")
    val selectedVoiceName: StateFlow<String> = _selectedVoiceName.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (_: Throwable) {
            _state.value = TtsState.Error("Text-to-speech is unavailable on this device.")
        }
    }

    override fun onInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            _state.value = TtsState.Error("Text-to-speech could not initialize on this device.")
            return
        }

        try {
            val languageResult = engine.setLanguage(Locale.US)
            if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                languageResult == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                _state.value = TtsState.Error("An English text-to-speech voice is not installed.")
                return
            }

            val englishVoices = safeEnglishVoices(engine)
            _voices.value = englishVoices.mapIndexed { index, voice ->
                val country = voice.locale.displayCountry.takeIf { it.isNotBlank() }
                    ?: voice.locale.displayLanguage
                val locality = if (voice.isNetworkConnectionRequired) "online" else "local"
                VoiceOption(
                    name = voice.name,
                    label = "Voice ${index + 1} · $country · $locality"
                )
            }

            val trialVoice = englishVoices.firstOrNull { !it.isNetworkConnectionRequired }
                ?: englishVoices.firstOrNull()
                ?: runCatching { engine.voice }.getOrNull()

            trialVoiceName = trialVoice?.name
            trialVoice?.let {
                runCatching { engine.voice = it }
                _selectedVoiceName.value = it.name
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
        } catch (_: Throwable) {
            _state.value = TtsState.Error("Text-to-speech is unavailable on this device.")
        }
    }

    fun applyTrialDefaults() {
        val engine = tts ?: return
        runCatching {
            trialVoiceName?.let { name ->
                safeEnglishVoices(engine).firstOrNull { it.name == name }?.let {
                    engine.voice = it
                    _selectedVoiceName.value = name
                }
            }
            engine.setSpeechRate(1.0f)
            _speechRate.value = 1.0f
        }
    }

    fun applyProSettings(voiceName: String, rate: Float) {
        val engine = tts ?: return
        runCatching {
            val resolvedName = voiceName.ifBlank { trialVoiceName.orEmpty() }
            safeEnglishVoices(engine).firstOrNull { it.name == resolvedName }?.let {
                engine.voice = it
                _selectedVoiceName.value = it.name
            }
            val safeRate = rate.coerceIn(0.7f, 1.4f)
            engine.setSpeechRate(safeRate)
            _speechRate.value = safeRate
        }
    }

    fun setProVoice(voiceName: String) {
        val engine = tts ?: return
        runCatching {
            safeEnglishVoices(engine).firstOrNull { it.name == voiceName }?.let {
                engine.voice = it
                _selectedVoiceName.value = it.name
            }
        }
    }

    fun setProSpeechRate(rate: Float) {
        val safeRate = rate.coerceIn(0.7f, 1.4f)
        runCatching { tts?.setSpeechRate(safeRate) }
        _speechRate.value = safeRate
    }

    fun speak(text: String) {
        val engine = tts ?: return
        if (_state.value is TtsState.Error || _state.value == TtsState.Shutdown) return
        val result = runCatching {
            engine.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "quote-${UUID.randomUUID()}"
            )
        }.getOrElse {
            _state.value = TtsState.Error("Text-to-speech playback could not start.")
            TextToSpeech.ERROR
        }
        if (result == TextToSpeech.ERROR) {
            _state.value = TtsState.Error("Text-to-speech playback could not start.")
        }
    }

    fun stop() {
        runCatching { tts?.stop() }
        if (_state.value !is TtsState.Error && _state.value != TtsState.Shutdown) {
            _state.value = TtsState.Ready
        }
    }

    fun shutdown() {
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
        tts = null
        _state.value = TtsState.Shutdown
    }

    private fun safeEnglishVoices(engine: TextToSpeech): List<Voice> =
        runCatching {
            engine.voices.orEmpty()
                .filter { it.locale.language.equals("en", ignoreCase = true) }
                .sortedWith(
                    compareBy<Voice> { it.isNetworkConnectionRequired }
                        .thenByDescending { it.quality }
                        .thenBy { it.latency }
                        .thenBy { it.locale.displayCountry }
                        .thenBy { it.name }
                )
        }.getOrDefault(emptyList())
}
