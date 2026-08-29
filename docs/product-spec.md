# Quotes of Wisdom — Product Specification v1

## Product constraints

- Android only.
- Local-first. No custom backend and no login/registration system.
- RevenueCat is used for purchases/entitlements, not authentication.
- Every visual theme contains exactly three base colors.
- Theme composition follows a perceptual 60/30/10 hierarchy:
  - 60% dominant
  - 30% secondary
  - 10% accent
- The dominant/60 color owns the app canvas and largest surfaces.
- The secondary/30 color owns primary text and structural/control backgrounds.
- The accent/10 color owns borders, icons, labels and small emphasis.
- Material `tertiary` is an alias for the active theme accent; it is not a fourth color.
- All card/container borders use the active accent/tertiary color.
- All button labels use the active accent/tertiary color; disabled labels may reduce opacity but keep the accent hue.
- The Home access label (`FREE`, `GRACE`, `LOCKED`, `PRO`) uses the active accent/tertiary color.
- Primary readable copy uses the active secondary color.
- Dynamic/Material defaults must not introduce a hidden fourth color.
- The home screen does not show a redundant `Quotes of Wisdom` title.
- Quote text is displayed without added opening/closing quotation marks.
- v1 ships with **100 themes**: 2 Trial themes and 98 Pro themes.
- Theme display names are color-accurate and use at most two words.
- The app uses immersive full-screen rendering: the active dominant theme color owns the entire display, Android status/navigation bars are hidden during normal use, and system bars may be revealed transiently with the platform edge gesture/swipe.
- The window is allowed to render into short-edge display-cutout regions. Controls are placed around likely camera/cutout areas instead of reserving one global safe-drawing band across the entire screen.
- Quotes are local and carry author + classification metadata.

## Home interaction details

- Settings gear: top-left, accent/tertiary color, positioned close to the physical top edge.
- Daily streak: centered below the top camera/cutout line as a compact flame/count treatment.
- Tapping the streak triggers one approximately 1.2-second full-screen surge built from dense layered flame walls, curved tongues and embers. The effect uses only colors from the active three-color palette, does not change streak state, and has no continuously running particle system.
- Access label (`FREE`, `GRACE`, `LOCKED`, `PRO`): top-right as floating accent text, without a pill/background, positioned close to the physical top edge.
- When the access label is `PRO`, tapping it rotates only the text through one in-place 360-degree spin. It has no ripple, scale pulse, background change, navigation or entitlement/state change.
- Opening quote displays immediately.
- If TTS is available and access permits speech, narration begins as soon as the quote is visible and the TTS engine is ready; there is no artificial app-side delay.
- Favorite/Bookmark: bottom-left inside the quote container below the author.
- Share: bottom-right inside the quote container below the author.
- Long quote text is directly swipe-scrollable.
- Replay restarts the current quote using TTS queue flush.
- Next stops active speech before advancing.
- Favorites are persisted locally.
- Favorites open in their own dedicated screen from Settings, with a close control and per-item removal.
- Tapping a saved favorite plays that quote through TTS when the current access state permits speech.
- When Favorites has no saved bookmarks, do not show explanatory empty-state copy.
- Sharing remains plain attributed text for the current test build; final share-card polish can follow later.

## Quote personalization

- The global quote deck keeps the no-repeat shuffle behavior.
- Favoriting quotes is also a lightweight preference signal.
- When favorites exist, the app identifies the user's three most-favorited classifications.
- `Next` biases approximately 70% of eligible draws toward those classifications while still consuming the same global no-repeat deck.
- Remaining draws preserve exploration.

## Theme direction

- Use visually striking, proven three-color combinations and strong real-world color references.
- Keep a mix of restrained/premium and bold/high-contrast palettes.
- Every theme obeys the exact three-base-color and perceptual 60/30/10 rules.
- Theme display names use no more than two words and must describe the actual palette rather than legacy/internal IDs.
- Theme cards use accent borders and secondary text.
- Theme rows are lazily composed so the 100-theme library does not build all tiles at once.
- Theme rows expose a stable content type for lazy reuse, and each tile draws its three color swatches in one lightweight Canvas rather than composing three separate Material surfaces.
- Use immersive full-screen presentation so the dominant theme color visually owns the complete device display rather than stopping at persistent system-bar regions.

## Settings hierarchy

User-facing order after the header:

1. Favorites launcher.
2. Speech controls / Pro speech upsell.
3. Themes.

The streak is not duplicated in Settings; it lives on the Home header. Debug-only access controls may follow the user-facing sections in debug builds.

Settings copy stays terse.

## Retention and streak behavior

- Daily quote notifications are part of the retention loop.
- A streak day is completed by opening the app at least once during that local calendar day.
- Multiple openings in the same day count once.
- A missed local calendar day breaks the streak.
- Broken streak response uses an especially strong motivational quote rather than guilt-heavy messaging.
- Notification copy should feel distinctive and may be humorous where appropriate.

## Accessibility baseline

- Respect Android font scaling and avoid clipping long quotes.
- Provide TalkBack/content descriptions for icon-only controls.
- Use appropriately sized touch targets.
- Preserve readable contrast inside every three-color theme.
- Transient Android system bars remain reachable through platform gestures and use readable icon appearance against the active dominant color.
- Important functionality cannot depend on animation alone.
- The streak flame surge and text-only Pro spin are decorative microinteractions only; all underlying actions/state remain usable without seeing the animation.
- TTS failure/unavailability must never prevent reading quote text.

## Speech behavior

The app uses Android's installed Text-to-Speech ecosystem rather than bundling its own neural model for v1.

### Trial

- TTS enabled during `TRIAL_ACTIVE`.
- Trial uses the device's system-default TTS engine.
- One fixed English voice and fixed 1.0x speed.
- Prefer a higher-quality local/on-device English voice over a network-required voice.
- OEM TTS initialization/voice-enumeration failures degrade to text-only behavior instead of crashing the app.
- Additional voice-data installation is not exposed in Trial settings.

### Pro

- Enumerate every installed Android TTS engine reported by `TextToSpeech.getEngines()`.
- Expose an Engine selector above the Voice selector.
- Engine selection persists locally and reinitializes Android TTS against the selected engine package.
- After an engine switch, reload that engine's installed English voices.
- Voice labels identify local versus online/network-backed voices.
- Voices marked by Android as not yet installed are not presented as ready-to-use choices.
- `Get more voices` is a Pro feature and exposes the platform/engine voice-data installation flow.
- Adjustable speech rate from 0.7x to 1.4x.
- Engine/voice/rate preferences persist locally.

### Additional device voices

- The `Get more voices` action is visible only while the effective access state is Pro.
- It first launches `TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA` scoped to the currently selected engine when possible.
- If the selected engine does not expose a voice-data installer, retry the generic Android TTS install-data action.
- If neither installer is available, fall back to system Settings rather than failing the app.
- When the external installer/settings UI returns, reinitialize the active TTS engine so newly downloaded voices appear in the app.
- Custom bundled/downloadable neural voice models are deferred beyond v1 unless real user demand justifies the additional inference, model-storage, licensing and device-performance complexity.

## Access lifecycle

### TRIAL_ACTIVE

Starts automatically on first installation. No card or subscription enrollment is required.

Duration: 30 days.

- Full quote browsing.
- Replay and Next.
- Quote-text scrolling.
- Two themes.
- TTS with one fixed local-preferred voice and fixed speed.
- On each cold app launch, show the upgrade/paywall experience before home. It is dismissible during Trial.

### GRACE_TEXT_ONLY

Duration: 3 days immediately after Trial.

- Quote text remains accessible.
- TTS is disabled.
- Launch paywall is shown and remains dismissible.
- Upgrade messaging/notifications may be shown.

### LOCKED

Begins after the 3-day text-only grace period.

- Core app access is blocked by the upgrade/paywall experience.
- Launch paywall is non-dismissible without valid Pro access.
- Upgrade notifications continue for at least the first 4 locked days.

### PRO

Any active paid product granting `pro_access` enters this state.

- Continued app access.
- All 100 themes.
- TTS.
- Selectable installed TTS engines and English voices.
- Additional device voice-data installation through `Get more voices`.
- Adjustable speech speed.
- No launch upgrade interruption.

A `cold app launch` means a new app-session entry, not every temporary Activity resume.

The last successfully confirmed active RevenueCat entitlement seeds the next cold launch while
the SDK refreshes in the background. Free/unknown startup still waits for a fresh entitlement
resolution. A transient entitlement refresh failure must not downgrade a known Pro user or flash
the paywall; only a successful RevenueCat response confirming inactive access may replace a
previously confirmed Pro state. The local entitlement snapshot is excluded from cloud backup and
device transfer.

## Pro / paywall presentation

- Pro should feel premium through polish, themes, speech customization, and interaction quality rather than feature clutter.
- Paywall headline: `Choose your plan`.
- Show only plan name + price on each pricing card; remove `Try It!`, `Best Value!`, `Own It!` and similar eyebrow statements.
- Weekly, Monthly and Lifetime cards are horizontally centered and the plan group is centered in the screen.
- Each card displays its live RevenueCat/store localized price and cadence.
- A plan remains disabled until its real localized product and price have loaded; loading and unavailable states never display invented fallback prices.
- Monthly retains the thicker emphasis border.
- All pricing-card borders use active accent/tertiary.
- Pricing-card text uses active secondary.
- Each pricing card is itself the purchase target; there is no separate `Choose` button.
- No continuous/spinning paywall hero or Pro card interaction; the one-shot Home `PRO` label spin is a separate decorative microinteraction.
- No filler/explanatory copy beneath the plans.
- A small top-left info button uses active accent/tertiary and opens a prominent full-width Pro access card near the top of the display rather than expanding/reflowing the paywall itself.
- The Pro access card uses a stronger 2dp accent border, larger heading/body type, respects the display cutout itself, and is intentionally attention-grabbing without becoming a second paywall.
- The card explains the immediate value of upgrading: immediate Pro access, all 100 themes, engine/voice selection, additional voice downloads, speed controls, restored speech/access where applicable, and no launch upgrade interruption.
- The info card can be dismissed with its close control or by tapping outside it.
- All paywall UI follows the active three-color palette.

## App icon

- Do not use a generic light-bulb icon.
- The launcher icon is a bold minimal `Q` mark using the app's three-color visual language.
- Adaptive and legacy launcher resources are both provided.

## Commercial products

Frozen Test Store/catalog pricing:

- Weekly: USD 0.99.
- Monthly: USD 2.99.
- Lifetime: USD 29.99.

These values replace the earlier approximate `$0.50 / $1.00 / $29` targets and describe the intended store catalog. Runtime paywall prices must always come from RevenueCat/store localized pricing.

All paid products grant the same RevenueCat entitlement:

`pro_access`

Feature access must not branch on weekly/monthly/lifetime product identifiers.

## Trial implementation

The 30-day install trial is app-controlled because it begins automatically on installation without requiring auto-renewing subscription enrollment.

RevenueCat remains authoritative for paid Pro entitlement state.

```text
TRIAL_ACTIVE
    |
    | 30 days
    v
GRACE_TEXT_ONLY
    |
    | 3 days
    v
LOCKED

Any state + active pro_access -> PRO
```

## Trial identity hardening

The final purchase/trial implementation derives a stable opaque device-scoped App User ID from app-scoped device information rather than using a random anonymous RevenueCat ID.

```text
ANDROID_ID
+ application package name
+ app signing-certificate fingerprint
        |
        v
SHA-256
        |
        v
opaque RevenueCat App User ID
```

The raw Android identifier must never be displayed, logged, or transmitted as the RevenueCat identifier.

RevenueCat customer history/first-seen timing can serve as an external trial-history anchor. Local DataStore remains the fast/offline cache.

## Clock rollback hardening

Trial calculations must not trust user-editable wall time alone. Track local trial start, latest observed wall time, monotonic elapsed time during a running installation, and RevenueCat first-seen/customer timing when available.

Moving the device clock backwards must never grant additional trial time.

## Debug demo behavior

- Trial/Grace/Locked/Pro access previews exist only in debug builds.
- The selected debug demo state persists across app restarts for reliable demonstrations.
- Release builds ignore any persisted debug override value.
- Keep debug controls functional but visually terse.

## Known limits

This design deters casual trial resets without a backend/account system. It is not intended to resist factory resets, new Android user profiles, signing-key changes, rooted/device-tampered environments, or sophisticated integrity bypasses.
