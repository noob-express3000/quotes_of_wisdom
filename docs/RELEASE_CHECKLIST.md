# Quotes of Wisdom — Release Checklist

This file separates the direct judge APK from the eventual Google Play release. A checked code/CI item is not a substitute for a physical-device test.

## A. Shipaton / judge release

### Integrated baseline

- [x] Production-hardening and entitlement-startup fixes merged to `main`.
- [x] Quote validation, unit tests, lint, Debug APK, QA APK, and Release bundle path are green at commit `be6505c590ddbe43ac09be7e832ffcac09b1c9d5`.
- [x] CI Debug and QA artifacts use one verified stable test signer.
- [x] QA uses RevenueCat Test Store rather than production billing.
- [x] Test Store prices come from RevenueCat; no app-side fallback/location logic exists.
- [ ] Complete the final stable-signer QA smoke test on a physical device.
- [ ] Repeat the critical smoke path on both Samsung and itel if time permits.

### Public-source gate

- [x] License original project code/material under Apache-2.0 in `LICENSE`.
- [x] Scope the historical quote corpus separately in `NOTICE` and the provenance/rights records under `docs/`.
- [ ] Review the complete repository for credentials, signing material, private names, local paths, and accidental location evidence.
- [ ] Confirm only the public RevenueCat Test Store SDK key and the deliberately public **test-only** CI keystore are present.
- [ ] Fill the owner/contact/jurisdiction placeholders in `docs/PRIVACY_POLICY_DRAFT.md` and `docs/TERMS_OF_USE_DRAFT.md`.
- [ ] Make the GitHub repository public only after the preceding review.

### Permanent judge artifact

- [ ] Create a GitHub Release from the final public commit (recommended tag: `v1.0.0-judge`).
- [ ] Rename the asset clearly, for example `Quotes-of-Wisdom-v1.0.0-judge.apk`.
- [ ] Attach the stable CI-produced QA APK; do not rebuild it locally with a different signer.
- [ ] Publish the APK SHA-256.
- [ ] Publish the source commit and CI run URL.
- [ ] Publish the stable CI certificate SHA-256.
- [ ] State that the APK uses RevenueCat Test Store and makes no real-money charge.
- [ ] State that internet is required for offerings/purchase/restore, while bundled quotes and local features remain local-first.
- [ ] Keep the GitHub Release permanent; Actions artifacts are temporary.

### Device smoke

- [ ] Remove any APK signed by the older transient CI key before the first stable-signer install.
- [ ] Confirm clean install and cold launch.
- [ ] Confirm the launch paywall appears for non-Pro and remains dismissible during Trial.
- [ ] Tap Info, dismiss by its Close control, reopen it, and dismiss by tapping outside.
- [ ] Tap the paywall Close control repeatedly and confirm it always closes.
- [ ] Confirm Weekly, Monthly, and Lifetime show `$0.99`, `$2.99`, and `$29.99` in Test Store.
- [ ] Confirm unavailable/loading plans cannot be purchased and never show invented prices.
- [ ] Complete a Lifetime test purchase and confirm `PRO` appears.
- [ ] Force-stop and reopen online; confirm Pro remains.
- [ ] Force-stop, disable connectivity, and reopen; confirm a previously confirmed Pro user is not downgraded or shown the paywall.
- [ ] Restore Purchases for the same stable-signer Test Store identity.
- [ ] Confirm billing failure shows Retry and Retry refreshes offerings/entitlement data.
- [ ] Confirm Replay, Next, TTS, Favorite, Share, settings, and system Back.
- [ ] Confirm streak flames/horn and Pro spin do not crash or mutate access/streak state.
- [ ] Confirm notification grant/denial and one scheduled reminder.
- [ ] Confirm acceptable performance on the low-end itel device.

### Submission assets

- [ ] Record a concise demo following `docs/SHIPATON_SUBMISSION.md`.
- [ ] Capture clean portrait screenshots in a location-neutral QA theme/build.
- [ ] Capture Trial, paywall, Pro, theme, TTS, streak/flames, and notification proof.
- [ ] Add the public repository URL and GitHub Release/APK URL to the submission.
- [ ] Add BuildInPublic post links and identify at least one change made from public feedback.
- [ ] Re-check the official Shipaton form immediately before submission for changed fields or limits.

## B. Google Play production release

The judge QA APK must not be uploaded to Google Play.

### Release configuration and signing

- [ ] Create/confirm the Google Play Console app with package `com.shipaton.quotesofwisdom`.
- [ ] Increment `versionCode` for every uploaded bundle.
- [ ] Confirm `versionName` matches the public release.
- [ ] Supply the real RevenueCat Google Play public Android SDK key with `-PREVENUECAT_API_KEY=goog_...`.
- [ ] Add secure upload-signing injection outside the repository; the repository currently has no production release signing configuration.
- [ ] Keep the upload keystore and passwords out of source control and logs.
- [ ] Generate a signed Release AAB and verify its certificate.
- [ ] Upload to Internal or Closed testing before production.

### RevenueCat and Play Billing

- [ ] Create and activate `qow_weekly`, `qow_monthly`, and `qow_lifetime` in Google Play.
- [ ] Confirm `qow_weekly` and `qow_monthly` billing periods and auto-renewal configuration.
- [ ] Confirm the `pro_access` entitlement is active in RevenueCat.
- [ ] Attach all three Play products to RevenueCat's Current Offering using the expected package types.
- [ ] Confirm localized prices and billing periods match the live paywall in multiple Play countries.
- [ ] Test successful purchase, user cancellation, pending purchase, restore, refund/revocation, expiry, and offline launch through Google Play sandbox.
- [ ] Confirm only a successful inactive RevenueCat response removes previously confirmed Pro.
- [ ] Confirm entitlement loss resets the free reminder time to 09:00.

RevenueCat launch reference: <https://www.revenuecat.com/docs/test-and-launch/launch-checklist>

### Subscription disclosure and privacy

- [ ] Show price, billing period, automatic-renewal/cancellation language, and material subscription terms before purchase.
- [ ] Add tappable Terms and Privacy links on the production paywall without changing the three-color design system.
- [ ] Host a final public HTML privacy-policy URL; a repository draft alone is not sufficient.
- [ ] Finalize and host the Terms of Use at a permanent public URL.
- [ ] Link the final privacy policy both in-app and in Play Console.
- [ ] Link Terms from the production paywall.
- [ ] Replace every placeholder in `docs/PRIVACY_POLICY_DRAFT.md`.
- [ ] Replace every placeholder in `docs/TERMS_OF_USE_DRAFT.md`.
- [ ] Complete Google Play Data safety from the final SDK/data inventory using `docs/DATA_SAFETY_DRAFT.md` only as a working map.
- [ ] Account for the opaque RevenueCat customer identifier under Device or other IDs as applicable.
- [ ] Account for Android backup behavior and network-capable TTS providers accurately.
- [ ] Add a monitored support contact.
- [ ] Implement and test a process that can map a user's deletion request to the correct opaque RevenueCat customer record.
- [ ] Confirm content rating, target audience, ads, app access, and data-deletion declarations.
- [ ] Verify store listing/trial/pricing language matches Play and RevenueCat.

Google Play policy references:

- Subscriptions and user data: <https://support.google.com/googleplay/android-developer/answer/17517561?hl=en>
- Data safety: <https://support.google.com/googleplay/android-developer/answer/10787469?hl=en>

### Production device acceptance

- [ ] Test the Play-installed build on Samsung and itel devices.
- [ ] Verify launcher Q in normal, round, and themed-icon modes.
- [ ] Verify cold launch, background/resume, rotation, process recreation, system Back, and update install.
- [ ] Verify local and network TTS voices, missing voice data, Replay, and engine switching.
- [ ] Verify notifications after permission grant, denial, reboot, clock change, timezone change, and app update.
- [ ] Verify Trial, Grace, Locked, Pro, cancellation/expiry, and restored states.
- [ ] Run TalkBack, large-text, display-scaling, contrast, and reduced-animation checks.
- [ ] Confirm the paywall remains operable at supported font/display scales.

### Final commands and release record

```bash
python3 tools/validate_production_quotes.py app/src/main/assets/quotes.json
./gradlew :app:testDebugUnitTest :app:lintRelease
./gradlew :app:bundleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

- [ ] Confirm CI is green at the exact release commit.
- [ ] Confirm the final AAB is production-signed outside the repository's test-only path.
- [ ] Install the Play-generated universal/split APK from the testing track rather than treating the local QA APK as production proof.
- [ ] Record release commit, version code/name, AAB SHA-256, signing certificate, RevenueCat project/environment, track, and rollout percentage.
- [ ] Save rollback/support notes before starting production rollout.
