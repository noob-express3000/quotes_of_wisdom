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
- The 60/30/10 values are semantic color roles, not a requirement to pixel-count exact percentages.
- The dominant/60 color owns the app canvas and the largest visible surfaces.
- The secondary/30 color is the primary text/structural color and the background color of primary controls.
- The accent/10 color is reserved for control labels/highlights and other small emphasis areas.
- The home screen does not show a redundant `Quotes of Wisdom` title in the top-left corner.
- Quote text is displayed without added opening/closing quotation marks; the bordered quote container already provides the visual framing.
- v1 ships with 20 themes.
- Quotes are local and carry author + classification/genre metadata.
- The control previously interpreted as volume is a quote-text scroll control.
- A handwritten note contains an unfinished requirement beginning `Replay button should ...`; no additional Replay-specific UI rule is frozen until that requirement is completed.

## Home interaction details

- The Settings gear is placed at the top-left of the home screen and uses the active theme's accent/10 color. Do not introduce a separate Material tertiary color.
- The FREE/PRO status chip remains at the top-right.
- The opening quote is displayed immediately when the app opens.
- If TTS is available and the current access state permits speech, the opening quote begins speaking approximately 2 seconds after app launch.
- A Share icon sits at the bottom-right of the quote container, below the author line.
- A Favorite/Bookmark icon sits at the opposite bottom-left position of the quote container, below the author line.
- Share, Favorite, Settings, and all other controls must remain inside the active theme's exact three-color palette.
- Favorites are local-first and persisted on-device.
- Sharing should produce a clean, tasteful quote-sharing experience suitable for organic distribution; final shared-card treatment is designed later without changing quote authenticity or attribution.

## Theme direction

- Theme experimentation should draw from established visually striking three-color combinations and strong real-world color references.
- Prioritize visual impact and premium feel while preserving the exact three-base-color and perceptual 60/30/10 rules.
- Do not add extra colors for shadows, icon ink, animation, or Material defaults.

## Retention and streak behavior

- Daily quote notifications are part of the retention loop.
- A streak day is completed by opening the app at least once during that local calendar day. No extra interaction, playback, or quote-navigation requirement is imposed.
- Reopening the app multiple times on the same calendar day does not increment the streak more than once.
- A missed local calendar day breaks the streak.
- When a user breaks a streak, the app should respond with an especially strong motivational quote rather than punitive or guilt-heavy messaging.
- Notification copy should feel distinctive and may be humorous where appropriate rather than generic app-retention copy.
- Humor must not undermine the meaning of serious quotes or become disrespectful after a broken streak.

## Accessibility baseline

Accessibility should be built in as implementation hygiene rather than treated as a separate visual mode:

- Respect Android font scaling and avoid clipping long quotes.
- Provide TalkBack/content descriptions for icon-only controls.
- Use appropriately sized touch targets.
- Preserve readable contrast inside every three-color theme.
- Keep all important functionality usable without relying on animation alone.
- Touch-triggered premium animations should degrade gracefully when motion is reduced or disabled.
- TTS failure/unavailability must not prevent reading quote text.

## Access lifecycle

### TRIAL_ACTIVE

Starts automatically on first installation. No card or subscription enrollment is required.

Duration: 30 days.

Trial capabilities:

- Full quote browsing.
- Replay and Next.
- Quote-text scrolling.
- Two themes.
- TTS enabled with one fixed voice.
- Fixed TTS speed.
- On each cold app launch, show the upgrade/paywall experience before home. It is dismissible while the trial is active.

### GRACE_TEXT_ONLY

Begins immediately after the 30-day trial expires.

Duration: 3 days.

Capabilities:

- Quote text remains accessible.
- TTS is disabled.
- On each cold app launch, show the upgrade/paywall experience before home. It remains dismissible during this grace period.
- Upgrade messaging is shown.
- Upgrade notifications may be delivered.

### LOCKED

Begins after the 3-day text-only grace period.

- Core app access is blocked by the upgrade/paywall experience.
- The launch paywall is no longer dismissible while no valid Pro entitlement exists.
- Upgrade notifications continue for at least the first 4 locked days, producing at least 7 post-trial days of upgrade reminders in total.
- The app remains locked after that period until a valid Pro entitlement is active.

### PRO

Any active paid product granting the `pro_access` entitlement enters this state.

Capabilities:

- Continued app access.
- All 20 themes.
- TTS.
- Multiple selectable TTS voices.
- Adjustable TTS speed.
- No launch upgrade interruption while Pro is active.

A `cold app launch` means a new app-session entry, not every Android Activity resume after temporary backgrounding or notification-shade interaction.

## Pro presentation

- Pro should feel premium primarily through polished themes, speech customization, and interaction quality rather than feature clutter.
- The Pro card should have a small touch-triggered motion treatment, such as a subtle flip or spin. The exact motion is selected after visual testing.
- The animation must stay tasteful, responsive, and compatible with reduced-motion accessibility behavior.

## Commercial products

Initial target prices:

- Weekly: approximately USD 0.50 — paywall label: `Try It!`
- Monthly: approximately USD 1.00 — paywall label: `Best Value!`
- Lifetime: approximately USD 29.00 — paywall label: `Own It!`

The UI must display store/RevenueCat localized pricing rather than hardcoded currency strings.

All paid products grant the same RevenueCat entitlement:

`pro_access`

The application should not branch on weekly/monthly/lifetime product identifiers when deciding feature access.

## Trial implementation

The 30-day install trial is app-controlled because it begins automatically on installation without requiring the user to enroll in an auto-renewing subscription.

RevenueCat remains authoritative for paid Pro entitlement state.

Internal access model:

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

The app will not use RevenueCat's randomly generated anonymous ID for the final purchase/trial implementation.

Instead, it will derive a stable opaque RevenueCat App User ID from app-scoped device information. The raw Android identifier must never be displayed, logged, or transmitted as the RevenueCat identifier.

Conceptual derivation:

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

This gives the same signed app on the same Android user/device a stable RevenueCat identity across normal uninstall/reinstall cycles while avoiding a custom login system.

RevenueCat customer history, including first-seen timing when available, can then be used as an external anchor for trial history. Local DataStore state remains the fast/offline cache.

## Clock rollback hardening

Trial calculations must not trust the user-editable wall clock by itself.

The implementation will track:

- local trial-start timestamp,
- latest observed wall-clock timestamp,
- monotonic elapsed time during a running installation,
- RevenueCat first-seen/customer timing when available.

Moving the device clock backwards must never grant additional trial time. If timestamps disagree, the access controller should choose the earliest defensible trial start / most restrictive valid elapsed state rather than extending access.

## Known limits

This design is intended to deter casual trial resets without creating a backend or account system. It is not expected to resist factory resets, a new Android user profile, signing-key changes, rooted/device-tampered environments, or sophisticated integrity bypasses. Stronger abuse resistance would require platform attestation and/or server-side state and is intentionally outside v1 unless real-world abuse justifies it.
