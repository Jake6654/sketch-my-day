# AWS EC2 GPU VM Setup Notes

Goal: run only the `image-server` FastAPI service on an AWS GPU EC2 instance.

The EC2 VM is mainly for:

- Hugging Face model loading
- PyTorch / Diffusers inference
- GPU-based image generation
- Returning generated image URLs to `ai-service`

The `ai-service` can stay local or run elsewhere because it does not need a GPU.

---

## 1. Region

Recommended region:

```text
US East (N. Virginia) / us-east-1
```

Why:

- Usually has broad EC2/GPU support
- Common default region
- Good for learning AWS basics

Important:

- EC2 quotas are region-specific.
- If you request GPU quota in `us-east-1`, it applies to that region, not all regions.

---

## 2. AMI

Recommended AMI:

```text
Deep Learning OSS Nvidia Driver AMI GPU PyTorch
Amazon Linux 2023
64-bit (x86)
```

Why:

- Already designed for ML workloads
- Includes NVIDIA/GPU-related setup
- Includes PyTorch environment
- Easier than plain Ubuntu for GPU setup

Avoid for now:

```text
Plain Ubuntu Server
```

Plain Ubuntu can work, but you would need to manually install:

- NVIDIA driver
- CUDA
- PyTorch GPU build
- ML dependencies

Architecture:

```text
64-bit (x86)
```

Do not choose Arm for this setup.

---

## 3. Instance Type

Recommended:

```text
g5.2xlarge
```

Good cheaper option:

```text
g5.xlarge
```

### g5.xlarge

Usually:

```text
1 NVIDIA A10G GPU
24 GB GPU memory
4 vCPU
16 GB RAM
```

Good for:

- Cheaper first GPU test
- SDXL Turbo experiments
- Basic Hugging Face image-server testing

### g5.2xlarge

Usually:

```text
1 NVIDIA A10G GPU
24 GB GPU memory
8 vCPU
32 GB RAM
```

Better for:

- More comfortable testing
- More RAM
- Running FastAPI + model server more smoothly

### Why G5?

G5 instances are GPU instances suitable for:

- ML inference
- Image generation
- PyTorch workloads
- Hugging Face Diffusers

---

## 4. EC2 Quota

For G5 instances, request this quota:

```text
Running On-Demand G and VT instances
```

Why:

- `g5.xlarge` and `g5.2xlarge` are G family GPU instances.
- AWS often measures this quota in vCPUs, not GPU count.

Example:

```text
g5.xlarge = 4 vCPU
g5.2xlarge = 8 vCPU
```

Recommended quota request:

```text
8 vCPU for one g5.2xlarge
16 vCPU for some extra room
```

This does not mean 16 GPUs. It means 16 vCPUs worth of G/VT instance capacity.

---

## 5. Key Pair

Key pair is required for SSH login.

AWS stores:

```text
public key
```

You download:

```text
private key (.pem)
```

Create new key pair:

```text
Name: sketch-my-day-ec2-key
Type: RSA
Format: .pem
```

After downloading:

```bash
mkdir -p ~/.ssh
mv ~/Downloads/sketch-my-day-ec2-key.pem ~/.ssh/
chmod 400 ~/.ssh/sketch-my-day-ec2-key.pem
```

SSH example for Amazon Linux:

```bash
ssh -i ~/.ssh/sketch-my-day-ec2-key.pem ec2-user@EC2_PUBLIC_IP
```

Why key pair is needed:

- EC2 usually does not use password login.
- The `.pem` private key proves you are allowed to access the VM.
- You need SSH to install, run, and debug the `image-server`.

Important:

- Do not commit `.pem` files to Git.
- If you lose the private key, you cannot download it again.

---

## 6. Network Settings

Default VPC is okay for learning.

Recommended:

```text
VPC: default
Subnet: no preference
Auto-assign public IP: enabled
```

Why public IP?

- Your local `ai-service` needs to call the EC2 `image-server`.
- You need to SSH into the instance from your Mac.

---

## 7. Security Group

A security group is a virtual firewall for your EC2 instance.

It controls inbound and outbound traffic.

Recommended inbound rules:

### SSH

```text
Type: SSH
Port: 22
Source: My IP
```

Why:

- Allows you to SSH into the EC2 instance.
- Restricting to `My IP` is safer than opening to everyone.

Avoid:

```text
SSH from 0.0.0.0/0
```

That allows SSH attempts from the entire internet.

### image-server API

```text
Type: Custom TCP
Port: 8001
Source: My IP
```

Why:

- Your FastAPI `image-server` will listen on port `8001`.
- Your local `ai-service` will call:

```text
http://EC2_PUBLIC_IP:8001/generate
```

Do not open port `8001` to the whole internet unless you know why.

### HTTP / HTTPS

For now:

```text
HTTP 80: disabled
HTTPS 443: disabled
```

Why:

- You are not using Nginx, a domain, or TLS yet.
- Your test server runs directly on port `8001`.

Later, if you add production-style hosting, you may use:

- Nginx
- HTTPS
- Domain name
- Load balancer

---

## 8. Storage

Recommended root EBS volume:

```text
150 GiB minimum
250 GiB recommended
Type: gp3
```

Why not 25 GiB?

25 GiB is too small for ML work.

Disk will be used by:

- OS files
- Docker images
- PyTorch
- Hugging Face model cache
- Generated images
- Logs

### gp3

`gp3` is a general-purpose SSD EBS volume.

Good for:

- Boot disk
- Docker
- Model cache
- Normal project workloads

For this project:

```text
gp3 is enough
```

---

## 9. File Systems

For now, choose:

```text
File systems: None
```

You do not need:

- S3 Files
- EFS
- FSx

### S3 Files

S3 is object storage.

Good for:

- Storing generated images long-term
- Serving image URLs
- Decoupling files from the VM

Not needed for the first EC2 test.

Later, generated images should probably move to S3.

### EFS

EFS is a shared network file system.

Good for:

- Multiple EC2 instances sharing the same files
- Shared app data across servers

Not needed now because you only have one GPU VM.

### FSx

FSx is a high-performance managed file system family.

Good for:

- Enterprise file workloads
- High-performance storage
- Specialized workloads

Overkill for this project right now.

---

## 10. What Runs On EC2?

For now, only run:

```text
image-server
```

The EC2 GPU VM should run:

- FastAPI image-server
- Hugging Face model
- PyTorch/Diffusers inference
- Generated image file serving

The local or separate `ai-service` does:

- OpenAI prompt generation
- Redis job tracking
- Calling image-server

Target architecture:

```text
ai-service
  -> OpenAI API
  -> http://EC2_PUBLIC_IP:8001/generate

EC2 GPU VM
  -> image-server
  -> Hugging Face model
  -> generated image URL
```

---

## 11. First Commands After Launch

SSH into the instance:

```bash
ssh -i ~/.ssh/sketch-my-day-ec2-key.pem ec2-user@EC2_PUBLIC_IP
```

Check GPU:

```bash
nvidia-smi
```

Check PyTorch GPU support:

```bash
python3 -c "import torch; print(torch.__version__); print(torch.cuda.is_available())"
```

Expected:

```text
True
```

If it prints `False`, PyTorch cannot use the GPU yet.

---

## Recommended Final Settings

```text
Region:
us-east-1

AMI:
Deep Learning OSS Nvidia Driver AMI GPU PyTorch
Amazon Linux 2023
64-bit x86

Instance:
g5.2xlarge
or g5.xlarge for cheaper testing

Quota:
Running On-Demand G and VT instances
Request 8 or 16 vCPUs

Storage:
150-250 GiB gp3

Key pair:
RSA .pem

Security group:
SSH 22 from My IP
Custom TCP 8001 from My IP

File systems:
None
```
