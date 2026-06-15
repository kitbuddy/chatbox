#!/bin/bash

# AWS EC2 Automated Deployment Script for Chatbox
# Run this on your EC2 instance: bash deploy.sh

set -e

echo "================================"
echo "Chatbox AWS EC2 Deployment Script"
echo "================================"

# Detect OS
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
fi

echo "[1/5] Installing dependencies..."

if [ "$OS" = "amzn" ] || [ "$OS" = "amazonlinux" ]; then
    echo "Detected Amazon Linux 2"
    sudo yum update -y > /dev/null 2>&1
    sudo yum install -y docker git > /dev/null 2>&1
elif [ "$OS" = "ubuntu" ]; then
    echo "Detected Ubuntu"
    sudo apt-get update > /dev/null 2>&1
    sudo apt-get install -y docker.io docker-compose git > /dev/null 2>&1
else
    echo "❌ Unsupported OS. Please install Docker and Docker Compose manually."
    exit 1
fi

echo "✓ Docker installed"

# Install Docker Compose if not available
if ! command -v docker-compose &> /dev/null; then
    echo "[2/5] Installing Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose > /dev/null 2>&1
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✓ Docker Compose installed"
else
    echo "[2/5] Docker Compose already installed"
fi

echo "[3/5] Starting Docker service..."
if [ "$OS" = "amzn" ] || [ "$OS" = "amazonlinux" ]; then
    sudo systemctl start docker > /dev/null 2>&1
    sudo systemctl enable docker > /dev/null 2>&1
    sudo usermod -a -G docker ec2-user > /dev/null 2>&1
elif [ "$OS" = "ubuntu" ]; then
    sudo systemctl start docker > /dev/null 2>&1
    sudo systemctl enable docker > /dev/null 2>&1
    sudo usermod -a -G docker ubuntu > /dev/null 2>&1
fi
echo "✓ Docker service started"

echo "[4/5] Cloning and building Chatbox..."
if [ ! -d "chatbox" ]; then
    echo "Enter your Git repository URL:"
    read REPO_URL
    git clone "$REPO_URL" chatbox > /dev/null 2>&1
else
    echo "Using existing chatbox directory"
fi

cd chatbox

echo "[5/5] Building Docker containers..."
docker-compose up -d --build > /dev/null 2>&1

echo ""
echo "================================"
echo "✅ Deployment Complete!"
echo "================================"
echo ""
echo "Next Steps:"
echo "1. Wait for containers to start (30-60 seconds)"
echo "2. Pull Ollama model:"
echo "   docker exec chatbox-ollama ollama pull llama2"
echo ""
echo "3. Access the application:"
echo "   http://$(hostname -I | awk '{print $1}'):8080"
echo ""
echo "Monitor logs:"
echo "   docker-compose logs -f"
echo ""
echo "For detailed guide, see AWS_DEPLOYMENT.md"
