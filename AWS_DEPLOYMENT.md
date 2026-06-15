# AWS EC2 Deployment Guide for Chatbox

This guide walks you through deploying the Chatbox application on an AWS EC2 instance using Docker.

## Prerequisites

- AWS Account with EC2 access
- EC2 instance running Amazon Linux 2 or Ubuntu 20.04+
- Instance type: **t3.xlarge or larger** (for Ollama model loading)
  - Minimum 4GB RAM for base system + 4-26GB for Ollama models
- Security group configured with:
  - Port 8080 (Chatbox API)
  - Port 11434 (Ollama API)
  - Port 22 (SSH, if needed)

## Step 1: Launch EC2 Instance

1. Go to AWS EC2 Dashboard
2. Click "Launch instances"
3. Select: **Amazon Linux 2 (AMI)** or **Ubuntu 20.04 LTS**
4. Instance type: **t3.xlarge** or larger
5. Storage: **50GB+ EBS volume**
6. Security Group: Allow inbound:
   - TCP 8080 (Chatbox)
   - TCP 11434 (Ollama)
   - TCP 22 (SSH)
7. Launch and save your key pair

## Step 2: Connect to EC2 Instance

```bash
# Using SSH from your local machine
chmod 400 your-key-pair.pem
ssh -i your-key-pair.pem ec2-user@<EC2_PUBLIC_IP>

# For Ubuntu, replace ec2-user with ubuntu
ssh -i your-key-pair.pem ubuntu@<EC2_PUBLIC_IP>
```

## Step 3: Install Docker and Docker Compose

### For Amazon Linux 2:
```bash
sudo yum update -y
sudo yum install -y docker git

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add ec2-user to docker group
sudo usermod -a -G docker ec2-user
newgrp docker
```

### For Ubuntu:
```bash
sudo apt-get update
sudo apt-get install -y docker.io docker-compose git

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add ubuntu to docker group
sudo usermod -a -G docker ubuntu
newgrp docker
```

## Step 4: Clone and Deploy Chatbox

```bash
# Clone the repository
git clone <your-repo-url> chatbox
cd chatbox

# Build and start containers
docker-compose up -d --build

# Check container status
docker-compose ps
```

## Step 5: Load Ollama Model

```bash
# Wait for Ollama container to be ready (check logs)
docker-compose logs -f ollama

# Pull the desired model (llama2 is default)
docker exec chatbox-ollama ollama pull llama2

# Verify Ollama is working
curl http://localhost:11434/api/status
```

## Step 6: Access the Application

Open your browser and navigate to:
```
http://<EC2_PUBLIC_IP>:8080
```

You should see the Chatbox chat interface.

## Monitoring and Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f chatbox
docker-compose logs -f ollama

# Check container health
docker-compose ps
```

## Configuration

### Environment Variables

Edit `docker-compose.yml` to adjust:
- `JAVA_OPTS`: JVM memory settings
- `OLLAMA_HOST`: Ollama server URL
- Model size and type

Example for t3.xlarge:
```yaml
environment:
  - JAVA_OPTS=-Xmx2g -Xms1g
  - OLLAMA_HOST=http://ollama:11434
```

### Weather API Configuration

Update `src/main/resources/application.properties`:
```properties
weather.api.key=YOUR_API_KEY_HERE
```

Then rebuild:
```bash
docker-compose up -d --build chatbox
```

## Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs chatbox

# Restart containers
docker-compose down
docker-compose up -d --build
```

### Out of memory errors
- Upgrade EC2 instance to larger type (t3.2xlarge)
- Reduce JVM memory: `-Xmx1g` in docker-compose.yml
- Use smaller model: `ollama pull mistral` instead of `dolphin-mixtral`

### Can't connect to application
```bash
# Verify containers running
docker-compose ps

# Check if ports are listening
sudo netstat -tlnp | grep 8080
sudo netstat -tlnp | grep 11434

# Check security group rules in AWS Console
```

### Ollama model download slow
- Large models (26GB) take time to download
- Monitor progress: `docker-compose logs -f ollama`
- Consider using smaller models (llama2, mistral)

## Performance Optimization

### For Production:

1. **Use larger instance**:
   ```bash
   # Recommended: t3.2xlarge or c5.2xlarge
   ```

2. **Allocate more memory to containers**:
   ```yaml
   # In docker-compose.yml
   environment:
     - JAVA_OPTS=-Xmx4g -Xms2g
   ```

3. **Use persistent storage**:
   ```bash
   # Create EBS volume for Ollama data
   docker volume create ollama-data
   ```

4. **Enable auto-restart**:
   ```yaml
   # Already configured with: restart: unless-stopped
   ```

## Backup and Restore

### Backup Ollama Models
```bash
# Backup volumes
docker exec chatbox-ollama tar czf - /root/.ollama > ollama-backup.tar.gz

# Restore from backup
docker exec -i chatbox-ollama tar xzf - < ollama-backup.tar.gz
```

## Stop and Cleanup

```bash
# Stop all containers
docker-compose down

# Remove volumes (WARNING: Deletes data)
docker-compose down -v

# Remove all Docker images
docker rmi $(docker images -q)
```

## Security Considerations

1. **Restrict Security Group**:
   - Only open ports 8080, 11434 to trusted IPs
   - Avoid opening to 0.0.0.0/0

2. **Use Elastic IP**:
   - Allocate static IP for your EC2 instance

3. **Monitor Logs**:
   - Regularly check `docker-compose logs`
   - Set up CloudWatch alarms

4. **Update Regularly**:
   ```bash
   cd chatbox
   git pull
   docker-compose up -d --build
   ```

## Support

For issues:
1. Check logs: `docker-compose logs -f`
2. Review AWS CloudWatch metrics
3. Verify security group configuration
4. Check EC2 instance disk space: `df -h`
