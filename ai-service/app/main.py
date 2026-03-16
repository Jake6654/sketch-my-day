from fastapi import FastAPI
import json
import os

from dotenv import load_dotenv
from openai import OpenAI
from .schemas import GenerateImageRequest, GenerateImageResponse

"""
"This is the main FastAPI application file. It creates the app, defines API routes, receives requests, and returns responses
"""

load_dotenv()

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Creates the FastAPI app
app = FastAPI(
    title="Sketch My Day AI Service",
    description="AI service for diary analysis and illustration generation",
    version="0.1.0",
)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


# Image generation endpoint
def process_diary_with_llm(payload: GenerateImageRequest) -> dict[str, str]:
    system_prompt = """
You are an assistant that helps convert a user's diary entry into structured inputs for an illustration model.

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

# Call diffusion model
def generate_illustration(prompt: str, negative_prompt: str) -> str:
    """
    Temporary mock image generation logic.
    Later, replace this with a real diffusion model call.
    """
    return "https://picsum.photos/seed/sketch-my-day/1024/1024"

# separate logic and execution
@app.post("/generate-image", response_model=GenerateImageResponse)
def generate_image(payload: GenerateImageRequest) -> GenerateImageResponse:
    llm_result = process_diary_with_llm(payload)

    illustration_url = generate_illustration(
        prompt=llm_result["prompt"],
        negative_prompt=llm_result["negative_prompt"],
    )

    return GenerateImageResponse(
        illustration_url=illustration_url,
        summary=llm_result["summary"],
        prompt=llm_result["prompt"],
        negative_prompt=llm_result["negative_prompt"],
    )
