# Chatbox Docker & AWS Deployment Quick Reference

## Files Created

| File | Purpose |
|------|---------|
| `Dockerfile` | Multi-stage build for backend (Maven) and frontend (Node.js) |
| `docker-compose.yml` | Orchestrates chatbox backend + Ollama LLM containers |
| `.dockerignore` | Excludes unnecessary files from Docker image |
| `deploy.sh` | Automated EC2 deployment script |
| `AWS_DEPLOYMENT.md` | Complete AWS EC2 deployment guide |
| `.env.aws` | Environment variables template for AWS |

## Quick Start - Local Docker

```bash
# Build and run locally
docker-compose up -d --build

# Pull a model
docker exec chatbox-ollama ollama pull llama2

# Access at http://localhost:8080
```

## Quick Start - AWS EC2

```bash
# 1. SSH into EC2 instance
ssh -i your-key.pem ec2-user@<EC2_IP>

# 2. Clone and deploy
git clone <your-repo> chatbox
cd chatbox

# 3. Run automated deployment
bash deploy.sh

# 4. Pull model
docker exec chatbox-ollama ollama pull llama2

# 5. Access at http://<EC2_IP>:8080
```

## EC2 Instance Recommendations

| Metric | Recommendation |
|--------|-----------------|
| Instance Type | t3.xlarge (minimum) or t3.2xlarge (recommended) |
| Memory | 8GB minimum (4GB OS + 4GB Ollama) |
| Storage | 50GB+ EBS volume |
| Model Size | llama2 (~4GB), mistral (~5GB) |
| Max Model | dolphin-mixtral (~26GB, needs t3.2xlarge) |

## Available Ollama Models

```bash
# Fast models (recommended for EC2)
docker exec chatbox-ollama ollama pull llama2        # 4GB
docker exec chatbox-ollama ollama pull mistral       # 5GB
docker exec chatbox-ollama ollama pull neural-chat   # 5GB

# Large model (needs more resources)
docker exec chatbox-ollama ollama pull dolphin-mixtral  # 26GB
```

## Docker Commands

```bash
# View container status
docker-compose ps

# View logs
docker-compose logs -f                    # All
docker-compose logs -f chatbox            # Chatbox only
docker-compose logs -f ollama             # Ollama only

# Restart containers
docker-compose restart

# Stop containers
docker-compose down

# Rebuild after code changes
docker-compose up -d --build chatbox

# Check container health
docker-compose ps                         # Health status in HEALTH column
```

## Monitoring

```bash
# Health checks (should return success)
curl http://localhost:8080/api/chat/health    # Chatbox health
curl http://localhost:11434/api/status         # Ollama health

# Container resource usage
docker stats

# Disk usage
df -h                                         # EC2 disk space
docker system df                              # Docker disk usage
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Out of memory | Use t3.2xlarge, reduce `JAVA_OPTS=-Xmx1g` |
| Can't connect | Check security group ports 8080, 11434 |
| Slow startup | First model pull takes 5-30 min, check logs |
| Port in use | `sudo lsof -i :8080` or `lsof -i :11434` |
| Model download fails | Check disk space: `df -h` |

## Security Best Practices

```bash
# 1. Restrict security group to your IP
#    AWS Console → Security Groups → Edit inbound rules

# 2. Use Elastic IP (static IP for your EC2)
#    AWS Console → Elastic IPs

# 3. Enable auto-restart
#    Already configured in docker-compose.yml

# 4. Backup Ollama data
docker exec chatbox-ollama tar czf - /root/.ollama > backup.tar.gz

# 5. Monitor CloudWatch logs
#    AWS Console → CloudWatch → Logs
```

## Deployment Checklist

- [ ] EC2 instance launched (t3.xlarge+)
- [ ] Security group configured (ports 8080, 11434, SSH)
- [ ] SSH key pair saved locally
- [ ] Repository cloned to EC2
- [ ] `deploy.sh` executed successfully
- [ ] Ollama model pulled (`ollama pull llama2`)
- [ ] Application accessible at `http://<EC2_IP>:8080`
- [ ] Container health checks passing
- [ ] Weather API key configured (if using weather features)

## Cost Optimization

```yaml
# Reduce memory for cost
JAVA_OPTS=-Xmx1g -Xms512m    # Development
JAVA_OPTS=-Xmx2g -Xms1g      # Production t3.xlarge
JAVA_OPTS=-Xmx4g -Xms2g      # Production t3.2xlarge
```

## Scaling for Production

1. **Increase instance size**
   - t3.2xlarge for multiple models
   - c5.4xlarge for high concurrency

2. **Use Elastic Container Registry (ECR)**
   - Push image to AWS ECR
   - Deploy from ECR instead of building

3. **Add Application Load Balancer (ALB)**
   - Multi-AZ deployment
   - Auto-scaling groups

4. **RDS for future database**
   - PostgreSQL for chat history
   - ElastiCache for session storage

## Support Files

- **AWS_DEPLOYMENT.md** - Complete step-by-step guide
- **deploy.sh** - Automated setup script
- **.env.aws** - Environment template

## Next Steps

1. Review `AWS_DEPLOYMENT.md` for detailed instructions
2. Run `deploy.sh` on your EC2 instance
3. Pull an Ollama model
4. Test the application
5. Configure your domain/DNS if needed
