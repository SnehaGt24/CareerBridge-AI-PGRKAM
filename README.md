# CareerBridge AI — PGRKAM Career Copilot

CareerBridge AI is a student-first employment assistance prototype built around the original PRJ_301 PGRKAM conversational-navigation concept.

## Product direction

Instead of presenting PGRKAM services as disconnected modules, CareerBridge connects the student's full career journey:

**Career Passport → Opportunity Intelligence → Apply Smart → Recruiter Connect → Follow-up → Interview Coach → Outcome**

## Included features

- AI Career Copilot using the existing PGRKAM intent-classification backend
- Career Passport for reusable student information
- Explainable role-fit and skill-gap matching
- Apply Smart workflow
- Application Journey with stages and follow-up tracking
- Recruiter Connect with personalized outreach drafting
- Resume review / tailoring support for PDF, DOCX, TXT and MD
- Skill-gap based 14-day preparation plan
- Interview Coach with technical, project and HR practice
- Job Safety Check for common recruitment red flags
- PGRKAM services, job-mela and career-resource links
- Local browser persistence for the prototype workspace

## Run

### Windows

Double-click `run.bat`, then open `http://localhost:8080`.

### macOS / Linux

```bash
chmod +x run.sh
./run.sh
```

Then open `http://localhost:8080`.

## Notes

This prototype intentionally does not send recruiter emails automatically, scrape private contact information, or claim that a job is genuine. Users verify official recruitment channels and send messages themselves.

For production deployment, add authentication, a persistent database, verified opportunity ingestion, consent/privacy controls, secure server-side integrations, audit logs and proper source verification.

### AI Career Copilot behavior
The Copilot is designed to do two things together: answer the student's immediate career question and navigate them to the relevant official PGRKAM service. Supported responses expose a clear **Open PGRKAM** action and, where useful, a second CareerBridge action for continuing the workflow.

### UI theme
The current build uses a full light theme throughout the application for improved readability and a cleaner SaaS-style experience. The dashboard/sidebar/cards/forms no longer use black or dark panels.
