# Google Play Data Safety — Working Draft

> **Do not submit this blindly.** This mapping reflects the repository on 29 August 2026 and RevenueCat Android SDK 10.18.1. Re-check the final AAB, RevenueCat dashboard integrations, Google Play's current form, and the final privacy policy immediately before submission.

Official references:

- RevenueCat Google Play Data Safety guidance: <https://www.revenuecat.com/docs/platform-resources/google-platform-resources/google-plays-data-safety>
- Google Play Data Safety instructions: <https://support.google.com/googleplay/android-developer/answer/10787469?hl=en>
- Google Play User Data policy: <https://support.google.com/googleplay/android-developer/answer/10144311?hl=en>

## Final-code inventory

- RevenueCat is the only third-party network SDK declared directly by the app.
- The app does not include advertising, Firebase, a standalone analytics SDK, or a crash-reporting SDK.
- The app does not call RevenueCat `collectDeviceIdentifiers()` or set email, name, phone, advertising ID, or other customer attributes.
- The app supplies a custom opaque RevenueCat App User ID derived locally from Android ID, package name, and signing certificate.
- RevenueCat receives purchase/entitlement requests and retains purchase history.
- Quotes, favorites, streaks, themes, trial state, speech preferences, and reminder preferences otherwise remain local, subject to Android backup behavior.
- Android Text-to-Speech may pass bundled quote text to the user's selected speech engine. The quote is app content rather than user-provided data, but the final policy should still disclose network-capable voice behavior.
- Share is a user-initiated transfer through Android's share sheet.

## Proposed top-level answers

| Play Console question | Working answer | Reason / blocker |
|---|---|---|
| Does the app collect or share required user data types? | **Yes** | RevenueCat collects purchase history and the app sends an opaque identifier. |
| Is collected data encrypted in transit? | **Yes** | RevenueCat documents encryption in transit; the app disables cleartext traffic. Verify every final provider. |
| Can users request deletion? | **Not ready to answer Yes** | First publish a support channel and a process that can map a request to the RevenueCat customer record. |
| Does the app use an account system? | **No** | There is no registration/login or custom account backend. |

## Proposed data-type mapping

### Financial info → Purchase history

| Field | Working answer |
|---|---|
| Collected | Yes |
| Shared | No, if RevenueCat acts only as a service provider and no non-service-provider integration receives events |
| Processed ephemerally | No |
| Required or optional | Required for the paid-access flow |
| Purpose | App functionality and Analytics |

RevenueCat's current Android guidance explicitly requires Purchase history and specifies App functionality plus Analytics because purchase history powers entitlements, receipt validation, customer history, charts, and experiments.

### Device or other IDs

| Field | Working answer |
|---|---|
| Collected | Yes (conservative declaration) |
| Shared | No, if RevenueCat is the only recipient/service provider and no external integration receives it |
| Processed ephemerally | No |
| Required or optional | Required while RevenueCat billing is configured |
| Purpose | App functionality |

Reason: the app reads Android ID locally, combines it with the package name and signing certificate, hashes the material, and transmits the resulting stable opaque value as RevenueCat's App User ID. Although the raw Android ID is not passed as the customer ID, the transmitted identifier is derived from and relates to one app/device identity. Declaring Device or other IDs is the safer working position unless Google Play or qualified review confirms a different classification.

## Categories not indicated by the current repository

The current app code does not indicate collection of:

- precise or approximate location;
- name, email address, phone number, physical address, race/ethnicity, political/religious beliefs, sexual orientation, or other personal information;
- payment-card details;
- health/fitness data;
- messages, photos, videos, audio files, files/documents, calendar, or contacts;
- web-browsing or search history;
- user-generated app interactions/content;
- crash logs, diagnostics, or other app-performance telemetry;
- advertising identifiers.

Do not copy this section into Play Console without checking the final merged manifest, dependency graph, SDK behavior, and RevenueCat dashboard configuration.

## Dashboard and owner checks

- [ ] Confirm no RevenueCat advertising, attribution, webhook, or third-party analytics integration shares customer/purchase data outside service-provider processing.
- [ ] Confirm no RevenueCat customer attributes are configured elsewhere.
- [ ] Confirm the production SDK dependency graph contains no additional data-collecting libraries.
- [ ] Decide whether the app targets children; this can materially change identifier and disclosure requirements.
- [ ] Publish a support email/contact form for privacy and deletion requests.
- [ ] Define how support will identify the user's opaque RevenueCat record without requesting unnecessary personal data.
- [ ] Test the deletion process in RevenueCat and document what purchase records Google/RevenueCat must retain.
- [ ] Make the final privacy policy consistent with the submitted Data Safety form.
- [ ] Update this mapping whenever an SDK, integration, account system, analytics tool, or data flow changes.
