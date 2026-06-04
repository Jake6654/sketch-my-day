import hashlib

# Why use hash? Before every prompt returned the same fake image URL. After this, different prompts return different mock image URLs
def generate_with_mock(prompt: str, negative_prompt: str | None = None) -> str:
    seed = hashlib.sha256(prompt.encode("utf-8")).hexdigest()[:16]
    return f"https://picsum.photos/seed/{seed}/1024/1024"