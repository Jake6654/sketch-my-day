# Sketch My Day (Rebuild)

A full stack diary application rebuilt from scratch to deeply understand
frontend architecture, backend design, and deployment pipeline.

This project focuses on learning by building, starting with a clean frontend
and gradually integrating a Spring Boot backend, CI pipeline, and cloud deployment.

---

## 🚀 Tech Stack

### Frontend
- Next.js 16
- React 19
- TypeScript
- Tailwind CSS
- Zustand (state management)
- Axios (API communication)

### Backend (Planned)
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Security + JWT (later)

### DevOps
- GitHub Actions (CI)
- Docker (planned)
- AWS (planned)
- Kubernetes (planned)

### AI Service
- Python
- FastAPI
- Diffusion Model (Stable Diffusion)
- PyTorch (planned)

---

## 🏗 Architecture (AI Integrated)

```mermaid
flowchart LR
  U[User] -->|HTTPS| FE[Next.js Frontend]
  FE -->|REST API| BE[Spring Boot API]
  BE -->|HTTP| AI[Python AI Service]
  AI -->|Generate Image| DM[Diffusion Model]
  BE -->|SQL| DB[(PostgreSQL)]
  BE -->|Store Image| S3[(Object Storage)]
> The backend communicates with a dedicated Python AI service
> that generates illustrations using a diffusion model based on diary content.

---

# 🔥 2️⃣ Request Flow (AI 이미지 생성 포함)

이게 제일 중요한 부분이야.

```markdown
## 🔄 Request Flow (Create Diary + Generate Image)

```mermaid
sequenceDiagram
  participant U as User
  participant FE as Frontend (Next.js)
  participant BE as Backend (Spring Boot)
  participant AI as Python AI Service
  participant DB as PostgreSQL
  participant S3 as Object Storage

  U->>FE: Write diary + Click Save
  FE->>BE: POST /api/diaries
  BE->>AI: POST /generate-image (diary text)
  AI->>AI: Run Diffusion Model
  AI-->>BE: Return image (base64 or URL)
  BE->>S3: Upload image
  BE->>DB: Save diary + image URL
  BE-->>FE: 201 Created (with image URL)
  FE-->>U: Display diary with generated image
