FROM nvidia/cuda:12.4.1-cudnn-runtime-ubuntu22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV PYTHONUNBUFFERED=1

WORKDIR /app

RUN apt-get update && apt-get install -y \
    python3.11 \
    python3.11-venv \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN python3.11 -m pip install --upgrade pip

RUN python3.11 -m pip install torch torchvision --index-url https://download.pytorch.org/whl/cu124

RUN python3.11 -m pip install --no-cache-dir -r requirements.txt

COPY . .

RUN mkdir -p generated

EXPOSE 8001

CMD ["python3.11", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]