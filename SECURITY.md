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

Server-side checklist (what MUST be implemented on server)
- Rate limiting per IP and per account for authentication endpoints.
- Exponential lockout / exponential delay server-side for repeated failed login attempts.
- Strict input validation and parameterized queries (no SQL concatenation).
- Validate file MIME type and size; store files safely and enforce access checks.
- Use HTTPS, secure cookies, and short-lived access tokens (with refresh tokens and revocation support).

Developer commands & quick checks
- Find risky logs in the project:
```bash
grep -R "Log\.\|android.util.Log" -n app || true
```
- Build a release locally (checks for accidental debug features):
```bash
./gradlew assembleRelease
```
- Run a dependency check (example using OWASP Dependency-Check CLI):
```bash
dependency-check --project "SIMOPKL" --scan ./app
```

How to review a PR for security (short checklist for reviewers)
- No tokens or secrets in the diff.
- No `Log.d` printing request/response bodies or tokens.
- Network logging in `RetrofitClient` is redacted and controlled by build type.
- Token storage uses an encrypted store (Keystore/EncryptedSharedPreferences) or a secure alternative.
- Server error messages are not printed verbatim in client logs; only safe messages.
- File upload size and type are validated client-side and re-validated on the server.

Frequently asked questions (FAQ, simple answers)
Q: Is client-side sanitization enough to stop attackers?
A: No. Client-side sanitization improves UX and reduces accidental issues. The server must validate and sanitize — the server is the real authority.

Q: Why not log full network bodies while debugging?
A: Because logs can leak tokens or private data. If you absolutely need full logs, enable them only on a local developer machine, scrub/redact secrets, and never ship those logs.

Q: Should I store tokens in plain SharedPreferences?
A: No. Use EncryptedSharedPreferences or another Keystore-backed solution. Plain SharedPreferences can be read on a rooted device.

Next steps (suggested improvements)
- Add a small `AppLog` wrapper and replace remaining `Log.*` usages automatically (I can implement this for you).
- Add unit tests for `InputSanitizer` to ensure expected behavior.
- Add a debug build variant that sets a flag for safe redacted logging.
- Document the server-side requirements in a short `SERVER_SECURITY.md` and coordinate with backend developers.

If you want, I can:
- Add a short developer-focused `SECURITY.md` section to the `README.md`.
- Implement the `AppLog` wrapper and replace remaining `Log.*` usages.
- Add unit tests for `InputSanitizer` and a small Robolectric test verifying `TokenManager` stores and returns token values (without printing them).

If you'd like me to make any of the code examples into real files in the repo (for example, create `AppLog.kt` and replace `Log` calls), tell me which one to start with and I'll implement it and push a commit.

---
Generated: 2026-01-06
