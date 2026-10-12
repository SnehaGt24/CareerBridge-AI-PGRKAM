# CareerBridge AI — Updated Project Notes

## New product direction — September 2026

The prototype now uses the student's résumé as a primary starting point:

**Upload résumé → extract profile signals → match role types → prioritise preferred location → verify current PGRKAM vacancy → apply / prepare**

A second path strengthens the study-abroad module:

**Career Passport → university profile matching → eligibility guidance → documents → application steps → official university/government links**

## Resume-to-job matching

`ResumeMatchService` performs lightweight local extraction from résumé text. It detects common degree terms, technical skills, experience signals and common locations. It then calls `JobMatchService` to produce explainable role matches.

The matcher reports:
- resume / role fit score
- matched skills
- missing skills
- preferred-location match
- remote availability
- work mode
- preparation roadmap
- official PGRKAM vacancy hub link

The location ordering is intentionally transparent: an exact preferred-city match gets the highest location priority, a remote-compatible role gets a secondary priority, and other locations remain available without claiming physical distance or guaranteed availability.

## PGRKAM boundary

The prototype does **not** claim to ingest the live PGRKAM vacancy database. The PGRKAM vacancies page itself tells candidates to check the employer/advertisement for current eligibility, deadline and other details. Therefore the UI labels the internal role catalogue as a prototype match and sends the user to the official PGRKAM vacancy hub for verification.

## Study Abroad matching

`StudyAbroadService` now returns detailed university profile records containing:
- university, city, country and institution type
- representative programme areas
- duration/intake guidance
- profile-match heuristic
- academic eligibility guidance
- language requirement guidance
- test requirement guidance
- work-experience guidance
- document checklist
- step-by-step application guidance
- official university/application URL
- official country-level study/visa guidance URL

The university data is deliberately phrased as guidance rather than a promise of admission. Exact requirements must be checked on the selected programme's official page.

## AI Career Copilot

The Copilot still provides official PGRKAM navigation, and it now also points supported queries toward the new workflows:
- resume + job/role questions → Resume + Job Match
- study abroad / university questions → Study Abroad Match
- private/government/job-mela questions → Opportunities + PGRKAM
- career-choice questions → PGRKAM vocational guidance

## Existing modules retained

Career Passport, manual Opportunities, Apply Smart, Applications, Recruiter Connect, Resume Review, Skill Plan, Interview Coach, PGRKAM Resources and Job Safety Check remain part of the project.

## Validation completed

- Java compilation succeeds with the JDK HTTP server stack.
- Built-in Copilot intent suite remains 6/6 (100% demo accuracy).
- `/api/resume-match` returns extracted profile signals and role matches.
- `/api/study-abroad` returns detailed university records and application guidance.
- Browser-side JavaScript passes `node --check`.
- The application keeps the existing light UI.
