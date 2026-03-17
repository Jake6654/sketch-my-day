#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

HOST="127.0.0.1"
PORT="8000"

if [ ! -d ".venv" ]; then
  echo "[ERROR] .venv not found. Run setup first: python3 -m venv .venv && pip install -r requirements.txt"
  exit 1
fi

source .venv/bin/activate

if [ -z "${OPENAI_API_KEY:-}" ] || [ -z "${REPLICATE_API_TOKEN:-}" ]; then
  if [ -f ".env" ]; then
    set -a
    # shellcheck disable=SC1091
    source .env
    set +a
  fi
fi

if [ -z "${OPENAI_API_KEY:-}" ] || [ -z "${REPLICATE_API_TOKEN:-}" ]; then
  echo "[ERROR] OPENAI_API_KEY / REPLICATE_API_TOKEN is missing"
  exit 1
fi

uvicorn app.main:app --host "$HOST" --port "$PORT" >/tmp/ai-service-smoke.log 2>&1 &
SERVER_PID=$!

cleanup() {
  kill "$SERVER_PID" >/dev/null 2>&1 || true
}
trap cleanup EXIT

for _ in {1..30}; do
  if curl -sSf "http://$HOST:$PORT/health" >/dev/null; then
    break
  fi
  sleep 1
done

curl -sS -X POST "http://$HOST:$PORT/generate-image" \
  -H 'Content-Type: application/json' \
  -d '{
    "user_id":"smoke-test-user",
    "entry_date":"2026-03-16",
    "mood":"chill",
    "content":"오늘은 카페에서 조용히 일기를 쓰고 하루를 정리했다.",
    "todo":"[]",
    "reflection":"마무리해서 마음이 편안했다."
  }'

echo
