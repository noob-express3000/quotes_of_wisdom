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
- Primary readable copy uses the active secondary color.
- Dynamic/Material defaults must not introduce a hidden fourth color.
- The home screen does not show a redundant `Quotes of Wisdom` title.
- Quote text is displayed without added opening/closing quotation marks.
- v1 ships with **100 themes**: 2 Trial themes and 98 Pro themes.
- Quotes are local and carry author + classification metadata.

## Home interaction details

- Settings gear: top-left, accent/tertiary color.
- Daily streak: centered in the top header as a compact flame/count treatment.
- Access label (`FREE`, `GRACE`, `LOCKED`, `PRO`): top-right as floating accent text, without a pill/background.
- Opening quote displays immediately.
- If TTS is available and access permits speech, narration begins about 2 seconds later.
- Favorite/Bookmark: bottom-left inside the quote container below the author.
- Share: bottom-right inside the quote container below the author.
- Long quote text is directly swipe-scrollable.
- Replay restarts the current quote using TTS queue flush.
- Next stops active speech before advancing.
- Favorites are persisted locally.
- Favorites open in their own dedicated screen from Settings, with a close control and per-item removal.
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
- Theme cards use accent borders and secondary text.

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
- Important functionality cannot depend on animation alone.
- TTS failure/unavailability must never prevent reading quote text.

## Speech behavior

The app uses Android's installed Text-to-Speech ecosystem rather than bundling its own neural model for v1.

### Trial

- TTS enabled during `TRIAL_ACTIVE`.
- Trial uses the device's system-default TTS engine.
- One fixed English voice and fixed 1.0x speed.
- Prefer a higher-quality local/on-device English voice over a network-required voice.
- OEM TTS initialization/voice-enumeration failures degrade to text-only behavior instead of crashing the app.
- `Get more voices` is available as a device-compatibility action even outside Pro; it is not treated as a premium feature.

### Pro

- Enumerate every installed Android TTS engine reported by `TextToSpeech.getEngines()`.
- Expose an Engine selector above the Voice selector.
- Engine selection persists locally and reinitializes Android TTS against the selected engine package.
- After an engine switch, reload that engine's installed English voices.
- Voice labels identify local versus online/network-backed voices.
- Voices marked by Android as not yet installed are not presented as ready-to-use choices.
- Adjustable speech rate from 0.7x to 1.4x.
- Engine/voice/rate preferences persist locally.

### Additional device voices

- `Get more voices` first launches `TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA` scoped to the currently selected engine when possible.
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
- Adjustable speech speed.
- No launch upgrade interruption.

A `cold app launch` means a new app-session entry, not every temporary Activity resume.

## Pro / paywall presentation

- Pro should feel premium through polish, themes, speech customization, and interaction quality rather than feature clutter.
- Paywall headline: `Choose your plan`.
- Show only plan name + price on each pricing card; remove `Try It!`, `Best Value!`, `Own It!` and similar eyebrow statements.
- Weekly, Monthly and Lifetime cards are horizontally centered and the plan group is centered in the screen.
- Monthly retains the thicker emphasis border.
- All pricing-card borders use active accent/tertiary.
- Pricing-card text uses active secondary.
- Each pricing card is itself the purchase target; there is no separate `Choose` button.
- No spinning Pro hero/card interaction.
- No filler/explanatory copy beneath the plans.
- A small top-left info button uses active accent/tertiary and explains the immediate value of upgrading: immediate Pro access, all 100 themes, voice selection/speed controls, restored speech/access where applicable, and no launch upgrade interruption.
- All paywall UI follows the active three-color palette.

## App icon

- Do not use a generic light-bulb icon.
- The launcher icon is a bold minimal `Q` mark using the app's three-color visual language.
- Adaptive and legacy launcher resources are both provided.

## Commercial products

Initial target prices:

- Weekly: approximately USD 0.50.
- Monthly: approximately USD 1.00.
- Lifetime: approximately USD 29.00.

The physical-test build may show target-price placeholders. The production RevenueCat build must display store/RevenueCat localized pricing rather than hardcoded currency strings.

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

The final purchase/trial implementation derives a stable opaque RevenueCat App User ID from app-scoped device information rather than using a random anonymous RevenueCat ID.

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
