# Privacy Policy for myQuote — Draft

> **Not ready to publish:** replace every bracketed placeholder, confirm the final production SDK/data inventory, obtain a permanent public URL, and link that URL inside the app and Google Play Console.

- **Effective date:** 9 September 2026
- **Developer/Data controller:** [DEVELOPER OR LEGAL NAME]
- **Privacy and support contact:** [SUPPORT EMAIL]
- **App package:** `com.shipaton.quotesofwisdom`

myQuote is a local-first Android application. This policy explains what information the app stores on your device, what is processed by service providers to offer purchases and speech, and the choices available to you.

## Summary

- myQuote does not contain advertising or a third-party analytics SDK.
- The app does not request access to precise or approximate device location.
- Quotes, favorites, themes, streaks, trial state, and reminder preferences are primarily stored on your device.
- RevenueCat processes an anonymous App User ID and purchase/entitlement information so paid Pro access can work.
- Google Play processes production payments; the direct judge build uses RevenueCat Test Store and does not charge real money.
- If you choose a network-capable Text-to-Speech voice, the installed speech provider may process quote text under its own privacy terms.

## Information stored locally

The app stores information needed to provide its features, including:

- favorite quotes and quote-deck progress;
- selected theme;
- streak and last-opened-day state;
- trial/grace/access timing state;
- Text-to-Speech engine, voice, and speed preferences;
- reminder enablement and selected reminder time.

This information is not sent to a custom myQuote backend because the app does not operate one. It remains on the device unless a feature described below requires a service provider. Some ordinary app preferences—including trial timing—may be included in Android's device backup if the user enables Android backup. Local notification records are excluded from app backup where configured.

RevenueCat separately caches its App User ID and `CustomerInfo` on the device as part of the RevenueCat SDK. myQuote relies on RevenueCat's entitlement cache and refresh behavior rather than maintaining a second local Pro-entitlement database.

## Purchases and Pro entitlement

myQuote uses RevenueCat to load product offerings, process purchase results, restore purchases, and determine whether the `pro_access` entitlement is active.

myQuote has no account or login system and does not construct its own RevenueCat customer identifier from Android device identifiers. The RevenueCat SDK is configured without a custom App User ID, so RevenueCat creates and caches a random anonymous App User ID for the installation.

After an uninstall/reinstall or installation on another device, the anonymous App User ID may change. The app provides Restore Purchases so purchases associated with the same store account can be recovered. The RevenueCat project should use **Transfer to new App User ID** restore behavior for this no-login model.

RevenueCat receives the anonymous App User ID together with information needed to provide and troubleshoot offerings, transactions, subscriptions, and entitlements. RevenueCat may also process app configuration, locale/currency, network, and service-request information as described in its own policy.

In a production Google Play build, Google Play handles payment credentials and payment processing. myQuote does not receive or store full payment-card details. The judge/QA build uses RevenueCat Test Store and makes no real-money charge.

Service-provider policies:

- RevenueCat Privacy Policy: <https://www.revenuecat.com/privacy/>
- Google Privacy Policy: <https://policies.google.com/privacy>
- Google Play Terms: <https://play.google.com/about/play-terms/>

## Text-to-Speech

myQuote uses Android's installed Text-to-Speech system. When narration is requested, the current quote text is provided to the Text-to-Speech engine selected on the device.

- A local/on-device voice may process the text entirely on the device.
- A network-required voice may send the text to that engine provider.
- The available engines and their privacy behavior are controlled by the device and the installed provider, not by myQuote.

Users can avoid network speech processing by selecting a local voice where available or by not using narration.

## Sharing

When a user taps Share, myQuote sends the current quote and author attribution to Android's system share sheet. The content is provided only to the app/service the user chooses. That recipient processes the shared content under its own privacy terms.

## Notifications

Daily reminders are scheduled locally with Android system services. On Android 13 and newer, the app requests notification permission. Reminder preferences remain local. Users can disable reminders inside the app or through Android system settings.

## Location

myQuote does not request Android location permission and does not use GPS or nearby-device signals to determine location. Store providers may select currency or regional availability from the user's store country, and network service providers may process an IP address under their own policies; the app does not convert that information into a location profile.

## Retention and deletion

Local app data remains until it is overwritten through normal use, cleared through Android settings, or removed by uninstalling the app. Android backup may retain eligible preferences according to the user's Google/Android backup settings.

RevenueCat and Google retain transaction, subscription, entitlement, and related service records according to their legal obligations and retention policies. To request access to or deletion of information associated with myQuote, contact [SUPPORT EMAIL]. Some transaction records may need to be retained for legal, fraud-prevention, accounting, or platform-compliance reasons.

## Security

myQuote disables cleartext network traffic and relies on Android and its service providers for encrypted network transport. The app minimizes external processing by bundling its quote corpus and avoiding a custom account/backend system. No method of storage or transmission is completely secure, but the developer uses reasonable measures appropriate to the information processed.

## Children's privacy

[CONFIRM TARGET AUDIENCE AND INSERT THE APPROPRIATE CHILDREN'S PRIVACY STATEMENT BEFORE PUBLICATION.]

## Changes to this policy

This policy may be updated when app features, service providers, or legal requirements change. The effective date at the top will be updated when a revised policy is published.

## Contact

Questions or privacy requests may be sent to:

- [DEVELOPER OR LEGAL NAME]
- [SUPPORT EMAIL]
- [OPTIONAL POSTAL ADDRESS OR BUSINESS CONTACT PAGE]
