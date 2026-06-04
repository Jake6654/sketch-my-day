#!/usr/bin/env bash
set -euo pipefail

# use the existing env vars otherwise use default env vars
AI_SERVICE_URL="${AI_SERVICE_URL:-http://127.0.0.1:8000}"
IMAGE_SERVER_URL="${IMAGE_SERVER_URL:-http://127.0.0.1:8001}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-60}"
POLL_INTERVAL_SECONDS="${POLL_INTERVAL_SECONDS:-2}"

echo "[INFO] Checking ai-service health..."
curl -sSf "$AI_SERVICE_URL/health" >/dev/null

echo "[INFO] Checking image-server health..."
curl -sSf "$IMAGE_SERVER_URL/health" >/dev/null

echo "[INFO] Creating image generation job..."
CREATE_RESPONSE="$(curl -sSf -X POST "$AI_SERVICE_URL/generate-image" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "smoke-test-user",
    "entry_date": "2026-06-01",
    "mood": "focused",
    "content": "Today I tested the local AI stack smoke test.",
    "todo": "[]",
    "reflection": "The system should complete an async generation job."
  }')"

JOB_ID="$(printf '%s' "$CREATE_RESPONSE" | python3 -c 'import sys,json; print(json.load(sys.stdin)["job_id"])')"

if [ -z "$JOB_ID" ]; then
  echo "[ERROR] Failed to extract job_id from response:"
  echo "$CREATE_RESPONSE"
  exit 1
fi

echo "[INFO] Created job: $JOB_ID"
echo "[INFO] Polling job status..."

SECONDS_WAITED=0

while [ "$SECONDS_WAITED" -lt "$TIMEOUT_SECONDS" ]; do
  STATUS_RESPONSE="$(curl -sSf "$AI_SERVICE_URL/generate-image/$JOB_ID")"

  STATUS="$(printf '%s' "$STATUS_RESPONSE" | python3 -c 'import sys,json; print(json.load(sys.stdin)["status"])')"

  echo "[INFO] status=$STATUS"

  if [ "$STATUS" = "completed" ]; then
    IMAGE_URL="$(printf '%s' "$STATUS_RESPONSE" | python3 -c 'import sys,json; print(json.load(sys.stdin).get("illustration_url") or "")')"

    if [ -z "$IMAGE_URL" ]; then
      echo "[ERROR] Job completed but illustration_url is missing"
      echo "$STATUS_RESPONSE"
      exit 1
    fi

    echo "[PASS] Job completed successfully"
    echo "[PASS] image_url=$IMAGE_URL"
    exit 0
  fi

  #
  if [ "$STATUS" = "failed" ]; then
    echo "[ERROR] Job failed"
    echo "$STATUS_RESPONSE"
    exit 1
  fi

  sleep "$POLL_INTERVAL_SECONDS"
  SECONDS_WAITED=$((SECONDS_WAITED + POLL_INTERVAL_SECONDS))
done

echo "[ERROR] Timed out waiting for job to complete"
curl -sS "$AI_SERVICE_URL/generate-image/$JOB_ID" || true
echo
exit 1