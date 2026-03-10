from fastapi import FastAPI

from .schemas import GenerateImageRequest, GenerateImageResponse

app = FastAPI(
    title="Sketch My Day AI Service",
    description="AI service for diary analysis and illustration generation",
    version="0.1.0",
)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/generate-image", response_model=GenerateImageResponse)
def generate_image(payload: GenerateImageRequest) -> GenerateImageResponse:
    # Mock response for integration testing with backend.
    prompt = (
        "soft watercolor diary illustration, cozy desk at night, "
        f"{payload.mood} atmosphere, journal writing scene"
    )
    negative_prompt = "blurry, bad anatomy, extra fingers, text, watermark"
    summary = f"{payload.entry_date} diary illustration generated from journal content."

    return GenerateImageResponse(
        illustration_url="https://picsum.photos/seed/sketch-my-day/1024/1024",
        summary=summary,
        prompt=prompt,
        negative_prompt=negative_prompt,
    )
