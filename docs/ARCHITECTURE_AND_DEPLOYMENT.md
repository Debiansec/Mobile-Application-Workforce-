# WORKFORCE HEALTH SA — PLATFORM ARCHITECTURE & DEPLOYMENT MANUAL

## 1. System Overview
The **WORKFORCE HEALTH SA Occupational Hygiene Digital Platform** replaces paper inspection checklists across South African mining and heavy industrial sites.
The system consists of three seamlessly integrated tiers:
1. **Android Mobile Application (Offline-First):** Built with Kotlin, Jetpack Compose, Material 3, Room local database, ActivityResult media capture, GPS geostamping, and electronic signature pad.
2. **PostgreSQL / Supabase Cloud Database & Storage:** Relational schema with Row Level Security (RLS), POPIA audit trails, and private S3/storage buckets for photographs, certificates, and SDS sheets.
3. **Web-Based Management Portal:** Next.js / React management portal for Occupational Hygienists (SAIOH registered), Client Mine Managers, and System Administrators.

---

## 2. Initial Module: Monthly Ablution Facilities Checklist (Male)
Implements all 51 authoritative items from the official specification:
- **SECTION A — BASINS (Q1 to Q5):** Cleanliness, liquid soap dispensers, water taps closing, hand-washing education signage, mold/yeast/mildew detection.
- **SECTION B — SHOWERS (Q1 to Q5):** Cleanliness, taps closing properly, fitted showerheads, curtain/door privacy, mold/yeast/mildew on walls/floors.
- **SECTION C — TOILETS (Q1 to Q7):** Cleanliness & dry floors, water bowl lids, sitting seats, flushing mechanism, toilet paper supply, door closing, mold/yeast/mildew signs.
- **SECTION D — URINALS (Q1 to Q5):** Urinals cleanliness, proper flushing, dry floors, bad odor detection, mold/yeast/mildew signs.
- **SECTION E — LOCKERS (Q1 to Q5):** Locker allocation per employee, owner name/number marking, doors closing properly, padlock/locking mechanisms, area cleanliness.
- **SECTION F — GENERAL (Q1 to Q23):** Gender signage, hand-washing posters, fungal infection warning posters, rubbish bin & liners, deep sanitization frequency & certificates, cleaning register sign-offs, complaint/compliment book, tidy standard, doors closing automatically, air odours, hygiene training frequency, ventilation assessment reference, illumination assessment reference, user count (~145 employees), deep-cleaning chemicals & procedure status (Q20), frosted privacy windows, routine cleaning chemicals (Q22), and disabled-friendly facilities.
- **SECTION G — OTHER:** Repeatable custom compliance items.

---

## 3. Database Schema & Migration Execution
The PostgreSQL schema migration file is located at `backend/migrations/001_initial_schema.sql`.

### Deployment to Supabase / PostgreSQL:
```bash
# 1. Login to Supabase CLI
supabase login

# 2. Link your production cloud project
supabase link --project-ref <your-supabase-project-id>

# 3. Apply the initial schema migration
supabase db push
```

---

## 4. Environment Variables Configuration
Copy `.env.example` to `.env` in the root workspace and configure appropriate keys:
```properties
# Cloud Backend Configuration
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-public-anon-key
SUPABASE_SERVICE_ROLE_KEY=your-backend-only-secret-key

# Database Connection Pooler (Transaction Mode)
DATABASE_URL=postgresql://postgres:[PASSWORD]@db.[PROJECT-REF].supabase.co:6543/postgres

# Storage Bucket Names
STORAGE_EVIDENCE_BUCKET=inspection-evidence
STORAGE_REPORTS_BUCKET=authorized-reports
STORAGE_SDS_BUCKET=chemical-sds-documents
```

---

## 5. Web Portal Deployment
The web portal is located in `web-portal/`.
```bash
cd web-portal
npm install
npm run build
npm run start
```
Can be deployed to Vercel, AWS ECS, or Google Cloud Run.

---

## 6. Android Application Build & APK Generation
- **Compile SDK:** 36
- **Min SDK:** 24 (Android 7.0+ Nougat, covers all mining rugged handhelds & tablets)
- **Target SDK:** 36
- **Architecture:** Clean Architecture + MVVM + Jetpack Compose
- **Test suite:** JUnit + Robolectric JVM tests verified (`gradle :app:testDebugUnitTest`)

To build a release APK locally:
```bash
gradle :app:assembleRelease
```
The resulting APK will be generated at `app/build/outputs/apk/release/app-release.apk`.
