SERVER_SECURITY.md — Server-side hardening for SIMOPKL

This document lists mandatory server-side controls and best practices. The mobile app applies client-side UX-level protections, but the server must be the authority for security controls.

1) Authentication & tokens
- Use short-lived access tokens (e.g., JWT with expiry) and refresh tokens for long-lived sessions.
- Implement token revocation (blacklist) for compromised tokens.
- Validate tokens on every request and check signature and expiry.
- Do not accept tokens from query parameters. Use Authorization header: `Authorization: Bearer <token>`.

2) Rate limiting and brute-force protection
- Enforce rate limiting by IP and by account for login and token endpoints.
- Implement exponential backoff for repeated failed logins.
- Consider CAPTCHA after N failed attempts.
- Log suspicious activity and generate alerts for multiple account failures from one IP.

3) Input validation and output encoding
- Validate all inputs server-side: types, lengths, ranges, and formats.
- Use parameterized queries / prepared statements to prevent SQL injection.
- Escape or encode output when returning HTML; prefer JSON APIs.

4) File uploads
- Validate MIME type and file size server-side (do not trust client).
- Rename files on storage to avoid path traversal or filename disclosure.
- Store files outside the web root and serve via signed, time-limited URLs if needed.
- Scan uploaded files for malware if appropriate.

5) Authorization & RBAC
- Verify that every action is authorized (e.g., user can't approve their own registration).
- Implement role-based access control and enforce it consistently.

6) Transport security
- Enforce TLS 1.2+ on all endpoints.
- Use HSTS and secure cookies with `Secure` and `HttpOnly` flags.

7) Logging & monitoring
- Avoid logging full tokens or PII. Log event-types and identifiers instead.
- Integrate with central logging and alerting (Sentry, ELK, Datadog) and redact sensitive fields.

8) Secrets & configuration
- Keep secrets out of source control. Use secure secret stores or environment variables in CI/CD.
- Rotate credentials and keys regularly.

9) Misc
- Keep dependencies up-to-date and apply security patches promptly.
- Use CSP and other headers where relevant for web components.

Recommended implementation checklist
- [ ] Login endpoint enforces rate limit & exponential delay.
- [ ] File upload endpoints validate type/size and store files securely.
- [ ] All DB access uses parameterized queries / ORM safe methods.
- [ ] Issued tokens have expiry and revocation endpoint.

Contact
Coordinate with backend engineers to implement these measures and confirm endpoints (paths, required headers, response shapes).

