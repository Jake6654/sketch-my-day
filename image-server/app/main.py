from fastapi import FastAPI

from .schemas import GenerateImageRequest, GenerateImageResponse

app = FastAPI(
    title="Sketch My Day Image Server",
    description="Self-hosted image generation server",
    version="0.1.0",
)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/generate", response_model=GenerateImageResponse)
def generate_image(payload: GenerateImageRequest) -> GenerateImageResponse:
    # For now, every request returns the same fake image URL.
    return GenerateImageResponse(
        image_url="https://picsum.photos/seed/sketch-my-day-mock/1024/1024"
    )
