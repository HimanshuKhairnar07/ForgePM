# Sprint 2 — Workspaces and projects

**Goal:** Authorized users can create and manage private projects, with access restricted to the correct workspace and project members.

1. **Workspace creation:** Implement `POST /api/v1/workspaces`. Create workspace and OWNER membership atomically. Validate slugs and return `409` for collisions. Add accessible workspace listing for the dashboard.
2. **Membership:** Implement `POST /api/v1/workspaces/{id}/members`. Resolve existing users by normalized email. Protect owner role and prevent privilege escalation. Decide removal, transfer, and invitation semantics before adding those endpoints.
3. **Projects:** Add `V2__create_projects_and_members.sql`, JPA repositories, and project service. Enforce workspace-scoped project keys, active lead eligibility, role checks, and archival. Implement list/create/detail plus edit/archive and membership endpoints. Bound page size and add a unique tie-breaker to default sort.
4. **Authorization:** Check workspace membership before project membership and mutation permissions, including for lookups by UUID. Use `404` when revealing the resource would cross an isolation boundary; use `403` when an authorized reader tries a forbidden mutation. Define the full role matrix before implementing each endpoint.
5. **UI:** Add workspace selector, project list, create/edit forms and membership controls. Use React Router and TanStack Query; document server validation responses and show pending/empty/error states.

**Acceptance:** Test owner membership creation, duplicate slugs and project keys, unauthorized member changes, viewer write denial, outsider UUID lookups, cross-workspace lead rejection, stable pagination, and archive behavior. Exercise register → workspace → project in the browser before moving on to issues.
