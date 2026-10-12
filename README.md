# CareerBridge AI — PGRKAM Career Copilot

**CareerBridge AI: Resume-Based Job and University Matching with Location-Aware Career Assistance**

*A PGRKAM-integrated student employment and study-abroad assistance prototype.*

CareerBridge starts from the student's own career profile and résumé. It extracts useful signals, explains which role types fit the profile, prioritizes roles according to the student's preferred location, and connects the student to the official PGRKAM vacancy/service layer. The same profile can be used to shortlist study-abroad university profiles and open a detailed eligibility, documents and application guide.

## Main workflow

**Resume / Career Passport → Profile Extraction → Resume–Job Match → Location Priority → PGRKAM Vacancy Verification → Apply Smart → Recruiter Connect → Interview Coach**

For higher education:

**Career Passport → Study Abroad Match → University Profile → Eligibility → Documents → How to Apply → Official University / Government Guidance**

## Included features

- AI Career Copilot with deterministic intent classification and official PGRKAM navigation
- Resume upload and browser-side PDF/DOCX/TXT/Markdown extraction
- Resume profile extraction: degree, skills, target-role signals, location and experience signals
- Resume-to-job matching with explainable matched skills and missing skills
- Location-aware job prioritisation using the student's preferred city/location; remote-compatible roles are surfaced after exact-location matches
- PGRKAM vacancy verification links on every matched role
- Career Passport with education, CGPA, target role, location, English-test information, skills and projects
- Manual Opportunity Intelligence for direct profile-based role matching
- Apply Smart + Application Journey + recruiter follow-up
- Recruiter Connect with user-controlled outreach drafting
- Resume Review / Tailor as a separate role-specific review tool
- Skill-gap based 14-day preparation plan
- Interview Coach with technical, project and HR practice
- Job Safety Check for common recruitment red flags
- Study Abroad Match with university profile details, programme areas, duration/intake guidance, academic/language/test guidance, documents, application steps and official links
- PGRKAM service navigation for vacancies, registration, vocational guidance and related resources
- Local browser persistence for the prototype workspace
- Full light UI for readability

## Important prototype boundaries

- The job catalogue in this build is a **prototype role catalogue**, not a live scrape of PGRKAM vacancies. Each role links to the official PGRKAM vacancy hub so the student can verify the current live vacancy, employer advertisement, eligibility and deadline.
- Resume/job scores are **profile-fit indicators**, not probabilities of getting hired.
- University scores are **profile-match heuristics**, not admission probabilities.
- University requirements, fees, deadlines, intake availability and visa rules can change; students must verify the exact programme on the official university/government page before applying.
- No recruiter message is sent automatically.
- Personal workspace data is stored in browser localStorage in this prototype.

## Run

### Windows

Double-click `run.bat`, then open `http://localhost:8080`.

### macOS / Linux

```bash
chmod +x run.sh
./run.sh
```

Then open `http://localhost:8080`.

## Backend endpoints

- `GET /api/health`
- `POST /api/chat`
- `POST /api/job-match`
- `POST /api/resume-match`
- `POST /api/resume-review`
- `POST /api/study-abroad`
- `POST /api/application`
- `POST /api/hr-contact`
- `POST /api/interview`
- `GET /api/hr-directory`

## PGRKAM integration

The current PGRKAM portal provides candidate-facing employment services including registration, vacancies and vocational guidance. CareerBridge uses these official services as the verification/navigation layer rather than pretending to replace the government portal.

Official PGRKAM links used by the prototype:

- https://cms.pgrkam.com/
- https://cms.pgrkam.com/vacancies
- https://cms.pgrkam.com/employment-services-related-to-candidates
- https://cms.pgrkam.com/vocational-guidance
- https://cms.pgrkam.com/registration

## Future production work

- Authorised live PGRKAM vacancy/API/feed integration
- Verified employer and vacancy ingestion with source timestamps
- Authentication and persistent database storage
- Consent/privacy controls and audit logs
- Stronger résumé parsing and multilingual extraction
- Live university/programme feeds and scholarship data from authorised sources
- Application deadlines and reminders
- Cloud deployment and monitoring
## Secure Google Login

CareerBridge uses a server-side **email sign-in 2.0 Authorization Code flow**. The app never asks for or stores a Google password. After Google authenticates the account, the Java server creates an HttpOnly session cookie and the browser receives only the signed-in profile basics needed by the UI.

### Local setup

1. Create a Google Cloud project and configure an OAuth 2.0 **Web application** client.
2. Add this authorized redirect URI exactly:
   `http://localhost:8080/auth/google/callback`
3. In the project folder, create a file named `.env` by copying `.env.example`. Put your Google client ID and client secret in it. The app reads `.env` automatically for local development; operating-system environment variables still take priority.
4. Start with `run.bat` or `run.sh`.
5. Open `http://localhost:8080` and choose **Continue with email**.

If Google credentials are missing, the login screen now shows a clear setup message instead of a vague sign-in failure.

### Production security

Use HTTPS, set `SECURE_COOKIES=true`, use a production redirect URI, keep the Google client secret only on the server, and replace the in-memory session store with a durable server-side session/identity store. The current project is a secure local/demo implementation, not a production identity platform.

### Data separation

Career Passport, résumé matches and application data stored in the browser are scoped by the authenticated Google email so separate signed-in accounts do not intentionally share the same local workspace.
