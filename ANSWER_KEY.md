# Answer Key — planted issues

Use this to score how thorough the automated review was. Don't peek before
running the review, or it defeats the point.

## Security vulnerabilities

| # | File | Issue |
|---|------|-------|
| 1 | `UserService.java` → `searchUsersByName()` | SQL injection — raw string concatenation into query |
| 2 | `UserService.java` → `login()` | SQL injection — second raw-concatenated query |
| 3 | `UserService.java` → `login()` | Hardcoded credential check (`admin` / `SuperSecret123!`) |
| 4 | `UserService.java` → `login()` | Logs the raw plaintext password |
| 5 | `UserService.java` → `hashPassword()` | MD5 used for password hashing (broken/weak) |
| 6 | `UserService.java` → `readUserFile()` | Path traversal — filename used unsanitized in file path |
| 7 | `UserService.java` → `readUserFile()` | Leaks internal file-system error details to caller |
| 8 | `UserService.java` → `pingHost()` | Command injection — host string passed straight to shell `exec` |
| 9 | `DemoController.java` → class annotation | `@CrossOrigin(origins = "*", allowCredentials = "true")` — dangerous combo, wide-open CORS with credentials |
| 10 | `DemoController.java` → `/admin/sessions` | No authentication/authorization on an admin endpoint |
| 11 | `DemoController.java` → `/login` | Returns the entire session token map to the client |
| 12 | `DemoController.java` → `/crash` | Leaks exception details/stack trace to the client |
| 13 | `application.properties` | Hardcoded DB password committed to source |
| 14 | `application.properties` | Hardcoded third-party API key committed to source |
| 15 | `application.properties` | Hardcoded "master password" committed to source |

## Code quality / standards violations

| # | File | Issue |
|---|------|-------|
| 16 | `UserService.java` | Field injection (`@Autowired` on field) instead of constructor injection |
| 17 | `UserService.java` | Public mutable static field (`sessionTokens`) — shared, non-thread-safe global state |
| 18 | `UserService.java` | Magic numbers with no explanation (`max_retry_count`, `Timeout`) |
| 19 | `UserService.java` | Inconsistent naming — `max_retry_count` (snake_case) vs `Timeout` (PascalCase) vs Java convention (camelCase) |
| 20 | `UserService.java` → constructor | Confusing dual-initialization: field set to `null` then overwritten by injection |
| 21 | `UserService.java` → `hashPassword()` | Swallows exception and returns `null` instead of failing loudly |
| 22 | `UserService.java` → `unusedLegacyHelper()` | Dead/unused code left in the file |
| 23 | `DemoController.java` | Field injection, no access modifier convention on `userService` |
| 24 | `DemoController.java` → `/legacyPing` | Unused parameter (`unused_param`), inconsistent naming (`legacy_ping` method name) |
| 25 | Whole project | Zero test coverage |
| 26 | Whole project | No input validation annotations anywhere |

**26 planted issues total.** A strong review should catch most of the
security items (1–15) at minimum — those are the highest priority per
`CLAUDE.md`. The style/quality items (16–26) are a good secondary signal
for how deep the review goes.
