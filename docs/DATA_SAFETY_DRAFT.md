# Google Play Data Safety — Working Draft

> **Do not submit this blindly.** This mapping reflects the repository on 9 September 2026 and RevenueCat Android SDK 10.18.1. Re-check the final AAB, RevenueCat dashboard integrations, Google Play's current form, and the final privacy policy immediately before submission.

Official references:

- RevenueCat Google Play Data Safety guidance: <https://www.revenuecat.com/docs/platform-resources/google-platform-resources/google-plays-data-safety>
- Google Play Data Safety instructions: <https://support.google.com/googleplay/android-developer/answer/10787469?hl=en>
- Google Play User Data policy: <https://support.google.com/googleplay/android-developer/answer/10144311?hl=en>

## Final-code inventory

- **myQuote** is the public product name. The stable technical package remains `com.shipaton.quotesofwisdom`.
- RevenueCat is the only third-party network SDK declared directly by the app.
- The app does not include advertising, Firebase, a standalone analytics SDK, or a crash-reporting SDK.
- The app does not call RevenueCat `collectDeviceIdentifiers()` or set email, name, phone, advertising ID, or other customer attributes.
- The app does not create a customer identifier from Android ID or other device identifiers. RevenueCat generates and caches its own random anonymous App User ID because the app has no login system.
- RevenueCat receives purchase/entitlement requests and retains purchase history.
- Quotes, favorites, streaks, themes, trial state, speech preferences, and reminder preferences otherwise remain local, subject to Android backup behavior.
- Android Text-to-Speech may pass bundled quote text to the user's selected speech engine. The quote is app content rather than user-provided data, but the final policy should still disclose network-capable voice behavior.
- Share is a user-initiated transfer through Android's share sheet.

## Proposed top-level answers

| Play Console question | Working answer | Reason / blocker |
|---|---|---|
| Does the app collect or share required user data types? | **Yes** | RevenueCat collects purchase history. |
| Is collected data encrypted in transit? | **Yes** | RevenueCat documents encryption in transit; the app disables cleartext traffic. Verify every final provider. |
| Can users request deletion? | **Not ready to answer Yes** | First publish a support channel and a process for handling RevenueCat deletion requests. |
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

RevenueCat's current Google Play Data Safety guidance marks this category as conditional when integrations use identifiers such as `gpsAdId` or `androidId`.

The current myQuote code:

- does not read Android ID for billing identity;
- does not call `collectDeviceIdentifiers()`;
- does not use an advertising or attribution SDK;
- configures RevenueCat without a custom App User ID, allowing RevenueCat to create its own random anonymous identifier.

**Working position:** do not declare Device or other IDs solely because of the current direct RevenueCat integration unless the final Play Console wording, RevenueCat configuration, or another integration requires it. Re-check this immediately before submission rather than relying on this draft.

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

- [ ] Set RevenueCat restore behavior to **Transfer to new App User ID** for the no-login anonymous-user model. Check the sandbox override separately if enabled.
- [ ] Confirm no RevenueCat advertising, attribution, webhook, or third-party analytics integration shares customer/purchase data outside service-provider processing.
- [ ] Confirm no RevenueCat customer attributes are configured elsewhere.
- [ ] Confirm the production SDK dependency graph contains no additional data-collecting libraries.
- [ ] Decide whether the app targets children; this can materially change identifier and disclosure requirements.
- [ ] Publish a support email/contact form for privacy and deletion requests.
- [ ] Define a practical process for helping a user identify the RevenueCat customer record associated with a purchase without requesting unnecessary personal data.
- [ ] Test the deletion process in RevenueCat and document what purchase records Google/RevenueCat must retain.
- [ ] Make the final myQuote Privacy Policy consistent with the submitted Data Safety form.
- [ ] Update this mapping whenever an SDK, integration, account system, analytics tool, or data flow changes.
