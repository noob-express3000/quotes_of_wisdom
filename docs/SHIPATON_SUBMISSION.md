# myQuote — Shipaton Submission and Judge Guide

_Updated: 2026-09-09. Re-check the live competition form before submitting._

## Submission identity

- **Project:** myQuote
- **Platform:** Android 6.0+
- **Current status:** working Android release candidate; RevenueCat Test Store validated; Google Play production configuration/testing pending developer-account access
- **Repository:** <https://github.com/noob-express3000/quotes_of_wisdom>
- **Judge release:** <https://github.com/noob-express3000/quotes_of_wisdom/releases/tag/v1.0.2>
- **Judge APK:** `myQuote-v1.0.2-judge.apk`
- **Judge APK SHA-256:** `3439768959570d5962edc845f72dce0ee082b6e0f90b9913035952585e6ad053`
- **Source commit:** `9facebb8dc647048802cff2da392f319b4e24ee8`
- **Android CI run:** `34399275732`
- **Source license:** Apache-2.0; historical quote texts documented separately in `NOTICE`
- **Business model:** 30-day app-controlled trial, weekly/monthly subscription, or lifetime Pro
- **Billing demonstration:** RevenueCat Test Store; no real-money charge
- **Technical package:** `com.shipaton.quotesofwisdom` retained for compatibility

### One-line pitch

One good thought, spoken beautifully, inside a focused daily ritual that works without an app account or content backend.

### Short description

myQuote is a local-first Android app that turns a carefully verified quote library into a focused daily ritual. It combines no-repeat browsing, favorites, streaks, reminders, Android Text-to-Speech, 100 strict three-color themes, and a RevenueCat upgrade flow. The app has no app login, ads, analytics SDK, or custom backend. RevenueCat supplies localized product information and the single `pro_access` entitlement across weekly, monthly, and lifetime products.

### Current release status

myQuote is a **working Android release candidate/judge build**, not a production Google Play release. Version 1.0.2 is published as a permanent GitHub judge release and was built from an exact source commit that passed the repository's quote validation, tests, lint, Debug/QA build paths, Release AAB-path validation, stable-signature verification, and artifact upload.

The judge APK uses RevenueCat Test Store. A production Google Play release still requires Play Console access and product configuration, the production RevenueCat Google Play SDK key, production signing, hosted policy URLs, and billing tests through a Play testing track. The remaining gap is store publication/production validation rather than building an unfinished prototype.

### What RevenueCat enables

- one `pro_access` entitlement across weekly, monthly, and lifetime products;
- Current Offering retrieval and real formatted prices;
- purchase and restore flows;
- entitlement updates during the running app;
- cached `CustomerInfo` for restart/offline entitlement behavior;
- Test Store evaluation without charging judges.

The app does not branch Pro features by product ID. Every valid paid product grants the same entitlement.

Because myQuote has no login system, RevenueCat is configured without a custom App User ID and generates an anonymous ID for the installation. The RevenueCat project should use **Transfer to new App User ID** restore behavior so a user can recover purchases after reinstalling or moving to another device.

## Pricing and trial rationale

RevenueCat's State of Subscription Apps 2025 report influenced the commercial structure, but the exact prices were product decisions rather than values copied from the report.

- RevenueCat reported the strongest median trial-to-paid conversion for **17–32 day trials (45.7%)**, which supported using a 30-day trial for a habit-oriented product rather than a very short trial.
- RevenueCat reported broad use of hybrid monetization, including apps combining subscriptions with one-time/lifetime purchases, which supported offering Lifetime alongside recurring plans.
- The report also showed materially stronger retention and lower refund behavior at lower price points, which supported deliberately inexpensive pricing for an app without an expensive content backend.

The resulting catalog targets are:

- Weekly: **$0.99**
- Monthly: **$2.99**
- Lifetime: **$29.99**

These exact prices are intentionally low product choices, not claimed as RevenueCat benchmark recommendations.

## Judge installation

1. Use an Android 6.0 or newer device.
2. Download `myQuote-v1.0.2-judge.apk` from the permanent `v1.0.2` GitHub Release.
3. If Android reports a signing conflict with an older test build, uninstall the older build once.
4. Allow installation from the browser/file manager when Android prompts.
5. Install and open **myQuote**.
6. Keep internet access available for RevenueCat offerings, purchase, and restore tests.

The APK uses RevenueCat Test Store. Test purchases are simulated and do not charge real money. The quote corpus and ordinary quote browsing are bundled/local-first.

## Three-minute demo script

### 0:00–0:25 — The product

- Open myQuote on a strong quote and let narration begin.
- State the problem: most quote apps are noisy feeds or require a content service; this one is a focused local daily ritual.
- Tap Next and Replay to establish the core loop.

### 0:25–0:55 — Retention without clutter

- Favorite a quote and briefly show Favorites.
- Show attributed sharing.
- Tap the streak once to show the palette-aware flames and synthesized horn.
- Show the daily-reminder control.

### 0:55–1:25 — Visual and speech depth

- Open Settings.
- Show the theme library and explain the exact three-color 60/30/10 constraint.
- Briefly show Pro engine, voice, voice-download, font, and speed controls.
- Return Home and demonstrate the same interface in a visually different theme.

### 1:25–2:15 — RevenueCat flow

- Open the paywall and show live Test Store prices.
- Tap Info, close it, and identify the weekly/monthly/lifetime options.
- Complete a Lifetime Test Store purchase.
- Show the `PRO` label and unlocked controls.
- Force-close/reopen or use a prepared cut to prove Pro persists.
- Mention Restore Purchases and RevenueCat's cached entitlement state.

### 2:15–2:45 — Engineering proof

- Show the public repository, green GitHub Actions run, and quote-verification ledger.
- State: Kotlin, Jetpack Compose, DataStore, Android TTS, AlarmManager, RevenueCat; no app account or custom backend.
- Mention physical Android testing.

### 2:45–3:00 — Close

- Return to the best visual theme/quote.
- End with the myQuote name, Gothic Q, and public repository link.

## Suggested technical highlights

- **RevenueCat-native identity:** RevenueCat-generated anonymous App User IDs because there is no login system.
- **Restore path:** the no-login model pairs with **Transfer to new App User ID** restore behavior.
- **Entitlement authority:** RevenueCat `CustomerInfo` and its SDK cache rather than a second Boolean entitlement database.
- **Defensive purchase handling:** a transaction is not reported as Pro success unless returned `CustomerInfo` contains active `pro_access`.
- **Real pricing:** paywall strings come from RevenueCat/store products; no location lookup or hardcoded production currency fallback.
- **Local-first resilience:** quote corpus, favorites, streaks, themes, and reminders work without a custom service.
- **Content integrity:** every production quote is covered by curation, provenance, and verification records.

## Screenshot set

1. strongest Home theme with a short quote;
2. contrasting light Home theme;
3. streak flame surge;
4. Settings theme/font library;
5. TTS engine/voice controls;
6. paywall with Test Store prices;
7. Pro access info card;
8. Android daily notification showing myQuote branding;
9. public GitHub Actions success and quote-verification proof.

Use the location-neutral Test Store build for public capture. Avoid status-bar/location indicators, personal notifications, account names, local file paths, RevenueCat dashboard customer records, and production credentials.

## Release record

```text
Tag: v1.0.2
Title: myQuote v1.0.2
Source commit: 9facebb8dc647048802cff2da392f319b4e24ee8
Android CI run: 34399275732
APK: myQuote-v1.0.2-judge.apk
APK SHA-256: 3439768959570d5962edc845f72dce0ee082b6e0f90b9913035952585e6ad053
Signer certificate SHA-256: 72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03
Minimum Android version: Android 6.0 (API 23)
Billing: RevenueCat Test Store; no real-money charge
```

Historical `v1.0.0` and `v1.0.1` releases retain their original names/assets for provenance; the current product brand is myQuote.

## Final submission links

- Public repository: <https://github.com/noob-express3000/quotes_of_wisdom>
- Judge GitHub Release: <https://github.com/noob-express3000/quotes_of_wisdom/releases/tag/v1.0.2>
- Demo video: `<fill>`
- BuildInPublic post/thread: `<fill>`
- Privacy policy: `<fill after hosting/finalization>`
- Support contact: `<fill>`
