# ⚠️ vulnerable-demo-api — INTENTIONALLY INSECURE, TEST-ONLY

**This project is deliberately broken.** It exists for exactly one purpose:
to test whether an automated code review tool (in this case, Claude's
GitHub PR review action) actually catches known bad patterns.

**Do not deploy this anywhere. Do not use any of this code as a reference
for how to write Spring Boot apps.** See `task-api` for a clean example
instead.

## How to use it

1. Push this to its own GitHub repo (or a throwaway branch of an existing
   one) — same steps as any repo:
   ```bash
   cd vulnerable-demo-api
   git init
   git add .
   git commit -m "Initial commit (intentionally vulnerable test fixture)"
   git branch -M main
   git remote add origin https://github.com/<you>/<repo>.git
   git push -u origin main
   ```
2. Install the Claude GitHub App and add the `ANTHROPIC_API_KEY` secret,
   same as before.
3. Open a PR against `main` (e.g. branch off, tweak a comment, push, open
   PR) so `claude-review.yml` triggers.
4. Compare what Claude flags against `ANSWER_KEY.md` below.

## What's intentionally wrong

See `ANSWER_KEY.md` for the full list of planted issues — check it only
*after* you've seen what the automated review caught, so you get an
honest read on how thorough it is.
