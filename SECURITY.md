# SECURITY.md — Security Practices for KeamananSistemInformasiMobile

This document explains the security practices applied to this Android project and gives step-by-step guidance and code examples so an ordinary person (classmate or reviewer) can understand and follow them.

Why this document exists
- Mobile apps interact with servers and handle user data (email, names, tokens). Small mistakes can leak passwords, tokens, personal data, or allow account attacks.
- This file explains what we fixed and what you should keep doing, with code examples you can copy into the project.

Quick summary (plain words)
- We sanitize user input before sending it to the server so accidental scripts or HTML can't slip through the UI.
- We store sensitive tokens using Android's encrypted storage (Keystore-backed EncryptedSharedPreferences).
- We avoid logging sensitive information (no tokens, no full URLs with parameters, and no full response bodies in production).
- We show small code examples that you can reuse to keep the app safe.

Checklist (what to check in the repo)
- InputSanitizer exists and is used in login/register/profile flows: `app/src/main/java/.../utils/InputSanitizer.kt`.
- Network logging is redacted in `RetrofitClient.kt` (no URL/body prints in production).
- `TokenManager.kt` stores tokens in EncryptedSharedPreferences (Keystore-backed).
- No `Log.d` or `Log.e` entries printing tokens or full server responses in production code.

Threats we defend against (simple)
- Accidental script injection in text fields (not a full XSS vector in Compose, but avoid rendering HTML).
- Tokens printed in logs (risk if logs are shared or uploaded to crash reporting).
- Brute-force login attempts — client-side backoff improves UX; server must enforce rate limits.
- Upload of large or wrong file types — client filters obvious mistakes; server must re-check.

Important code snippets (copy/paste)

1) InputSanitizer (safe, minimal client-side sanitation)
```kotlin
object InputSanitizer {
    // Trim, collapse whitespace, remove <script>..</script> blocks and angle brackets.
    fun sanitizeForApi(value: String?): String {
        if (value == null) return ""
        var v = value.trim().replace(Regex("\\s+"), " ")
        v = v.replace(Regex("(?i)<script.*?>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
        v = v.replace("<", "").replace(">", "")
        v = v.replace(Regex("[\\x00-\\x1F\\x7F]"), "")
        return v
    }
}
```
How to use: sanitize inputs in viewmodels before sending to the API.

Example (in a login/register viewmodel):
```kotlin
val safeEmail = InputSanitizer.sanitizeForApi(email).lowercase()
val safePassword = InputSanitizer.sanitizeForApi(password)
api.login(LoginRequest(safeEmail, safePassword))
```
Note: server-side validation is still mandatory — sanitization is a UX-level safety net.

2) Retrofit interceptor: never print full URL or token
```kotlin
// addInterceptor { chain ->
//   val req = chain.request()
//   val auth = req.header("Authorization")
//   val masked = auth?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"
//   Log.d("RetrofitClient", "HTTP ${req.method} — Authorization=$masked")
//   chain.proceed(req)
// }
```
Key points:
- Do not print `req.url` or `req.body()` in production logs.
- If you enable `HttpLoggingInterceptor` in debug builds, redact Authorization with `loggingInterceptor.redactHeader("Authorization")` (if supported) and use `Level.BASIC`.

3) Example secure token storage (EncryptedSharedPreferences)
```kotlin
// TokenManager.kt (high level)
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()
val prefs = EncryptedSharedPreferences.create(
    context, "secure_auth_prefs", masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

fun saveToken(token: String) {
    prefs.edit().putString("access_token", token).apply()
}
fun getToken(): String? = prefs.getString("access_token", null)
```
Important: do not log the token value. Only log a generic message like "token saved".

4) Small redacting logger wrapper (replace direct Log.* uses)
```kotlin
object AppLog {
    private fun redact(value: String?): String = value?.replace(Regex("Bearer\\s+(.+)"), "Bearer [REDACTED]") ?: "(none)"

    fun d(tag: String, msg: String, token: String? = null) {
        val safe = if (token != null) " $msg — Authorization=${redact(token)}" else msg
        android.util.Log.d(tag, safe)
    }

    fun e(tag: String, msg: String) { android.util.Log.e(tag, msg) }
}
```
Replace `Log.d("Tag", msg)` with `AppLog.d("Tag", msg)` (pass token only in debug if needed — but prefer not to).

5) Exponential lockout (UX example and rules)
- Client-side algorithm (fast summary):
  - Keep failedAttemptCount in secure storage (EncryptedSharedPreferences) if you want persistence.
  - After each failed attempt: failedAttemptCount++, compute lockoutSeconds = base * 2^(failedAttemptCount-1) (for example base = 5 seconds).
  - Show animated countdown on the login button (use Compose animation + LaunchedEffect with a 1-second tick).
- IMPORTANT: server must *also* enforce rate limits and account lockouts. Client-only lockout is not sufficient for security.

Example (pseudo-Kotlin for counting):
```kotlin
val base = 5 // seconds
val lockout = base * (1 shl (failedAttempts-1))
// show countdown and disable the login button
```

6) File upload validation (client-side checks)
```kotlin
val type = contentResolver.getType(uri)
if (type != "application/pdf") { error("File must be PDF") }
if (file.length() > 2 * 1024 * 1024) { error("File too large") }
```
Server must re-validate everything.

Where to put configuration and secrets (safe way)
- Do not store real secrets in the repo. Use `local.properties` or CI secrets. Example `local.properties` (do not commit):
```
API_BASE_URL=https://simopkl.cloud/api/
```
- For build-time constants, use `build.gradle.kts` in the `app` module to add a `buildConfigField` in `defaultConfig`, referencing `project.findProperty("API_BASE_URL")` or a Gradle property.

Production logging policy (simple)
- Production builds: no `Level.BODY` network logging and no print of tokens or PII.
- Debug builds (developer machines): you may enable `Level.BASIC` or `Level.BODY` but redact headers using `redactHeader("Authorization")` and never commit logs containing secrets.

---

## Sensitive data storage — current implementation (detailed)

This repository implements secure storage of authentication tokens using AndroidX Security's `EncryptedSharedPreferences` backed by a `MasterKey` stored in the Android Keystore. The implementation is located at:

`app/src/main/java/com/wahyuagast/keamanansisteminformasimobile/data/local/TokenManager.kt`

The important implementation details are:

- The class creates a `MasterKey` using AES256_GCM and uses it to initialize `EncryptedSharedPreferences`.
- Tokens are stored under a single key (`access_token`) in the encrypted prefs file `secure_auth_prefs`.
- The class intentionally avoids logging the contents of the token; it only logs a generic message when saving.

Exact reasons this is secure:
- `EncryptedSharedPreferences` uses the Android Keystore to hold the encryption key (MasterKey). The key material is not directly accessible to the app process and is protected by the OS.
- Keys are generated with AES256_GCM which provides authenticated encryption.
- Data written to `EncryptedSharedPreferences` is encrypted on disk; even if an attacker obtains the file, they cannot decrypt values without the key.

Limitations and threat model (be explicit for your assignment):
- EncryptedSharedPreferences protects data at rest in normal scenarios. If the device is physically compromised or rooted and the attacker has kernel-level access, they may be able to extract keys or memory.
- On devices that do not have hardware-backed keystore, keys are still protected but only software-backed—strong but not as robust as hardware-backed (StrongBox) keys.
- If the user enables device backups and backups are not encrypted with a device-protected key, there is an additional risk — ensure backups are configured safely or exclude the file.

Operational notes (how to test & verify)
1) Verify that `TokenManager` is used throughout the app for saving and getting tokens. Search for `TokenManager(` or `saveToken(` in the repo.
2) Run the app, perform a login, then inspect the app's data directory (on a non-rooted emulator or via `adb shell`) and confirm that the file `secure_auth_prefs` exists but its contents are not readable plaintext.
   - Example (on an emulator with `adb shell`):
     - `adb shell ls -l /data/data/<package>/shared_prefs/` — file will exist but encrypted
     - `adb shell cat /data/data/<package>/shared_prefs/secure_auth_prefs.xml` — you should see unreadable, encrypted blobs (do not attempt on production device)
3) Verify logs: login should not print the token; `AppLog` prints only a generic save message.

Hardening options (optional enhancements you can mention in the assignment)
- Require user authentication (biometric or device PIN) for key usage. This can be done by creating the `MasterKey` with `setUserAuthenticationRequired(true)` and appropriate `setUserAuthenticationValidityDurationSeconds` settings. This adds UX friction (user must authenticate to access the token).
- Use StrongBox (hardware-backed) when available by requesting `setIsStrongBoxBacked(true)` when generating the key parameters — this ensures keys live in secure hardware.
- Rotate keys periodically: generate a new MasterKey and re-encrypt stored values (careful with migration logic).

Code example (TokenManager used in this project)

```kotlin
// app/src/main/java/.../TokenManager.kt
class TokenManager(context: Context) {
    private val prefs: SharedPreferences
    companion object {
        private const val FILE_NAME = "secure_auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    fun saveToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }
    fun getToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)
    fun clearToken() { prefs.edit().clear().apply() }
}
```

Suggested assignment phrasing (short paragraph you can copy)
> The app stores authentication tokens securely with AndroidX EncryptedSharedPreferences and a MasterKey stored in the Android Keystore. The key uses AES256-GCM and the pref file is encrypted at rest, preventing other apps or casual attackers from reading the token. The `TokenManager` class centralizes token handling. Note that this protects against typical app-data-extraction threats; physically compromised or rooted devices remain a more difficult threat that requires hardware-backed protections and runtime hardening.

Automated checks you can add (CI)
- Fail PRs if any file writes to plain `SharedPreferences` (grep for `getSharedPreferences\(|edit\(\)\.putString\(` without `EncryptedSharedPreferences`) — reject unless justified.
- Run a simple grep to detect raw `Log.d` calls with suspicious keywords such as `token`, `password`, or `Authorization`.

Next steps I can implement for you
- Add optional biometric protection (MasterKey user-auth) and migration logic.
- Add a small unit/instrumentation test that verifies `TokenManager.saveToken()` followed by `getToken()` returns the same value on emulator; the test should not print the token.
- Add a CI script that rejects plain SharedPreferences or unredacted log usage in the repository.

If you want any of those implemented, tell me which one (biometric hardening, tests, or CI checks) and I will implement it and push the changes.

---
Generated: 2026-01-06
