import replicate


def generate_with_replicate(prompt: str, negative_prompt: str) -> str:
    output = replicate.run(
        "black-forest-labs/flux-dev",
        input={
            "prompt": prompt,
        },
    )

    return str(output[0]) if isinstance(output, list) else str(output)