# Sprint 1 — Authentication foundation

- [x] Spring Boot, Maven, Flyway, PostgreSQL and React/TypeScript project structure.
- [x] V1 migration for users, workspaces and workspace memberships.
- [x] Registration, case-insensitive duplicate handling, BCrypt hashing, JWT login and protected profile.
- [x] Registration, login and profile screens wired to the real API.
- [x] PostgreSQL integration tests: account lifecycle, duplicate email, invalid credentials, missing/invalid token and invalid input.
- [x] TypeScript production build and same-origin API smoke test via Vite.
- [x] Dockerfiles, Compose configuration and GitHub Actions workflow added.
- [ ] Run Compose from a clean checkout and confirm Nginx routing; Docker was not available in the build sandbox.
- [ ] Create a GitHub repository, push the folder, and confirm CI passes remotely.
- [ ] Run a browser-based account-flow test, including keyboard navigation and mobile viewport.

Local verification: Maven `verify` passed (5 tests, PostgreSQL 17); frontend build passed; the HTTP smoke test passed. The API smoke test does not substitute for a browser UI test.
