# AI Service (FastAPI)

This service provides AI endpoints used by the Spring Boot backend.

## Setup

```bash
cd ai-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Run

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

## Endpoints

- `GET /health`
- `POST /generate-image`

### Request Example

```json
{
  "user_id": "user-123",
  "entry_date": "2026-03-10",
  "mood": "chill",
  "content": "Today I felt calm and finished my project.",
  "todo": "[{\"id\":1,\"text\":\"Study Spring\",\"done\":true}]",
  "reflection": "I feel stable."
}
```

### Response Example (Mock)

```json
{
  "illustration_url": "https://picsum.photos/seed/sketch-my-day/1024/1024",
  "summary": "2026-03-10 diary illustration generated from journal content.",
  "prompt": "soft watercolor diary illustration, cozy desk at night, chill atmosphere, journal writing scene",
  "negative_prompt": "blurry, bad anatomy, extra fingers, text, watermark"
}
```
