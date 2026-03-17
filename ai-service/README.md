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

## Quick Test

1) Start server

```bash
cd ai-service
source .venv/bin/activate
uvicorn app.main:app --host 127.0.0.1 --port 8000
```

2) Run one-line `curl` test in another terminal

```bash
curl -sS -X POST "http://127.0.0.1:8000/generate-image" -H "Content-Type: application/json" -d '{"user_id":"smoke-test-user","entry_date":"2026-03-16","mood":"chill","content":"오늘은 카페에서 조용히 일기를 쓰고 하루를 정리했다.","todo":"[]","reflection":"마무리해서 마음이 편안했다."}'
```

3) Or run all-in-one smoke test script (start server + request + shutdown)

```bash
cd ai-service
./scripts/smoke-test-generate-image.sh
```

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
