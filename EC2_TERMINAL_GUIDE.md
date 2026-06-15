# EC2 Terminal Deployment Guide - Step-by-Step

## Prerequisites Checklist
- ✅ EC2 instance running (Amazon Linux 2 or Ubuntu 20.04+)
- ✅ t3.xlarge or larger instance type
- ✅ 50GB+ EBS volume
- ✅ Security group configured:
  - Port 8080 (Chatbox API)
  - Port 11434 (Ollama)
  - Port 22 (SSH)
- ✅ SSH key pair saved locally
- ✅ Connected via SSH terminal

---

## STEP 1: Connect to EC2 Instance

### If not already connected:

```bash
# From your local machine terminal
chmod 400 your-key-pair.pem
ssh -i your-key-pair.pem ec2-user@<EC2_PUBLIC_IP>

# For Ubuntu instead of Amazon Linux:
# ssh -i your-key-pair.pem ubuntu@<EC2_PUBLIC_IP>
```

**Expected output:** You should see an Amazon Linux or Ubuntu shell prompt.

---

## STEP 2: Update System Packages

### For Amazon Linux 2:
```bash
sudo yum update -y
sudo yum install -y git curl
```

### For Ubuntu:
```bash
sudo apt-get update -y
sudo apt-get install -y git curl
```

**What this does:** Updates system packages and installs git (for cloning repo) and curl (for health checks).

---

## STEP 3: Install Docker and Docker Compose

### For Amazon Linux 2:

```bash
# Install Docker
sudo yum install -y docker

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### For Ubuntu:

```bash
# Install Docker
sudo apt-get install -y docker.io docker-compose

# Verify installation
docker --version
docker-compose --version
```

**Expected output:** Should show Docker version and Docker Compose version.

---

## STEP 4: Configure Docker Permissions

```bash
# Add current user to docker group
sudo usermod -a -G docker $USER
newgrp docker

# Verify (should NOT show "Permission denied")
docker ps
```

**What this does:** Allows you to run Docker commands without using `sudo`.

---

## STEP 5: Start Docker Service

### For Amazon Linux 2:
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### For Ubuntu:
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

**Expected output:** No errors should be shown.

---

## STEP 6: Clone the Chatbox Repository

```bash
# Navigate to home directory
cd ~

# Clone repository (replace with your actual repo URL)
git clone https://github.com/YOUR_USERNAME/chatbox.git
cd chatbox

# List files to verify
ls -la
```

**Expected output:** Should see: `Dockerfile`, `docker-compose.yml`, `deploy.sh`, `pom.xml`, `frontend/`, `src/`, etc.

---

## STEP 7: Verify Repository Files

```bash
# Check key files exist
ls -la Dockerfile docker-compose.yml AWS_DEPLOYMENT.md deploy.sh

# View the docker-compose configuration
cat docker-compose.yml
```

**Expected output:** All files should exist and be readable.

---

## STEP 8: Configure Environment Variables (Optional but Recommended)

```bash
# Copy environment template
cp .env.aws .env.local

# Edit with your settings (optional)
nano .env.aws
# Or use vim if you prefer
# vim .env.aws

# Look for these and update if needed:
# - WEATHER_API_KEY=your_api_key_here
# - OLLAMA_MODEL=llama2
# - JAVA_OPTS=-Xmx2g -Xms1g

# Press CTRL+X then Y to save in nano
```

**What this does:** Customizes the application for your environment.

---

## STEP 9: Build Docker Image and Start Containers

```bash
# Build and start containers (this takes 10-30 minutes depending on internet)
docker-compose up -d --build

# Check container status
docker-compose ps

# View build progress
docker-compose logs -f
```

**Expected output:**
- Status should show containers are "Up"
- Logs should show Spring Boot starting
- Wait until you see "Chatbox API is running" or similar

**What this does:**
- Builds Docker image from Dockerfile (Maven compiles Java, Node builds Angular)
- Starts two containers: `chatbox-backend` and `chatbox-ollama`
- Exposes ports 8080 and 11434

---

## STEP 10: Wait for Services to Be Ready

```bash
# Check health status (wait ~30-60 seconds)
watch docker-compose ps

# Press CTRL+C to exit watch mode
```

**Expected output:** Health status should change from `(health: starting)` to `(healthy)`.

---

## STEP 11: Pull Ollama Model

```bash
# Pull the default llama2 model (takes 5-30 minutes depending on size)
docker exec chatbox-ollama ollama pull llama2

# Monitor progress
docker-compose logs -f ollama
```

**Expected output:** You'll see download progress. Example:
```
pulling manifest
pulling 975ef3f5cbf7
downloading 3.3 GiB / 3.3 GiB
...
success
```

**Note:** This step takes time! Don't interrupt it. Monitor the progress.

---

## STEP 12: Verify Health Checks

```bash
# Test Chatbox API health
curl http://localhost:8080/api/chat/health

# Test Ollama health
curl http://localhost:11434/api/status
```

**Expected output:**
```
Chatbox API is running
{"status":"success"}
```

---

## STEP 13: Get Your EC2 Public IP

```bash
# Get public IP of your EC2 instance
curl http://169.254.169.254/latest/meta-data/public-ipv4

# Or check via AWS Console:
# EC2 Dashboard → Instances → Select your instance → Public IPv4
```

**Example output:** `54.123.45.678`

---

## STEP 14: Access the Application

Open your browser and go to:
```
http://<YOUR_EC2_PUBLIC_IP>:8080
```

Example: `http://54.123.45.678:8080`

**Expected:** You should see the Chatbox chat interface with a message input box.

---

## STEP 15: Test the Application

1. Type a test message in the chat box
2. Press Enter
3. Wait for response from Ollama
4. If you see a response, deployment is successful! ✅

**Example:**
- Input: "Hello, what's 2+2?"
- Output: "2+2 equals 4" (or similar response)

---

## STEP 16: View Logs and Monitor

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f chatbox      # Backend logs
docker-compose logs -f ollama       # Ollama logs

# Check resource usage
docker stats

# Check disk space
df -h

# Press CTRL+C to stop viewing logs
```

---

## Common Issues & Fixes

### Container won't start
```bash
# Check logs for errors
docker-compose logs

# Restart containers
docker-compose restart

# If still failing, rebuild
docker-compose down
docker-compose up -d --build
```

### Out of memory error
```bash
# Reduce Java memory in docker-compose.yml
# Change: JAVA_OPTS=-Xmx2g -Xms1g
# To: JAVA_OPTS=-Xmx1g -Xms512m

# Then restart
docker-compose down
docker-compose up -d --build
```

### Can't access from browser
```bash
# Check if containers are running
docker-compose ps

# Check if ports are open
sudo netstat -tlnp | grep 8080
sudo netstat -tlnp | grep 11434

# Verify security group in AWS Console
# EC2 → Security Groups → Check inbound rules
```

### Model download fails
```bash
# Check disk space
df -h

# Check if Ollama container is running
docker-compose ps ollama

# Try pulling model again
docker exec chatbox-ollama ollama pull llama2

# View Ollama logs
docker-compose logs -f ollama
```

---

## Useful Commands Reference

```bash
# Container management
docker-compose ps                    # Show container status
docker-compose logs -f               # View live logs
docker-compose restart               # Restart all containers
docker-compose down                  # Stop all containers
docker-compose up -d --build         # Rebuild and start

# Testing
curl http://localhost:8080/api/chat/health
curl http://localhost:11434/api/status

# System info
df -h                                # Disk usage
docker stats                         # Container resource usage
free -h                              # Memory usage
```

---

## Deployment Complete! 🎉

If you can access the application at `http://<EC2_IP>:8080` and send messages successfully, your deployment is complete.

### Next Steps:

1. **Keep monitoring logs**
   ```bash
   docker-compose logs -f
   ```

2. **Set up persistent backups** (Optional)
   ```bash
   docker exec chatbox-ollama tar czf - /root/.ollama > ollama-backup.tar.gz
   ```

3. **Configure domain** (If you have a domain)
   - Point domain DNS to your EC2 public IP
   - Access via `http://yourdomain.com:8080`

4. **Set up SSL/HTTPS** (For production)
   - Use AWS Certificate Manager
   - Configure with Application Load Balancer

5. **Monitor in CloudWatch**
   - AWS Console → CloudWatch → View logs

---

## Troubleshooting Quick Links

If you encounter issues, check:
- **AWS_DEPLOYMENT.md** - Comprehensive deployment guide
- **DOCKER_QUICKSTART.md** - Docker commands reference
- Container logs: `docker-compose logs`
- Health check: `curl http://localhost:8080/api/chat/health`
