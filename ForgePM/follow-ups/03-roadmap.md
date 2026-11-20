# Delivery roadmap

| Planned period | Deliverable | Exit criteria |
|---|---|---|
| Week 1 | Authentication foundation | Register, sign in, read protected profile; test invalid input and tokens. |
| Week 2 | Workspaces, projects, memberships | Isolation and role tests described in the sprint checklist. |
| Week 3 | Issue management and collaboration | Task/Bug/Story/Epic; filtering, assignment, comments and labels; validated assignees and bounded queries. |
| Week 4 | Kanban, issue activity and dashboard | Persist drag/drop through a transition endpoint; reject invalid transitions and stale updates. Add watches and notifications. |
| Week 5 | Tested MVP | Exercise register → workspace → project → issue → assignment → transition → history on a clean install. |
| Week 6 | Phase 2: Scrum and reports | Backlog, sprint lifecycle, status/priority counts, overdue and workload reports; validate metrics and dates. |
| Weeks 7–8 | Release polish | Verify containerized setup and CI, document API and ERD, prepare demo and release notes. |
| After MVP / later | Advanced and optional AI | Scope issue links, custom workflows, integrations, and assistive features separately. |

## Issues and workflow implementation constraints

- Use a transaction-safe per-project counter for issue numbers, not `MAX()+1`. Assert uniqueness in PostgreSQL and test concurrent creation.
- Store types, statuses and transitions in tables; resolve allowed statuses against the issue's own project.
- Use JPA `@Version` and return `409` for stale writes. Commit issue changes and history in one transaction.
- Test membership boundaries on every issue and collaboration route. Do not start sprint features until the MVP acceptance flow passes.
