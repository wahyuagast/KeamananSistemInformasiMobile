# Keamanan Sistem Informasi Mobile

A Kotlin Android app built with Jetpack Compose that demonstrates authentication, role-based navigation, student PKL (internship) submissions, and admin review workflows, backed by Supabase (Auth, PostgREST, Storage).

## Tech stack

- Android, Kotlin, Jetpack Compose, Navigation Compose, Material 3
- MVVM with repositories and view models
- Retrofit + Kotlinx Serialization (JSON) for REST
- Supabase (Auth, PostgREST, Storage)
- Gradle Kotlin DSL
- Android Studio Otter \(`2025.2.1`\)

## High-level features

- Auth: login, register, logout
- Role-based routing: user vs. admin
- Student PKL flow:
  - Dashboard and multiple forms: registration, execution, monev, final report, exam docs
  - Upload files to Supabase Storage and persist metadata
- Admin flow:
  - List all registrations as submissions
  - Review \(`approve`/`reject`\) with optional comment
  - Loading/error toasts and refresh actions

## Project structure
KeamananSistemInformasiMobile/ ├─ app/ │ ├─ build.gradle.kts │ ├─ src/main/ │ │ ├─ AndroidManifest.xml │ │ ├─ java/com/wahyuagast/keamanansisteminformasimobile/ │ │ │ ├─ MainActivity.kt ← App entry, navigation host │ │ │ └─ supabaseauth/ │ │ │ ├─ data/ │ │ │ │ └─ TokenManager.kt ← Access tokens │ │ │ ├─ models/ ← DTOs and requests │ │ │ ├─ network/ │ │ │ │ ├─ RetrofitProvider.kt ← Retrofit instances │ │ │ │ ├─ PklApi.kt ← PostgREST endpoints │ │ │ │ └─ Constants.kt ← Supabase config (host, keys, urls) │ │ │ ├─ repository/ │ │ │ │ ├─ AuthRepository.kt │ │ │ │ └─ PklRepository.kt ← Business logic & uploads │ │ │ └─ ui/ │ │ │ ├─ LoginScreen.kt, RegisterScreen.kt │ │ │ ├─ AdminHomeScreen.kt, UserHomeScreen.kt │ │ │ └─ pkl/ │ │ │ ├─ AdminScreen.kt ← Admin list+review orchestrator │ │ │ ├─ AdminDashboard.kt ← Admin UI with toasts │ │ │ ├─ StudentDashboard.kt │ │ │ ├─ SubmissionFormScreen.kt │ │ │ ├─ RegistrationFormScreen.kt │ │ │ ├─ ExecutionFormScreen.kt │ │ │ ├─ MonevScreen.kt │ │ │ ├─ FinalReportScreen.kt │ │ │ └─ ExamDocsScreen.kt │ └─ proguard-rules.pro ├─ build.gradle.kts ├─ settings.gradle.kts └─ gradle/ wrapper, properties

Note: File names may vary slightly; paths above reflect key components you will interact with most.

## Setup

### Requirements

- Android Studio Otter \(`2025.2.1`\) or newer
- Android SDK Platform 34\+ installed
- Gradle is managed by the wrapper
- A Supabase project with:
  - Auth enabled
  - PostgREST tables/functions for PKL entities
  - Storage bucket for uploads
  - RLS policies configured

### Configure Supabase

Set your Supabase config in `app/src/main/java/com/wahyuagast/keamanansisteminformasimobile/supabaseauth/network/Constants.kt`:

- `PROJECT_HOST` \(`<your-project-ref>.supabase.co`\)
- `SUPABASE_ANON_KEY` \(*public anon key*\)
- `STORAGE_BASE` \(`https://<host>/storage/v1/object`\)

If `Constants.kt` does not exist, create it under the `network` package with the above fields.

### Open, build, and run

- Open the project in Android Studio
- Sync Gradle
- Select a device/emulator and Run

CLI on Windows:

- `gradlew.bat clean`
- `gradlew.bat assembleDebug`
- APK is under `app/build/outputs/apk/debug/`

## How the app works

### App entry and navigation

`MainActivity.kt` hosts a Navigation Compose `NavHost` with routes:

- `login`, `register`
- `user_home` → `student_dashboard` → `submission_form`, `registration_form`, `execution_form`, `monev_form`, `final_report`, `exam_docs`
- `admin_home` → `admin_dashboard`

After login:
- If `profile.role == "admin"`, navigate to `admin_home`
- Else go to `user_home`

### Auth

- `AuthRepository` talks to Supabase Auth and PostgREST
- `TokenManager` stores/retrieves bearer token for authenticated calls
- `AuthViewModel` exposes login/register/logout

### Student PKL flow

- `StudentDashboard` shows current submissions
- Forms call `PklRepository` to:
  - Create domain entries \(`createSubmission`, `createRegistration`, etc.\)
  - Upload files to Supabase Storage, then persist metadata with a public URL
- Upload sequence:
  1. Convert `Uri` → `File` in cache \(`MainActivity.uriToFile`\)
  2. `PklRepository.uploadFileToStorage(...)` PUTs the file to Storage with auth headers
  3. Build a public URL and save metadata via PostgREST

### Admin flow

- `AdminScreen` orchestrates the admin list, loading, error, and actions
- `AdminDashboard` renders the list and review dialog; shows toasts for:
  - loading start/end
  - refresh clicks
  - error messages
  - review submission \(`approve`/`reject`\)

Review actions:
- Repository splits updates to avoid policy conflicts:
  - PATCH `status` first \(`approved` or `rejected`\)
  - PATCH `admin_comment` next \(*best effort*\)
- This ensures status changes succeed even if comment write is restricted

## Networking

### Retrofit provider

`RetrofitProvider` builds clients for:
- Auth API
- PostgREST API \(*injects bearer token from `TokenManager`*\)
- PKL API

Ensure the JSON converter matches your DTO annotations \(*Kotlinx Serialization is used*\).

### PKL API \(*key endpoints*\)

`PklApi.kt` includes endpoints such as:

- Admin:
  - `getRegistrationsAdmin(order = "created_at.desc")`
  - `updateRegistrationStatus(body, id="eq.<uuid>")`
  - `updateRegistrationComment(body, id="eq.<uuid>")`
- Students:
  - `getMySubmissions`, `createSubmission`
  - `getMyRegistrations`, `createRegistration`
  - `submissionAction`
  - `createUploadMetadata`
  - `getMyProfile`

### DTOs \(*examples*\)

- `RegistrationStatusUpdate { status: String }`
- `RegistrationCommentUpdate { admin_comment: String }`
- `SubmissionDto`, `RegistrationDto`, request bodies for create actions, and `UploadResponse`

Keep DTO fields aligned with PostgREST column names.

## View models

- `AuthViewModel` for auth screens
- `HomeViewModel` for role-based home screens
- `PklViewModel` \(*created per screen as needed*\) to manage:
  - Loading, errors, and list state
  - Calls into `PklRepository` for submissions/registrations and file uploads

## Storage uploads

- PUT file bytes directly to `STORAGE_BASE/<bucket>/<destPath>`
- Headers:
  - `Authorization: Bearer <token>`
  - `apikey: <anon-key>`
- On success, compute public URL:
  - `https://<PROJECT_HOST>/storage/v1/object/public/<bucket>/<destPath>`
- Persist metadata via PostgREST

## Error handling and toasts

- Admin UI:
  - Toast on refresh, loading start/end, error, and before sending review
- Errors from network calls are captured in view models and surfaced to the UI
- Repository returns `Boolean` or nullable DTOs on create/update calls

## Known issues and fixes

- Retrofit error: `Unable to create @Body converter for Map<String, Any>`  
  Fix: avoid `Map<String, Any?>`. Use typed DTOs \(*e.g., `RegistrationStatusUpdate`, `RegistrationCommentUpdate`*\).

- Comment write blocked by RLS/policy  
  Fix: split PATCH into `status` then `admin_comment`. Do not fail the status update if the comment write fails.

## Build scripts and tasks

- `gradlew.bat assembleDebug` — build debug APK
- `gradlew.bat test` — run unit tests \(*if present*\)
- `gradlew.bat clean` — clean project

## Conventions

- Kotlin + Compose UI
- Repository pattern with Retrofit for data
- DTOs annotated for Kotlinx Serialization
- Route names are plain strings in `MainActivity.kt`
- File names and paths referenced in docs are wrapped in backticks

## Security

- Do not commit real Supabase keys for production
- Consider moving secrets to `local.properties` or build configs for release builds
- Ensure RLS policies only allow appropriate read/write access per role

## License

MIT License

Copyright (c) 2025 wahyuagast
