# Privacy Policy for Quotes of Wisdom — Draft

> **Not ready to publish:** replace every bracketed placeholder, confirm the final production SDK/data inventory, obtain a permanent public URL, and link that URL inside the app and Google Play Console.

- **Effective date:** 29 August 2026
- **Developer/Data controller:** [DEVELOPER OR LEGAL NAME]
- **Privacy and support contact:** [SUPPORT EMAIL]
- **App package:** `com.shipaton.quotesofwisdom`

Quotes of Wisdom is a local-first Android application. This policy explains what information the app stores on your device, what is processed by service providers to offer purchases and speech, and the choices available to you.

## Summary

- Quotes of Wisdom does not contain advertising or a third-party analytics SDK.
- The app does not request access to precise or approximate device location.
- Quotes, favorites, themes, streaks, trial state, and reminder preferences are primarily stored on your device.
- RevenueCat processes an opaque app-user identifier and purchase/entitlement information so paid Pro access can work.
- Google Play processes production payments; the direct judge build uses RevenueCat Test Store and does not charge real money.
- If you choose a network-capable Text-to-Speech voice, the installed speech provider may process quote text under its own privacy terms.

## Information stored locally

The app stores information needed to provide its features, including:

- favorite quotes and quote-deck progress;
- selected theme;
- streak and last-opened-day state;
- trial/grace/access timing state;
- Text-to-Speech engine, voice, and speed preferences;
- reminder enablement and selected reminder time;
- the last successfully confirmed Pro-entitlement state.

This information is not sent to a custom Quotes of Wisdom backend because the app does not operate one. It remains on the device unless a feature described below requires a service provider. Some ordinary app preferences—including trial timing—may be included in Android's device backup if the user enables Android backup. The RevenueCat entitlement snapshot and local notification records are excluded from app backup where configured.

## Purchases and Pro entitlement

Quotes of Wisdom uses RevenueCat to load product offerings, process purchase results, restore purchases, and determine whether the `pro_access` entitlement is active.

The app creates its RevenueCat App User ID locally from:

```text
Android ID + app package name + app signing-certificate fingerprint
 -> SHA-256
 -> opaque identifier
```

The app does not pass the raw Android ID as this customer identifier. RevenueCat receives the resulting opaque identifier together with information needed to provide and troubleshoot offerings, transactions, subscriptions, and entitlements. RevenueCat may also process app configuration, locale/currency, network, and service-request information as described in its own policy.

In a production Google Play build, Google Play handles payment credentials and payment processing. Quotes of Wisdom does not receive or store full payment-card details. The judge/QA build uses RevenueCat Test Store and makes no real-money charge.

Service-provider policies:

- RevenueCat Privacy Policy: <https://www.revenuecat.com/privacy/>
- Google Privacy Policy: <https://policies.google.com/privacy>
- Google Play Terms: <https://play.google.com/about/play-terms/>

## Text-to-Speech

Quotes of Wisdom uses Android's installed Text-to-Speech system. When narration is requested, the current quote text is provided to the Text-to-Speech engine selected on the device.

- A local/on-device voice may process the text entirely on the device.
- A network-required voice may send the text to that engine provider.
- The available engines and their privacy behavior are controlled by the device and the installed provider, not by Quotes of Wisdom.

Users can avoid network speech processing by selecting a local voice where available or by not using narration.

## Sharing

When a user taps Share, Quotes of Wisdom sends the current quote and author attribution to Android's system share sheet. The content is provided only to the app/service the user chooses. That recipient processes the shared content under its own privacy terms.

## Notifications

Daily reminders are scheduled locally with Android system services. On Android 13 and newer, the app requests notification permission. Reminder preferences remain local. Users can disable reminders inside the app or through Android system settings.

## Location

Quotes of Wisdom does not request Android location permission and does not use GPS or nearby-device signals to determine location. Store providers may select currency or regional availability from the user's store country, and network service providers may process an IP address under their own policies; the app does not convert that information into a location profile.

## Retention and deletion

Local app data remains until it is overwritten through normal use, cleared through Android settings, or removed by uninstalling the app. Android backup may retain eligible preferences according to the user's Google/Android backup settings.

RevenueCat and Google retain transaction, subscription, entitlement, and related service records according to their legal obligations and retention policies. To request access to or deletion of information associated with Quotes of Wisdom, contact [SUPPORT EMAIL]. Some transaction records may need to be retained for legal, fraud-prevention, accounting, or platform-compliance reasons.

## Security

Quotes of Wisdom disables cleartext network traffic and relies on Android and its service providers for encrypted network transport. The app minimizes external processing by bundling its quote corpus and avoiding a custom account/backend system. No method of storage or transmission is completely secure, but the developer uses reasonable measures appropriate to the information processed.

## Children's privacy

[CONFIRM TARGET AUDIENCE AND INSERT THE APPROPRIATE CHILDREN'S PRIVACY STATEMENT BEFORE PUBLICATION.]

## Changes to this policy

This policy may be updated when app features, service providers, or legal requirements change. The effective date at the top will be updated when a revised policy is published.

## Contact

Questions or privacy requests may be sent to:

- [DEVELOPER OR LEGAL NAME]
- [SUPPORT EMAIL]
- [OPTIONAL POSTAL ADDRESS OR BUSINESS CONTACT PAGE]
