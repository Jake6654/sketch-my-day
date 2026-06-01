from pydantic import BaseModel, Field


# the image serve expects a prompt. negative prompt is optional
class GenerateImageRequest(BaseModel):
    prompt: str = Field(..., min_length=1, max_length=10000)
    negative_prompt: str | None = Field(default=None, max_length=5000)

# The image server returns image_url
class GenerateImageResponse(BaseModel):
    image_url: str
