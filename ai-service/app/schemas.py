from pydantic import BaseModel, Field


class GenerateImageRequest(BaseModel):
    user_id: str = Field(..., min_length=1, max_length=128)
    entry_date: str = Field(..., min_length=8, max_length=10)
    mood: str = Field(..., min_length=1, max_length=16)
    content: str = Field(..., min_length=1, max_length=5000)
    todo: str | None = Field(default=None, max_length=2000)
    reflection: str | None = Field(default=None, max_length=2000)


class GenerateImageResponse(BaseModel):
    illustration_url: str
    summary: str
    prompt: str
    negative_prompt: str
