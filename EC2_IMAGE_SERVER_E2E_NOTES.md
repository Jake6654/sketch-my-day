# EC2 Image Server End-to-End Notes

This document records the current working setup for connecting the local `ai-service` to the AWS EC2 GPU `image-server`.

Current successful architecture:

```text
Local Mac
  -> ai-service FastAPI
  -> Redis job tracking
  -> OpenAI prompt generation
  -> calls EC2 image-server

AWS EC2 GPU VM
  -> image-server FastAPI
  -> Hugging Face Diffusers
  -> PyTorch CUDA inference
  -> generated image saved locally
  -> image served through /static
```

---

## 1. What Runs Where

### Local machine

The local machine currently runs:

```text
ai-service
Redis
```

The `ai-service` is responsible for:

- Receiving `POST /generate-image`
- Creating a Redis job
- Calling OpenAI to create `summary`, `prompt`, and `negative_prompt`
- Calling the self-hosted image server
- Saving the final job result in Redis

### AWS EC2 GPU VM

The EC2 instance currently runs:

```text
image-server
```

The `image-server` is responsible for:

- Receiving `POST /generate`
- Loading the Hugging Face image model
- Running image generation on the NVIDIA A10G GPU
- Saving generated PNG files to `generated/`
- Serving generated files through `/static`
- Returning `image_url`

The `ai-service` is not deployed to the GPU VM at this stage because it does not need GPU compute.

---

## 2. EC2 GPU Status

The EC2 instance successfully detected the GPU with:

```bash
nvidia-smi
```

Confirmed GPU:

```text
NVIDIA A10G
CUDA visible
GPU memory around 23 GB
```

PyTorch GPU check:

```bash
python -c "import torch; print(torch.__version__); print(torch.cuda.is_available()); print(torch.cuda.get_device_name(0))"
```

Successful output:

```text
2.6.0+cu124
True
NVIDIA A10G
```

This confirmed that PyTorch can use the EC2 GPU.

---

## 3. EC2 Python Environment

The image server uses a Python virtual environment on EC2:

```bash
source ~/venvs/image-server/bin/activate
```

The shell prompt should show:

```text
(image-server)
```

The project is located at:

```text
~/apps/sketch-my-day/image-server
```

Move into it with:

```bash
cd ~/apps/sketch-my-day/image-server
```

---

## 4. Generated Image Directory

The `image-server` saves generated images in:

```text
image-server/generated/
```

Create it if needed:

```bash
mkdir -p generated
```

Why:

- Hugging Face provider saves PNG files here.
- FastAPI serves this directory through `/static`.

Example:

```text
generated/example.png
```

is served as:

```text
http://EC2_PUBLIC_DNS:8001/static/example.png
```

---

## 5. Running image-server On EC2

Run this from the EC2 `image-server` directory:

```bash
cd ~/apps/sketch-my-day/image-server
source ~/venvs/image-server/bin/activate

IMAGE_SERVER_PROVIDER=huggingface \
IMAGE_SERVER_PUBLIC_BASE_URL=http://ec2-3-91-147-95.compute-1.amazonaws.com:8001 \
HF_MODEL_ID=stabilityai/sdxl-turbo \
python -m uvicorn app.main:app --host 0.0.0.0 --port 8001
```

### Why `IMAGE_SERVER_PROVIDER=huggingface`

This tells `image-server` to use the Hugging Face provider instead of the mock provider.

### Why `IMAGE_SERVER_PUBLIC_BASE_URL`

The server uses this value to build public image URLs.

Example returned URL:

```text
http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/static/2c731984-68c6-4123-8e6f-24a344ef2cd7.png
```

### Why `HF_MODEL_ID=stabilityai/sdxl-turbo`

This selects the Hugging Face model used by Diffusers.

### Why `--host 0.0.0.0`

This allows traffic from outside the EC2 instance.

If the server used:

```bash
--host 127.0.0.1
```

then it would only be reachable from inside the EC2 instance.

### Why `--port 8001`

The project uses port `8001` for `image-server`.

The EC2 security group allows inbound TCP traffic on port `8001` from the local machine's IP.

---

## 6. Testing image-server On EC2

### Health check from EC2

```bash
curl -sS http://127.0.0.1:8001/health
```

Expected:

```json
{"status":"ok"}
```

### Generate test from EC2

```bash
curl -sS -X POST "http://127.0.0.1:8001/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "soft watercolor diary illustration of a calm evening desk",
    "negative_prompt": "blurry, watermark, distorted"
  }'
```

Expected:

```json
{
  "image_url": "http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/static/....png"
}
```

---

## 7. Testing image-server From Local Mac

From the local Mac:

```bash
curl -sS http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/health
```

Expected:

```json
{"status":"ok"}
```

Generate test:

```bash
curl -sS -X POST "http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "soft watercolor diary illustration of a calm evening desk",
    "negative_prompt": "blurry, watermark, distorted"
  }'
```

Expected:

```json
{
  "image_url": "http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/static/....png"
}
```

Open the returned `image_url` in a browser to confirm that the image is served correctly.

---

## 8. Security Group Requirements

The EC2 security group should allow inbound traffic from the local machine's IP.

Required inbound rules:

```text
Type: SSH
Port: 22
Source: My IP
```

```text
Type: Custom TCP
Port: 8001
Source: My IP
```

Why:

- Port `22` is needed for SSH.
- Port `8001` is needed for calling the FastAPI image-server from the local machine or local `ai-service`.

Avoid opening these to the entire internet unless there is a specific reason:

```text
0.0.0.0/0
```

---

## 9. Local ai-service Configuration

On the local Mac, update:

```text
ai-service/.env
```

Use:

```env
IMAGE_PROVIDER=self_hosted
IMAGE_MODEL_SERVER_URL=http://ec2-3-91-147-95.compute-1.amazonaws.com:8001
IMAGE_MODEL_TIMEOUT_SECONDS=300
```

Meaning:

```text
IMAGE_PROVIDER=self_hosted
```

The local `ai-service` should call a self-hosted image server instead of Replicate.

```text
IMAGE_MODEL_SERVER_URL=http://ec2-3-91-147-95.compute-1.amazonaws.com:8001
```

The self-hosted image server is the EC2 FastAPI server.

```text
IMAGE_MODEL_TIMEOUT_SECONDS=300
```

Image generation can be slow, so the HTTP client waits up to 300 seconds.

---

## 10. Running Local Redis

The local `ai-service` needs Redis for job status storage.

From the local Mac:

```bash
cd /Users/jake/Documents/my-project/sketch-my-day/ai-service
docker compose up -d redis
```

Why:

- `POST /generate-image` creates a job in Redis.
- `GET /generate-image/{job_id}` reads job status from Redis.

---

## 11. Running Local ai-service

From the local Mac:

```bash
cd /Users/jake/Documents/my-project/sketch-my-day/ai-service
source .venv/bin/activate
python -m uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload
```

Health check:

```bash
curl -sS http://127.0.0.1:8000/health
```

Expected:

```json
{"status":"ok"}
```

---

## 12. Full End-to-End Test

From the local Mac:

```bash
curl -sS -X POST "http://127.0.0.1:8000/generate-image" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "test-user",
    "entry_date": "2026-06-14",
    "mood": "focused",
    "content": "Today I connected my local AI service to an EC2 GPU image server.",
    "todo": "[]",
    "reflection": "The system should now generate a real image on the GPU VM."
  }'
```

Successful initial response:

```json
{
  "job_id": "bfa392fa-31e4-4c45-b9b2-7a3f1bda5d6c",
  "status": "pending"
}
```

Poll the job:

```bash
curl -sS "http://127.0.0.1:8000/generate-image/JOB_ID"
```

Successful final response:

```json
{
  "job_id": "bfa392fa-31e4-4c45-b9b2-7a3f1bda5d6c",
  "status": "completed",
  "illustration_url": "http://ec2-3-91-147-95.compute-1.amazonaws.com:8001/static/2c731984-68c6-4123-8e6f-24a344ef2cd7.png",
  "summary": "Today I connected my local AI service to an EC2 GPU image server and am focused on generating real images on the GPU VM.",
  "prompt": "A focused person working diligently on a laptop in a modern home office, with code and AI service connection visuals on the screen. The environment includes tech gadgets, a coffee cup, and a window showing a calm day outside. The scene captures concentration and technical progress related to AI and cloud GPU computing.",
  "negative_prompt": "blurry, low resolution, distorted, dark, cluttered background, cartoonish, messy, unrelated objects",
  "error": null
}
```

This confirms the full flow:

```text
local ai-service
  -> Redis job created
  -> OpenAI prompt generated
  -> EC2 image-server called
  -> Hugging Face generated image
  -> image URL returned
  -> Redis job marked completed
```

---

## 13. Current Success Criteria

The following have been confirmed:

```text
EC2 image-server runs
Hugging Face model downloads
PyTorch uses NVIDIA A10G
POST /generate returns image_url
/static image URL is accessible
local ai-service calls EC2 image-server
Redis job reaches completed status
final illustration_url points to EC2-generated image
```

---

## 14. Important Notes

### EC2 server is currently manually started

The current command runs Uvicorn in the foreground.

If the SSH session closes, the server may stop.

Better future options:

- `tmux`
- `nohup`
- `systemd`
- Docker container

### EC2 public DNS may change

If the instance is stopped and started again, the public DNS/IP may change unless using an Elastic IP.

If it changes, update:

```text
IMAGE_MODEL_SERVER_URL
IMAGE_SERVER_PUBLIC_BASE_URL
```

### Stop EC2 when not using it

GPU EC2 instances are expensive.

When done testing, stop the instance from the AWS console.

Stopping the instance stops compute/GPU charges, but EBS storage charges still remain.

---

## Next Recommended Step

Make the EC2 image-server run more reliably.

Recommended next step:

```text
Run image-server inside tmux
```

Then later:

```text
Create a systemd service or Docker-based deployment
```
