# Sketch My Day Project Overview

## 1. Product Summary

Sketch My Day is a full-stack illustrated diary application. Users sign in with Google, write a diary entry for a specific date, choose a mood, add todo reflections, generate an AI illustration from the diary content, and revisit past entries through a diary board and calendar.

The project combines a playful notebook-style frontend with a Spring Boot API, PostgreSQL persistence, and a FastAPI-based AI image generation pipeline.

## 2. Current User Experience

The application currently supports these main flows:

- Google login through Supabase Auth.
- Opening today's diary page from the home screen.
- Creating or editing a diary entry by date.
- Selecting a mood for the diary entry.
- Managing todo items and reflections.
- Requesting an AI-generated illustration based on the diary content.
- Polling the image generation job until it completes or fails.
- Saving diary data to the backend.
- Viewing saved diary summaries on a board and calendar.

## 3. High-Level Architecture

```text
User
  |
  v
Next.js Frontend
  |
  | Diary API / AI Job API
  v
Spring Boot Backend
  |
  | JPA
  v
PostgreSQL

Spring Boot Backend
  |
  | HTTP proxy request
  v
FastAPI AI Service
  |
  | Job status
  v
Redis

FastAPI AI Service
  |
  | Prompt generation
  v
OpenAI

FastAPI AI Service
  |
  | Image generation
  v
Replicate or Self-Hosted Image Server
```

## 4. Repository Structure

```text
sketch-my-day/
├── README.md
├── PROJECT_OVERVIEW.md
├── AWS_EC2_GPU_VM_SETUP.md
├── docker-compose.yml
├── scripts/
│   └── smoke-test-ai-stack.sh
├── frontend/
│   ├── app/
│   │   ├── page.tsx
│   │   ├── layout.tsx
│   │   ├── globals.css
│   │   ├── auth/callback/page.tsx
│   │   ├── diary/page.tsx
│   │   ├── diary/[date]/page.tsx
│   │   └── diary-board/page.tsx
│   ├── components/
│   │   ├── diary/
│   │   │   ├── DiaryEditor.tsx
│   │   │   ├── DiaryCalender.tsx
│   │   │   ├── HangingNotes.tsx
│   │   │   ├── BirthdaySurprise.tsx
│   │   │   └── diaryTypes.ts
│   │   └── ui/
│   ├── lib/
│   │   ├── supabaseClient.ts
│   │   └── utils.ts
│   ├── package.json
│   ├── next.config.ts
│   ├── tsconfig.json
│   └── Dockerfile
├── backend/
│   ├── src/main/java/sketch_my_day/demo/
│   │   ├── SketchMyDayApplication.java
│   │   ├── HealthController.java
│   │   ├── ai/
│   │   ├── config/
│   │   ├── diary/
│   │   └── logging/
│   ├── src/main/resources/application.yml
│   ├── src/test/
│   ├── build.gradle
│   └── Dockerfile
├── ai-service/
│   ├── app/
│   │   ├── main.py
│   │   ├── schemas.py
│   │   └── image_providers/
│   ├── scripts/
│   ├── requirements.txt
│   ├── docker-compose.yml
│   └── Dockerfile
└── image-server/
    ├── app/
    │   ├── main.py
    │   ├── schemas.py
    │   └── image_providers/
    ├── requirements.txt
    └── Dockerfile
```

## 5. Frontend

The frontend is a Next.js App Router application using React, TypeScript, Tailwind CSS, Supabase Auth, Framer Motion, and Lucide icons.

Main responsibilities:

- Own the user interface and diary writing experience.
- Manage Supabase Google authentication in the browser.
- Load the current Supabase session and user ID.
- Call the Spring Boot diary APIs.
- Start AI image generation jobs through the backend.
- Poll job status and display the generated illustration URL.
- Render the diary board, hanging notes, and calendar view.

Important files:

| File | Purpose |
| --- | --- |
| `frontend/app/page.tsx` | Home screen, Google login/logout, and entry point to today's diary |
| `frontend/app/auth/callback/page.tsx` | Supabase OAuth callback handling |
| `frontend/app/diary/page.tsx` | Redirect-style diary entry point |
| `frontend/app/diary/[date]/page.tsx` | Date-specific diary create/edit page |
| `frontend/app/diary-board/page.tsx` | Board and calendar view for saved diaries |
| `frontend/components/diary/DiaryEditor.tsx` | Main diary editor, todo state, save flow, and AI job polling |
| `frontend/components/diary/HangingNotes.tsx` | Visual preview of recent diary entries |
| `frontend/components/diary/DiaryCalender.tsx` | Calendar view of diary entries |
| `frontend/components/diary/BirthdaySurprise.tsx` | Special date-based surprise interaction |
| `frontend/lib/supabaseClient.ts` | Supabase browser client setup |

Current routes:

| Route | Purpose |
| --- | --- |
| `/` | Landing/home screen and authentication entry |
| `/auth/callback` | OAuth callback |
| `/diary` | Diary entry route |
| `/diary/:date` | Create or edit a diary for a specific date |
| `/diary-board` | View diary history through notes and calendar |

## 6. Backend

The backend is a Spring Boot REST API using Java 21, Spring Web MVC, Spring Data JPA, Spring Validation, WebClient, Lombok, and PostgreSQL.

Main responsibilities:

- Expose diary CRUD-style endpoints to the frontend.
- Persist diary entries in PostgreSQL.
- Convert database entities into response DTOs.
- Proxy AI image job requests to the FastAPI AI service.
- Centralize CORS and AI service configuration.
- Provide a health check endpoint.

Important packages:

| Package | Purpose |
| --- | --- |
| `diary/` | Diary entity, controller, service, repository, and DTOs |
| `ai/` | AI image job proxy controller, service, and DTOs |
| `config/` | CORS and AI WebClient configuration |
| `logging/` | OpsLens request/client support |

Backend endpoints:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/health` | Backend health check |
| `GET` | `/api/diaries?userId=...` | Return diary summaries for a user |
| `GET` | `/api/diaries/{date}?userId=...` | Return one diary for a user and date |
| `POST` | `/api/diaries` | Create or update a diary entry |
| `POST` | `/api/ai/generate-image` | Create an AI image generation job |
| `GET` | `/api/ai/generate-image/{jobId}` | Poll AI image generation job status |

The main backend flow is:

```text
Controller -> Service -> Repository -> PostgreSQL
```

For AI jobs, the backend flow is:

```text
Frontend -> Spring Boot Backend -> FastAPI AI Service -> Redis / OpenAI / Image Provider
```

## 7. AI Service

The AI service is a FastAPI application that creates asynchronous image generation jobs. It uses Redis to store temporary job state, OpenAI to turn diary content into an illustration prompt, and an image provider to generate the final image.

Main responsibilities:

- Accept diary content, mood, todo data, and reflections.
- Create a generation job with a UUID.
- Store job state in Redis with a temporary expiration.
- Generate a summary, image prompt, and negative prompt through OpenAI.
- Call the selected image provider.
- Return job status, illustration URL, prompt metadata, and errors.

AI service endpoints:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/health` | AI service health check |
| `POST` | `/generate-image` | Create a background generation job |
| `GET` | `/generate-image/{job_id}` | Read generation job status |

Job lifecycle:

```text
pending -> processing -> completed
                       -> failed
```

## 8. Image Server

The `image-server/` service is a separate self-hosted image generation server. The root Docker Compose configuration can run it beside Redis, the AI service, the backend, and the frontend.

Main responsibilities:

- Expose a `/generate` endpoint for image generation.
- Serve generated files through `/static`.
- Support provider-based image generation, including mock and Hugging Face-backed implementations.
- Allow the AI service to use `IMAGE_PROVIDER=self_hosted` and call `IMAGE_MODEL_SERVER_URL=http://image-server:8001` inside Docker.

Image server endpoints:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/health` | Image server health check |
| `POST` | `/generate` | Generate an image and return an image URL |
| `GET` | `/static/{filename}` | Serve generated image files |

## 9. Local Development

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Backend:

```bash
cd backend
./gradlew bootRun
```

AI service:

```bash
cd ai-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Full local AI stack from the project root:

```bash
docker compose up --build
```

## 10. Environment Variables

Frontend:

```text
NEXT_PUBLIC_SUPABASE_URL=
NEXT_PUBLIC_SUPABASE_ANON_KEY=
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Backend:

```text
DB_URL=
DB_USER=
DB_PASSWORD=
CORS_ALLOWED_ORIGINS=http://localhost:3000
AI_SERVICE_BASE_URL=http://127.0.0.1:8000
OPSLENS_URL=
OPSLENS_API_KEY=
OPSLENS_PROJECT=sketch-my-day
OPSLENS_ENVIRONMENT=dev
```

AI service:

```text
OPENAI_API_KEY=
REPLICATE_API_TOKEN=
REDIS_URL=redis://localhost:6379/0
IMAGE_PROVIDER=
IMAGE_MODEL_SERVER_URL=
IMAGE_MODEL_TIMEOUT_SECONDS=
```

Image server:

```text
IMAGE_SERVER_PROVIDER=
```

## 11. Current Strengths

- Clear separation between frontend, backend, AI orchestration, and image generation.
- Backend protects the frontend from directly depending on the AI service contract.
- Async AI job flow avoids blocking the frontend during long image generation.
- Redis job state gives the app a simple polling model.
- Supabase Auth provides a quick path to Google login.
- The UI has a distinctive playful diary identity.
- Docker Compose can run the main multi-service stack locally.

## 12. Recommended Future Development

### Product and UX

- Add richer diary browsing, including search, filters by mood, and month/year navigation.
- Add a gallery view focused on generated illustrations.
- Let users regenerate illustrations with different styles, such as watercolor, comic, cozy sketch, or cinematic.
- Add editable AI prompts for advanced users.
- Add draft autosave so diary text is not lost before saving.
- Add export options, such as image download, PDF diary pages, or monthly recap.
- Improve mobile writing ergonomics with a more compact editor toolbar and clearer save/generate states.
- Add accessibility checks for keyboard navigation, focus states, contrast, and screen reader labels.

### Backend and Data

- Replace raw `userId` query trust with authenticated request validation.
- Add database migrations with Flyway or Liquibase.
- Add explicit indexes for common diary queries, especially `userId + entryDate`.
- Store todos as structured JSON or normalized rows instead of serialized strings if todo features grow.
- Add pagination to diary summary endpoints.
- Add stronger error response formats so the frontend can show better messages.
- Add integration tests for diary save, update, fetch, and AI proxy behavior.

### AI Pipeline

- Add style presets and model/provider selection.
- Store generated prompt metadata with each diary so users can inspect or reuse it.
- Add retry and timeout policies around OpenAI and image provider calls.
- Add a job cleanup strategy that matches product needs instead of relying only on Redis expiration.
- Add content safety checks before generating prompts or images.
- Add a fallback provider when Replicate or the self-hosted image server is unavailable.
- Consider moving long-running generation into a real worker queue if traffic grows.

### Infrastructure and Deployment

- Create separate Docker Compose profiles for local mock generation, Replicate generation, and self-hosted GPU generation.
- Add CI checks for frontend lint/build, backend tests, and Python smoke tests.
- Add production-ready environment documentation for Vercel, backend hosting, Redis, PostgreSQL, and GPU image generation.
- Add centralized logging and tracing for AI job creation, processing, and failures.
- Add health checks for every service in deployment, not just local Docker Compose.
- Document backup and recovery for diary data.

### Code Quality

- Standardize naming between snake_case and camelCase responses across FastAPI, Spring, and frontend DTOs.
- Reduce duplicated request/response normalization code in the frontend.
- Move API calls into a small typed client module under `frontend/lib/`.
- Add frontend component tests for diary editor behavior.
- Add contract tests between Spring Boot and FastAPI.
- Remove generated cache files from tracked or visible source directories if they appear in future commits.

## 13. Suggested Next Milestones

1. Secure the API boundary by validating authenticated users instead of trusting `userId` from the browser.
2. Add database migrations and diary query indexes.
3. Extract frontend API calls into typed client helpers.
4. Add style presets and regeneration for AI illustrations.
5. Add CI checks for frontend, backend, and AI service.
6. Improve diary board navigation with search, mood filters, and pagination.
7. Prepare deployment documentation for the full production stack.

## 14. Long-Term Vision

Sketch My Day can grow from a diary app into a personal memory studio. The strongest direction is to make each saved day feel like a small collectible artifact: written reflection, emotional state, completed tasks, AI-generated image, and a timeline of personal growth.

The future version could include monthly storybooks, mood analytics, illustration style collections, private sharing, printable diary exports, and a safer production-grade AI pipeline. The core idea should stay simple: write about the day, turn it into art, and make looking back feel meaningful.
