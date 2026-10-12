# CareerBridge AI — Complete Feature Test Prompts

Use these prompts/checks after starting the project. Run the authentication checks first, then the feature checks. Expected behavior is written after each prompt so testing is repeatable.

## 1. Google Authentication & Account Security

1. **Open `http://localhost:8080` while signed out.**
   - Expected: Only the original CareerBridge Google login screen is usable; the dashboard is not accessible.
2. **Click `Continue with Google`.**
   - Expected: Google consent/account selection opens; CareerBridge never asks for a Google password.
3. **Complete Google sign-in with Account A.**
   - Expected: You return to CareerBridge and see Account A's name/email in the top-right profile area.
4. **Refresh the page.**
   - Expected: The session remains active and the workspace is still available.
5. **Open browser developer tools → Application/Storage → Cookies.**
   - Expected: `CB_SESSION` is HttpOnly and scoped to the site. Do not expect the Google access token in browser storage.
6. **Log out.**
   - Expected: You return to the login screen and protected API calls no longer work.
7. **After logout, try opening `/api/auth/me`.**
   - Expected: HTTP 401 and `authenticated:false`.
8. **While signed out, POST to `/api/job-match`.**
   - Expected: HTTP 401 with `Authentication required.`
9. **Sign in again with Account A.**
   - Expected: Account A's saved CareerBridge workspace returns.
10. **Create a small Career Passport entry under Account A, log out, then sign in with Account B.**
    - Expected: Account B does not intentionally see Account A's browser-local CareerBridge data.
11. **Try an invalid/expired OAuth callback state.**
    - Expected: Sign-in is rejected rather than creating a session.

## 2. Career Passport

- Prompt: **"Create a Career Passport for a final-year B.Tech CSE student targeting Java Developer, with Java, MySQL, HTML, Git and one internship."**
- Expected: Save the profile and show completeness, target role, education, location, skills, projects and experience.
- Edit the profile and save again.
- Expected: The updated values appear everywhere the profile is reused.

## 3. Dashboard

- Prompt: **"Open My Career and check whether the dashboard reflects my profile, role fit, applications and follow-ups."**
- Expected: Profile completeness, application count, follow-up count, role fit and next actions update from actual stored data.
- Add an application.
- Expected: Application count and pipeline update.

## 4. Resume Upload & Resume-Based Job Matching

Use a test résumé containing:
`B.Tech CSE, Java, MySQL, HTML, CSS, JavaScript, Git, Spring Boot project, Bengaluru, internship.`

- Prompt: **"Upload this résumé and find suitable jobs for my profile."**
- Expected: Resume profile is extracted and role matches are displayed.
- Prompt: **"Prioritize jobs near Bengaluru."**
- Expected: Bengaluru/local-compatible roles receive location priority; location is not presented as a hiring guarantee.
- Prompt: **"Why does this Java Developer role match my résumé?"**
- Expected: Matched skills, missing skills and role-fit explanation are visible.
- Prompt: **"Which skills am I missing for the top role?"**
- Expected: Skill gaps are listed.
- Click **Verify on PGRKAM**.
- Expected: Official PGRKAM vacancy page opens for verification.

## 5. Opportunities

- Prompt: **"I have B.Tech CSE, Java, SQL, Git and Bengaluru as my preferred location. Find role matches."**
- Expected: Explainable role-fit results with matched/missing skills and location fit.
- Change location to Mysuru.
- Expected: Location-priority ordering changes accordingly.
- Select Remote.
- Expected: Remote-compatible roles are surfaced.

## 6. AI Career Copilot

Test each exact prompt:

1. **"I am a final year CSE student looking for a software developer job."**
   - Expected: PRIVATE_JOB intent and PGRKAM vacancy guidance.
2. **"I don't know which career I should choose after engineering."**
   - Expected: CAREER_GUIDANCE response and PGRKAM vocational-guidance route.
3. **"I am a CSE student and I know Java, MySQL and HTML. What jobs should I target?"**
   - Expected: PRIVATE_JOB / role-oriented guidance.
4. **"I want to become a Java developer but I don't know Spring Boot."**
   - Expected: CAREER_GUIDANCE / skill-development guidance.
5. **"I have three months before graduation and I still don't have a job. Help me create a plan."**
   - Expected: CAREER_GUIDANCE and actionable next steps.
6. **"Who won yesterday's cricket match?"**
   - Expected: OUT_OF_SCOPE rather than a fabricated career answer.
7. **"Here is my resume. Which PGRKAM jobs are suitable for me?"**
   - Expected: The user is directed into the resume/job-matching workflow.
8. **"Show me jobs near Bengaluru."**
   - Expected: Location-aware job workflow.
9. **"Which universities can I apply to for MS in Computer Science?"**
   - Expected: Study Abroad workflow.
10. **"What documents do I need for this university?"**
    - Expected: University/application document guidance.

## 7. Applications / Application Journey

- Prompt: **"Save a Java Developer application at ABC Technologies in Bengaluru."**
- Expected: Application card is created with stage and follow-up fields.
- Change stage from Saved → Applied → Recruiter Contacted → Interview.
- Expected: Timeline/status updates correctly.
- Delete the application.
- Expected: It disappears from the tracker and dashboard count decreases.

## 8. Recruiter Connect

- Prompt: **"Generate a recruiter message for a Java Developer role at ABC Technologies using my real skills and project."**
- Expected: A personalized draft is created; CareerBridge does not send it automatically.
- Click Open email draft.
- Expected: User's mail client opens with a draft.
- Save/record the recruiter interaction.
- Expected: It appears in Applications.

## 9. Resume Review

- Prompt: **"Review my résumé for a Java Developer role."**
- Expected: Readiness score, strengths, improvements, missing sections and keyword tips.
- Test an empty résumé.
- Expected: Validation/error message rather than a fabricated review.

## 10. Skill Plan

- Prompt: **"Create a 14-day plan to close my missing Spring Boot, REST API and Docker skills."**
- Expected: Target role, priority gaps and a structured preparation path.
- Open Skill Plan from a matched role.
- Expected: The selected role's missing skills populate the plan.

## 11. Interview Coach

Test each interview type:

- **Technical:** "Start a Java technical mock interview."
- **Project:** "Start a project interview for my recruitment platform project."
- **HR:** "Start an HR interview for a fresher software developer."
- **Mixed:** "Start a mixed software developer interview."

Expected: Questions appear, answers can be entered, and the basic evaluation/next-question flow works.

## 12. Interview Reminder

- Enter company, date/time and Google Meet link.
- Save reminder.
- Expected: Reminder information is retained in the signed-in browser workspace.

## 13. Study Abroad Matching

Test:

- Prompt: **"Find universities for MS Computer Science in Germany for a student with 8.0 CGPA."**
- Prompt: **"Find universities for MS Computer Science in Canada with a medium budget."**
- Prompt: **"Show universities suitable for my profile."**

Expected: University profile matches show fit score, programme, location, duration/intake guidance, eligibility, language/test requirements, documents, application steps and official links.

Important: verify actual requirements, fees and deadlines on the official university website before applying. The match is a profile-fit heuristic, not an admission probability.

## 14. PGRKAM Services

Open and test:

- Private Jobs → PGRKAM vacancies
- Government employment services
- Job Melas / employment events
- Career Counselling / Vocational Guidance
- Self-employment support
- Overseas resources

Expected: Official PGRKAM pages open in a new tab.

## 15. Job Safety Check

Test prompt/text:

**"Pay ₹2,000 registration fee today and send your OTP and UPI details to reserve your guaranteed job."**

Expected: Multiple warning signs are detected.

Test:

**"Please review our official careers page and apply through the company website. No payment is requested."**

Expected: No common warning-sign rule should trigger, while the page still reminds the user to verify independently.

## 16. Global Navigation

- Open every sidebar item once.
- Expected: Correct page title and content appear.
- Use browser Back/Refresh after several page changes.
- Expected: Application and profile data remain available for the signed-in account.

## 17. Authentication Regression Test

1. Login Account A.
2. Save Career Passport.
3. Upload résumé and create a match.
4. Save an application.
5. Logout.
6. Login Account B.
7. Verify Account B starts with its own workspace.
8. Logout Account B.
9. Verify protected API calls return 401.

## 18. Production Security Checklist

Before real deployment:

- Use HTTPS.
- Set `SECURE_COOKIES=true`.
- Register the exact production Google redirect URI.
- Keep `GOOGLE_CLIENT_SECRET` only on the server.
- Use a durable server-side session store instead of the current in-memory demo store.
- Add session revocation/rotation and rate limiting.
- Add a persistent database with per-user authorization checks.
- Do not store Google access/refresh tokens in localStorage.
- Add privacy/retention controls for uploaded résumés.
- Validate uploaded file type and size server-side if files are ever uploaded to the backend.
