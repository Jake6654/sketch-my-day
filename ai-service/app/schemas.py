from pydantic import BaseModel, Field

# data schema for the initial request form
class GenerateImageRequest(BaseModel):
    user_id: str = Field(..., min_length=1, max_length=128)
    entry_date: str = Field(..., min_length=1, max_length=64)
    mood: str = Field(..., min_length=1, max_length=64)
    content: str = Field(..., min_length=1, max_length=5000)
    todo: str | None = Field(default=None, max_length=2000)
    reflection: str | None = Field(default=None, max_length=2000)


class GenerateImageResponse(BaseModel):
    illustration_url: str
    summary: str
    prompt: str
    negative_prompt: str


class GenerateImageJobCreateResponse(BaseModel):
    job_id: str
    status: str


class GenerateImageJobStatusResponse(BaseModel):
    job_id: str
    status: str
    illustration_url: str | None = None
    summary: str | None = None
    prompt: str | None = None
    negative_prompt: str | None = None
    error: str | None = None