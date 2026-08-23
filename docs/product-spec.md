# Quotes of Wisdom — Product Specification v1

## Product constraints

- Android only.
- Local-first. No custom backend and no login/registration system.
- RevenueCat is used for purchases/entitlements, not authentication.
- Every visual theme contains exactly three colors.
- Theme composition follows a perceptual 60/30/10 hierarchy:
  - 60% dominant
  - 30% secondary
  - 10% accent
- v1 ships with 20 themes.
- Quotes are local and carry author + classification/genre metadata.
- The control previously interpreted as volume is a quote-text scroll control.

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
- Lifetime: approximately USD 70.00.

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

## Known limitation

A purely local install-based trial can be reset by some reinstall/device-reset scenarios and is susceptible to deliberate clock manipulation. v1 accepts this tradeoff to preserve the no-login/no-backend architecture. We can harden it later if real-world abuse justifies the complexity.
