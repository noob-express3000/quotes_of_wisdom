# Quotes of Wisdom — Shipaton Submission and Judge Guide

_Updated: 2026-09-08. Re-check the live competition form before submitting._

Official references checked while preparing this draft:

- Next Gen Award: <https://shipaton.com/next-gen>
- BuildInPublic Award: <https://www.shipaton.com/categories/build-in-public-award>

## Submission identity

- **Project:** Quotes of Wisdom
- **Platform:** Android 6.0+
- **Repository:** <https://github.com/noob-express3000/quotes_of_wisdom>
- **Source license:** Apache-2.0; historical quote texts documented separately in `NOTICE`
- **Business model:** 30-day app-controlled trial, weekly/monthly subscription, or lifetime Pro
- **Billing demonstration:** RevenueCat Test Store; no real-money charge

### One-line pitch

One good thought, spoken beautifully, inside a focused daily ritual that works without an account or content backend.

### Short description

Quotes of Wisdom is a local-first Android app that turns a carefully verified quote library into a calm daily ritual. It combines no-repeat browsing, favorites, streaks, reminders, Android Text-to-Speech, 100 strict three-color themes, and a RevenueCat upgrade flow. The app has no login, ads, analytics SDK, or custom backend. RevenueCat supplies localized product information and the single `pro_access` entitlement across weekly, monthly, and lifetime products.

### What RevenueCat enables

- one `pro_access` entitlement across weekly, monthly, and lifetime products;
- Current Offering retrieval and real formatted prices;
- purchase and restore flows;
- entitlement updates during the running app;
- cached `CustomerInfo` for restart/offline entitlement behavior;
- Test Store evaluation without charging judges.

The app does not branch Pro features by product ID. Every valid paid product grants the same entitlement.

Because the app has no login system, RevenueCat is configured without a custom App User ID and generates an anonymous ID for the installation. The RevenueCat project should use **Transfer to new App User ID** restore behavior so a user can recover purchases after reinstalling or moving to another device.

## Judge installation

The GitHub Release should contain a stable CI-produced file named similar to:

```text
Quotes-of-Wisdom-v1.0.1-judge.apk
```

1. Use an Android 6.0 or newer device.
2. If an older Quotes of Wisdom test build is installed and Android reports a signing conflict, uninstall it once.
3. Download the APK from the permanent GitHub Release.
4. Allow installation from the browser/file manager when Android prompts.
5. Install and open the app.
6. Keep internet access available for RevenueCat offerings, purchase, and restore tests.

The APK uses RevenueCat Test Store. Test purchases are simulated and do not charge real money. The quote corpus and ordinary quote browsing are bundled/local-first.

## Three-minute demo script

### 0:00–0:25 — The product

- Open on a strong quote and let narration begin.
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
- State: Kotlin, Jetpack Compose, DataStore, Android TTS, AlarmManager, RevenueCat; no account or custom backend.
- Mention physical Android testing.

### 2:45–3:00 — Close

- Return to the best visual theme/quote.
- End with the pitch and the public repository link.

Do not spend demo time scrolling through every theme or reading every feature label. Show the product loop, the purchase proof, and the engineering decisions.

## Suggested technical highlights

- **RevenueCat-native identity:** the app uses RevenueCat-generated anonymous App User IDs because there is no login system.
- **Restore path:** the no-login model pairs with RevenueCat's **Transfer to new App User ID** restore behavior.
- **Entitlement authority:** the app relies on RevenueCat `CustomerInfo` and its SDK cache rather than maintaining a second Boolean entitlement database.
- **Defensive purchase handling:** a completed transaction is not reported as Pro success unless the returned `CustomerInfo` contains active `pro_access`.
- **Real pricing:** paywall strings come from RevenueCat/store products; there is no location lookup or hardcoded production currency fallback.
- **Local-first resilience:** the quote corpus, favorites, streaks, themes, and reminders work without a custom service.
- **Content integrity:** every production quote is covered by curation, provenance, and verification records.

## BuildInPublic evidence prompts

Link the actual posts rather than merely claiming the project was built publicly.

- initial problem and product direction;
- early screenshots and the 60/30/10 feedback loop;
- changes made from public feedback, such as accent-emphasis correction or simplified paywall cards;
- performance and signing/update discoveries;
- RevenueCat entitlement/restore hardening and the verified fix;
- final demo/release post with repository and APK links.

Suggested sentence structure:

> Feedback exposed **[specific problem]**. I changed **[specific behavior]**, verified it in **[build/test]**, and the result was **[observable improvement]**.

## Screenshot set

1. strongest Home theme with a short quote;
2. contrasting light Home theme;
3. streak flame surge;
4. Settings theme/font library;
5. TTS engine/voice controls;
6. paywall with Test Store prices;
7. Pro access info card;
8. Android daily notification;
9. public GitHub Actions success and quote-verification proof.

Use the location-neutral Test Store build for public capture. Avoid status-bar/location indicators, personal notifications, account names, local file paths, RevenueCat dashboard customer records, and production credentials.

## GitHub Release template

- **Tag:** `v1.0.1`
- **Title:** `Quotes of Wisdom v1.0.1`
- **Asset:** `Quotes-of-Wisdom-v1.0.1-judge.apk`

Include:

```text
Source commit: <full commit SHA>
CI run: <GitHub Actions URL>
APK SHA-256: <sha256>
Signer certificate SHA-256: 72:94:42:18:4D:E0:36:0C:72:8F:56:CE:DA:A5:36:90:09:4A:0A:60:00:3D:C4:8E:CD:D3:7D:33:F3:7A:0B:03
Minimum Android version: Android 6.0 (API 23)
Billing: RevenueCat Test Store; no real-money charge
```

Add this compatibility note:

> If an earlier test APK was signed differently, uninstall it once before installing this release. CI judge builds use the project's stable test-only signer and can update one another normally.

## Final submission links

- Public repository: <https://github.com/noob-express3000/quotes_of_wisdom>
- Judge GitHub Release: `<fill with final release>`
- Demo video: `<fill>`
- BuildInPublic post/thread: `<fill>`
- Privacy policy: `<fill after hosting/finalization>`
- Support contact: `<fill>`
