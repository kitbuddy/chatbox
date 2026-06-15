# Quick Start Guide

## Prerequisites Setup

### 1. Install Ollama
- Download from: https://ollama.ai
- After installation, start Ollama and pull the llama2 model:
  ```bash
  ollama run llama2
  ```
- This will download ~4GB and start the local LLM server

### 2. Get Weather API Key
- Visit: https://openweathermap.org/api
- Sign up for free (Free tier allows 1000 calls/day)
- Get your API key from Account Settings

## Running the Application

### Terminal 1: Start Backend

```bash
cd /Users/ankitjain/Desktop/projects/chatbox

# Edit application.properties with your API key
# Replace YOUR_API_KEY_HERE in: src/main/resources/application.properties

# Start the Spring Boot application
mvn spring-boot:run
```

Expected output:
```
. ____ _ __ _ _
/\\ / ___'_ __ _ _(_)_ __ __ _
( ( )\___ | '_ | '_| | '_ \/ _` |
\\/  ___)| |_)| | | | | || (_| |
 '  |____| .__|_| |_|_| |_\__, | |___/
 |_|
:: Spring Boot :: (v3.2.0)
Started ChatboxApplication in 12.345 seconds
```

### Terminal 2: Start Frontend

```bash
cd /Users/ankitjain/Desktop/projects/chatbox/frontend

# Install dependencies (first time only)
npm install

# Start development server
npm start
```

Expected output:
```
✔ Browser application bundle generation complete.
⠦ Building...

✔ Compiled successfully.

✔ Opening the application in the default browser...
```

### Terminal 3: Start Ollama (if not already running)

```bash
ollama run llama2
```

## Test the Application

1. Open browser: http://localhost:4200
2. You should see:
   - Chat interface with status "connected"
   - Welcome message from bot
3. Try these messages:
   - "What's the weather in London?"
   - "Tell me about the temperature in New York"
   - "How's the weather in Tokyo?"
   - Any other general question (will process without external API data)

## Troubleshooting

### Backend won't start
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill process on port 8080 if needed
kill -9 <PID>

# Or change port in src/main/resources/application.properties
# Add: server.port=9090
```

### Ollama connection error
```bash
# Check if Ollama is running
curl http://localhost:11434/api/status

# If not, make sure Ollama is running
ollama run llama2
```

### Frontend can't connect to backend
```bash
# Verify backend is running
curl http://localhost:8080/api/chat/health

# Should return: "Chatbox API is running"
```

### Weather API returns 401 error
```bash
# API key is invalid
# Get new key from: https://openweathermap.org/api
# Update: src/main/resources/application.properties
# weather.api.key=YOUR_NEW_KEY

# Rebuild and restart:
mvn clean compile
mvn spring-boot:run
```

## Build for Production

### Build Executable JAR
```bash
mvn clean package
java -jar target/chatbox-1.0-SNAPSHOT.jar
```

### Build Frontend for Production
```bash
cd frontend
npm run build
# Output: frontend/dist/chatbox-frontend
```

## Project Structure

```
chatbox/
├── README.md                        # Main documentation
├── QUICKSTART.md                    # This file
├── pom.xml                          # Maven config
├── setup.sh                         # Setup script
├── src/main/java/com/chatbox/
│   ├── ChatboxApplication.java      # Main Spring Boot app
│   ├── controller/ChatController.java
│   ├── service/ChatService.java
│   ├── service/OllamaService.java
│   ├── client/WeatherClient.java
│   ├── model/                       # DTOs
│   └── config/                      # Spring config
├── frontend/                        # Angular 20 app
│   ├── package.json
│   ├── angular.json
│   └── src/app/
├── target/                          # Compiled output
└── .git/                           # Git repository
```

## Architecture Summary

```
┌─────────────────┐
│  Angular 20 UI  │  (http://localhost:4200)
│  Chat Component │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  Spring Boot REST API       │  (http://localhost:8080)
│  POST /api/chat/message     │
│  GET /api/chat/health       │
└────────┬────────────────────┘
         │
         ├──────────────────────┐
         │                      │
         ▼                      ▼
┌──────────────────┐  ┌────────────────────┐
│  Ollama LLM      │  │  Weather API       │
│  localhost:11434 │  │  openweathermap.org│
└──────────────────┘  └────────────────────┘
```

## Advanced Configuration

### Use Different LLM Model

1. Pull alternative model:
   ```bash
   ollama pull mistral
   # or
   ollama pull neural-chat
   ```

2. Update configuration:
   ```properties
   # src/main/resources/application.properties
   ollama.model=mistral
   ```

3. Restart backend:
   ```bash
   mvn spring-boot:run
   ```

### Extend with More External APIs

1. Create new client in `src/main/java/com/chatbox/client/`
2. Add model classes in `src/main/java/com/chatbox/model/`
3. Update `ChatService.processMessage()` to call new API
4. Rebuild and restart

Example: Add News API, Stock Prices, Database Queries, etc.

## Performance Tips

- Ollama on first run will take time to initialize model
- Weather API calls are cached per conversation
- Frontend runs in development mode for faster iteration
- Use production build for deployment

## Support

Refer to README.md for detailed documentation and troubleshooting.
