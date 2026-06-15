#!/bin/bash

set -e

echo "================================"
echo "Chatbox Setup Script"
echo "================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java found:$(java -version 2>&1 | head -1)${NC}"

if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven found: $(mvn -v | head -1)${NC}"

if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Node.js found: $(node -v)${NC}"

if ! command -v npm &> /dev/null; then
    echo -e "${RED}✗ npm is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ npm found: $(npm -v)${NC}"

if ! command -v ollama &> /dev/null; then
    echo -e "${YELLOW}⚠ Ollama is not in PATH. Make sure it's installed from https://ollama.ai${NC}"
else
    echo -e "${GREEN}✓ Ollama found${NC}"
fi

echo ""
echo "Building backend..."
mvn clean install -q
echo -e "${GREEN}✓ Backend built successfully${NC}"

echo ""
echo "Setting up frontend..."
cd frontend
npm install -q
echo -e "${GREEN}✓ Frontend dependencies installed${NC}"

cd ..
echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "Next steps:"
echo "1. Make sure Ollama is running: ollama run llama2"
echo "2. Update your weather API key in: src/main/resources/application.properties"
echo "3. Start backend: mvn spring-boot:run"
echo "4. In another terminal, start frontend: cd frontend && npm start"
echo ""
echo "Then open: http://localhost:4200"
