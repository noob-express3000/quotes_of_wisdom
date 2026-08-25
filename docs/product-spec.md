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
- Unimportant/supporting copy uses the secondary color rather than consuming the accent role.
- Dynamic/Material colors must not introduce a hidden fourth color.
- The home screen does not show a redundant `Quotes of Wisdom` title.
- Quote text is displayed without added opening/closing quotation marks.
- v1 now ships with **50 themes**: 2 Trial themes and 48 Pro themes.
- Quotes are local and carry author + classification metadata.

## Home interaction details

- Settings gear: top-left, accent/10 color.
- Access label (`FREE`, `GRACE`, `LOCKED`, `PRO`): top-right as floating accent text, without a pill/background.
- Opening quote displays immediately.
- If TTS is available and access permits speech, narration begins about 2 seconds later.
- Favorite/Bookmark: bottom-left inside the quote container below the author.
- Share: bottom-right inside the quote container below the author.
- Long quote text is directly swipe-scrollable. The extra downward-arrow scroll control is removed.
- Replay restarts the current quote using TTS queue flush.
- Next stops active speech before advancing.
- Favorites are persisted locally.
- Favorites open in their own dedicated screen from Settings, with a close control and per-item removal.
- Sharing remains plain attributed text for the current test build; final share-card polish can follow later.

## Quote personalization

- The global quote deck keeps the no-repeat shuffle behavior.
- Favoriting quotes is also a lightweight preference signal.
- When favorites exist, the app identifies the user's three most-favorited classifications.
- `Next` biases approximately 70% of eligible draws toward those classifications while still consuming the same global no-repeat deck.
- The remaining draws preserve exploration and prevent the app from collapsing into a single category.

## Theme direction

- Theme experimentation should use visually striking, proven three-color combinations and strong real-world color references.
- The library should include both restrained/premium palettes and bolder high-contrast combinations; avoid making the collection feel uniformly soft or "hippy".
- Every theme still obeys the exact three-base-color and perceptual 60/30/10 rules.
- Theme cards keep consistent title/border placement throughout the gallery.

## Settings hierarchy

User-facing order after the header:

1. Favorites launcher.
2. Speech controls / Pro speech upsell.
3. Daily streak, shown with lightweight typography rather than another bordered card.
4. Themes last.

Debug-only access controls may follow the user-facing sections.

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
- Touch-triggered premium animation must degrade gracefully when motion is reduced/disabled.
- TTS failure/unavailability must never prevent reading quote text.

## Speech behavior

### Trial

- TTS enabled during `TRIAL_ACTIVE`.
- One fixed English voice and fixed 1.0x speed.
- The fixed voice is chosen deterministically from the device's available English voices, preferring a higher-quality **local/on-device** voice over a network-required voice.
- OEM TTS initialization/voice-enumeration failures degrade to text-only behavior instead of crashing the app.

### Pro

- Selectable English voices exposed by the Android TTS engine.
- Voice labels identify whether a voice is local or online/network-backed.
- Adjustable speech rate from 0.7x to 1.4x.
- Voice/rate preferences persist locally.

## Access lifecycle

### TRIAL_ACTIVE

Starts automatically on first installation. No card or subscription enrollment is required.

Duration: 30 days.

Capabilities:

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
- Upgrade notifications continue for at least the first 4 locked days, producing at least 7 post-trial reminder days total.

### PRO

Any active paid product granting `pro_access` enters this state.

- Continued app access.
- All 50 themes.
- TTS.
- Selectable voices.
- Adjustable speech speed.
- No launch upgrade interruption.

A `cold app launch` means a new app-session entry, not every temporary Activity resume.

## Pro / paywall presentation

- Pro should feel premium through polish, themes, speech customization, and interaction quality rather than feature clutter.
- Weekly label: `Try It!`
- Monthly label: `Best Value!`
- Lifetime label: `Own It!`
- Monthly retains the thicker emphasis border.
- Pricing cards have the small touch-triggered spin interaction; the large Pro hero card stays visually stable to reduce motion clutter.
- All paywall UI follows the active three-color palette.

## App icon

- Do not use a generic light-bulb icon.
- The launcher icon is a custom three-color mark based on a bold `Q`/quote motif using the default dominant, secondary and accent colors.
- Adaptive and legacy launcher resources are both provided.

## Commercial products

Initial target prices:

- Weekly: approximately USD 0.50 — `Try It!`
- Monthly: approximately USD 1.00 — `Best Value!`
- Lifetime: approximately USD 29.00 — `Own It!`

The production UI must display store/RevenueCat localized pricing rather than hardcoded currency strings.

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

RevenueCat customer history/first-seen timing can serve as an external trial-history anchor. Local DataStore state remains the fast/offline cache.

## Clock rollback hardening

Trial calculations must not trust user-editable wall time alone. Track:

- local trial-start timestamp,
- latest observed wall-clock timestamp,
- monotonic elapsed time during a running installation,
- RevenueCat first-seen/customer timing when available.

Moving the device clock backwards must never grant additional trial time.

## Debug demo behavior

- Trial/Grace/Locked/Pro access previews exist only in debug builds.
- The selected debug demo state persists across app restarts for reliable demonstrations.
- Release builds ignore any persisted debug override value.

## Known limits

This design deters casual trial resets without a backend/account system. It is not intended to resist factory resets, new Android user profiles, signing-key changes, rooted/device-tampered environments, or sophisticated integrity bypasses.
