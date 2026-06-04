import os

from .mock_provider import generate_with_mock

# This gives the image-server its own provider
def generate_image_url(prompt: str, negative_prompt: str | None = None) -> str:
    provider = os.getenv("IMAGE_SERVER_PROVIDER", "mock")

    if provider == "mock":
        return generate_with_mock(prompt, negative_prompt)

    raise ValueError(f"Unsupported IMAGE_SERVER_PROVIDER: {provider}")