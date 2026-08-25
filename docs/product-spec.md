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

### GRACE_TEXT_ONLY

Begins immediately after the 30-day trial expires.

Duration: 3 days.

Capabilities:

- Quote text remains accessible.
- TTS is disabled.
- Upgrade messaging is shown.
- Upgrade notifications may be delivered.

### LOCKED

Begins after the 3-day text-only grace period.

- Core app access is blocked by the upgrade/paywall experience.
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

## Commercial products

Initial target prices:

- Weekly: approximately USD 0.50.
- Monthly: approximately USD 1.00.
- Lifetime: approximately USD 45.00.

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
