import os
import uuid
from pathlib import Path

import torch
from diffusers import DiffusionPipeline

# choosing the model
MODEL_ID = os.getenv(
    "HF_MODEL_ID",
    "hf-internal-testing/tiny-stable-diffusion-pipe",
)
# let system know the project is running locally or in production
def get_public_base_url() -> str:
    public_base_url = os.getenv("IMAGE_SERVER_PUBLIC_BASE_URL")
    if public_base_url:
        return public_base_url

    app_env = os.getenv("APP_ENV", "local").lower()
    if app_env in {"local", "dev", "development"}:
        return "http://127.0.0.1:8001"

    raise RuntimeError(
        "IMAGE_SERVER_PUBLIC_BASE_URL is required outside local development. "
        "Set it to the public image-server URL, for example http://EC2_PUBLIC_IP:8001."
    )


PUBLIC_BASE_URL = get_public_base_url()

OUTPUT_DIR = Path(os.getenv("IMAGE_OUTPUT_DIR", "generated"))

# no model pipline has been loaded yet
_pipe = None

# choosing hardware
def get_device() -> str:
    if torch.cuda.is_available():
        return "cuda"

    if torch.backends.mps.is_available():
        return "mps"

    return "cpu"



# Load the Hugging face model once and reuse it 
def get_pipeline():
    global _pipe

    if _pipe is not None:
        return _pipe

    device = get_device()

    dtype = torch.float16 if device in {"cuda", "mps"} else torch.float32

    # This loads the model from Hugging Face.
    _pipe = DiffusionPipeline.from_pretrained(
        MODEL_ID,
        torch_dtype=dtype,
        safety_checker=None,
    requires_safety_checker=False,
    )

    # moves the model pipline to the selected hardware CPU or GPU
    _pipe = _pipe.to(device)

    return _pipe


def generate_with_huggingface(
    prompt: str,
    negative_prompt: str | None = None,
) -> str:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    pipe = get_pipeline()

    result = pipe(
        prompt=prompt,
        negative_prompt=negative_prompt,
        num_inference_steps=2,
    )

    image = result.images[0]

    filename = f"{uuid.uuid4()}.png"
    output_path = OUTPUT_DIR / filename
    image.save(output_path)

    return f"{PUBLIC_BASE_URL.rstrip('/')}/static/{filename}"