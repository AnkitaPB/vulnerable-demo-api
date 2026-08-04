# Project Standards for Claude

## Stack
- Java 17, Spring Boot 3.3.x, Maven

## Coding standards
- Controllers stay thin: no business logic, only request/response handling
- All business logic lives in the `service` package
- Validate all incoming request parameters/bodies
- Never concatenate user input directly into SQL queries — use parameterized
  queries / prepared statements
- Never pass user input directly to a shell command or file path without
  sanitization or an allowlist
- Never log secrets, passwords, or tokens
- Never hardcode credentials or API keys in source or properties files —
  use environment variables or a secrets manager
- Prefer constructor injection over field injection
- Password hashing must use a modern algorithm (bcrypt/argon2), never MD5/SHA1
- Every endpoint that returns or modifies data needs an authorization check

## Review priorities (in order)
1. Security (injection, auth, secrets, path traversal)
2. Correctness / bugs
3. Missing test coverage
4. Readability, naming, and dead code
