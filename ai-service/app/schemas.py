from pydantic import BaseModel, Field

"""
"This file defines the data shapes for requests and reponses using Pydantic models. In FastAPI, these models are used to validate incoming data and structure outgoing data.

BaseModel comes from Pydantic. It automatically validate input data and converts it into a Python object.
"""


# define what the client must send when requesting image generation. Todo and reflection are optional
class GenerateImageRequest(BaseModel):
    user_id: str = Field(..., min_length=1, max_length=128)
    entry_date: str = Field(..., min_length=8, max_length=10)
    mood: str = Field(..., min_length=1, max_length=16)
    content: str = Field(..., min_length=1, max_length=5000)
    todo: str | None = Field(default=None, max_length=2000)
    reflection: str | None = Field(default=None, max_length=2000)

# This describes what the API returns
class GenerateImageResponse(BaseModel):
    illustration_url: str
    summary: str
    prompt: str
    negative_prompt: str
