import os

from .replicate_provider import generate_with_replicate
from .self_hosted_provider import generate_with_self_hosted


def generate_illustration(prompt: str, negative_prompt: str) -> str:
    provider = os.getenv("IMAGE_PROVIDER", "replicate")

    if provider == "replicate":
        return generate_with_replicate(prompt, negative_prompt)

    if provider == "self_hosted":
        return generate_with_self_hosted(prompt, negative_prompt)

    raise ValueError(f"Unsupported IMAGE_PROVIDER: {provider}")