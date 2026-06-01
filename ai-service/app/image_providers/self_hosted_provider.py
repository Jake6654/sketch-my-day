import os

import requests


def generate_with_self_hosted(prompt: str, negative_prompt: str) -> str:
    base_url = os.getenv("IMAGE_MODEL_SERVER_URL")
    timeout = int(os.getenv("IMAGE_MODEL_TIMEOUT_SECONDS", "300"))

    if not base_url:
        raise ValueError(
            "IMAGE_MODEL_SERVER_URL is required when IMAGE_PROVIDER=self_hosted"
        )
    
    response = requests.post(
        # This builds the final endpoint URL. 
        # It works whether your env var has a trailing slash or not
        f"{base_url.rstrip('/')}/generate",
        json={
            "prompt": prompt,
            "negative_prompt": negative_prompt,
        },
        timeout=timeout,
    )
    response.raise_for_status()

    data = response.json()

    if "image_url" not in data:
        raise ValueError("Self-hosted image server response is missing image_url")

    return data["image_url"]
