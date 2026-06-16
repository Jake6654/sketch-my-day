FROM python:3.11-slim

WORKDIR /app
# Copy a local file into a container
COPY requirements.txt .
# Run this command while building an image
RUN pip install --no-cache-dir -r requirements.txt

# 전체 프로젝트를 복사
COPY . .

EXPOSE 8000

# Run this command while executhing a container
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]