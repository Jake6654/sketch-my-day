from pathlib import Path

from fastapi import FastAPI

from .schemas import GenerateImageRequest, GenerateImageResponse
from .image_providers import generate_image_url
from fastapi.staticfiles import StaticFiles

app = FastAPI(
    title="Sketch My Day Image Server",
    description="Self-hosted image generation server",
    version="0.1.0",
)

# This means files inside generated/ can be accessed through 
# http://127.0.0.1:8001/static/filename.png
generated_dir = Path(__file__).resolve().parent.parent / "generated"
generated_dir.mkdir(parents=True, exist_ok=True)
app.mount("/static", StaticFiles(directory=str(generated_dir)), name="static")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}

# Now main.py only handles HTTP routing
# The provider handles image generation
@app.post("/generate", response_model=GenerateImageResponse)
def generate_image(payload: GenerateImageRequest) -> GenerateImageResponse:
    image_url = generate_image_url(
        prompt=payload.prompt,
        negative_prompt=payload.negative_prompt,
    )

    return GenerateImageResponse(image_url=image_url)