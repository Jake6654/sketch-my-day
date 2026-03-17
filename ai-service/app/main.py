import json
import os
import uuid

from dotenv import load_dotenv
from fastapi import BackgroundTasks, FastAPI, HTTPException
from openai import OpenAI
import replicate

from .schemas import (
    GenerateImageJobCreateResponse,
    GenerateImageJobStatusResponse,
    GenerateImageRequest,
)

load_dotenv()

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = FastAPI(
    title="Sketch My Day AI Service",
    description="AI service for diary analysis and illustration generation",
    version="0.1.0",
)

# Temporary in-memory job store
# Later, replace this with Redis.
jobs: dict[str, dict] = {}


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}

# summarize diary content by LLM
def process_diary_with_llm(payload: GenerateImageRequest) -> dict[str, str]:
    system_prompt = """
You are an assistant that converts a diary entry into structured inputs for an illustration model.

Return valid JSON with exactly these keys:
- summary
- prompt
- negative_prompt

Rules:
- summary should be one or two sentences.
- prompt should describe a visually clear diary-style illustration scene.
- prompt should reflect the user's mood and diary content.
- negative_prompt should be a concise list of unwanted visual artifacts for diffusion models.
- Return JSON only. Do not include markdown fences.
"""

    user_prompt = f"""
Diary entry date: {payload.entry_date}
Mood: {payload.mood}
Content: {payload.content}
Todo: {payload.todo or ""}
Reflection: {payload.reflection or ""}
"""

    response = client.chat.completions.create(
        model="gpt-5.4",
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ],
        temperature=0.7,
    )

    raw_content = response.choices[0].message.content
    parsed = json.loads(raw_content)

    return {
        "summary": parsed["summary"],
        "prompt": parsed["prompt"],
        "negative_prompt": parsed["negative_prompt"],
    }


def generate_illustration(prompt: str, negative_prompt: str) -> str:
    output = replicate.run(
        "black-forest-labs/flux-dev",
        input={
            "prompt": prompt,
        },
    )

    if isinstance(output, list):
        return str(output[0])

    return str(output)


def run_generation_job(job_id: str, payload: GenerateImageRequest) -> None:
    try:
        jobs[job_id]["status"] = "processing"

        llm_result = process_diary_with_llm(payload)

        illustration_url = generate_illustration(
            prompt=llm_result["prompt"],
            negative_prompt=llm_result["negative_prompt"],
        )

        jobs[job_id] = {
            "job_id": job_id,
            "status": "completed",
            "illustration_url": illustration_url,
            "summary": llm_result["summary"],
            "prompt": llm_result["prompt"],
            "negative_prompt": llm_result["negative_prompt"],
            "error": None,
        }

    except Exception as e:
        jobs[job_id]["status"] = "failed"
        jobs[job_id]["error"] = str(e)


@app.post("/generate-image", response_model=GenerateImageJobCreateResponse)
def create_generate_image_job(
    payload: GenerateImageRequest,
    background_tasks: BackgroundTasks,
) -> GenerateImageJobCreateResponse:
    job_id = str(uuid.uuid4())

    jobs[job_id] = {
        "job_id": job_id,
        "status": "pending",
        "illustration_url": None,
        "summary": None,
        "prompt": None,
        "negative_prompt": None,
        "error": None,
    }

    background_tasks.add_task(run_generation_job, job_id, payload)

    return GenerateImageJobCreateResponse(
        job_id=job_id,
        status="pending",
    )


@app.get("/generate-image/{job_id}", response_model=GenerateImageJobStatusResponse)
def get_generate_image_job(job_id: str) -> GenerateImageJobStatusResponse:
    job = jobs.get(job_id)

    if not job:
        raise HTTPException(status_code=404, detail="Job not found")

    return GenerateImageJobStatusResponse(**job)