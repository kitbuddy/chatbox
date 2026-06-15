# Chatbox - Local LLM with External APIs

A full-stack chatbox application that integrates Java Spring Boot, Angular 20, LangChain4j, and Ollama with external API support.

## Architecture

```
User Interface (Angular 20)
        ↓
Spring Boot REST API (Java)
        ↓
    Intent Detection
        ↓
   External APIs (Weather, etc.) + Ollama LLM
        ↓
   Natural Language Response
        ↓
Angular Chat Display
```

## Prerequisites

- **Java 17+** - For Spring Boot backend
- **Maven 3.8+** - For building Java projects
- **Node.js 18+** & **npm** - For Angular frontend
- **Ollama** - Local LLM (download from https://ollama.ai)
- **Weather API Key** - Free from https://openweathermap.org/api

## Quick Start

### 1. Start Ollama (Local LLM)

```bash
# Download and install Ollama from https://ollama.ai
# Then run:
ollama run llama2
```

This will download and start the Llama 2 model locally.

### 2. Backend Setup

```bash
# Navigate to project root
cd /Users/ankitjain/Desktop/projects/chatbox

# Update weather API configuration
# Edit: src/main/resources/application.properties
# Replace: weather.api.key=YOUR_API_KEY_HERE
# Get free API key from: https://openweathermap.org/api

# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start Angular development server
npm start
```

The frontend will start on `http://localhost:4200`

## Configuration

### Backend Configuration (`src/main/resources/application.properties`)

```properties
# Ollama
ollama.base-url=http://localhost:11434
ollama.model=llama2

# Weather API (get key from openweathermap.org)
weather.api.key=YOUR_API_KEY_HERE
weather.api.url=https://api.openweathermap.org/data/2.5/weather

# Server
server.port=8080
```

### Available Ollama Models

Common models you can use:
- `llama2` - Meta's Llama 2 (default, ~4GB)
- `mistral` - Mistral 7B (~5GB)
- `neural-chat` - Intel's Neural Chat (~5GB)
- `dolphin-mixtral` - Dolphin Mixtral (~26GB)

Change model in `application.properties`:
```
ollama.model=mistral
```

Then pull the model:
```bash
ollama pull mistral
```

## Usage

1. **Open the Chat Interface**
   - Navigate to `http://localhost:4200`
   - You should see a connected status indicator

2. **Ask Weather Questions**
   - "What's the weather in New York?"
   - "Tell me the temperature in London"
   - "How's the wind in Tokyo?"

3. **General Questions**
   - Ask any question and Ollama will process it
   - Responses are in English

## API Endpoints

### Chat Message
```
POST /api/chat/message
Content-Type: application/json

{
  "message": "What's the weather in Paris?",
  "userId": "user-1"
}

Response:
{
  "response": "Based on the weather data from Paris...",
  "apiData": "City: Paris\nTemperature: 15°C\n...",
  "timestamp": "2026-06-05T21:25:00",
  "status": "success"
}
```

### Health Check
```
GET /api/chat/health

Response:
Chatbox API is running
```

## Project Structure

```
chatbox/
├── pom.xml                          # Maven configuration
├── src/main/java/com/chatbox/
│   ├── ChatboxApplication.java      # Main Spring Boot app
│   ├── controller/
│   │   └── ChatController.java      # REST endpoints
│   ├── service/
│   │   ├── ChatService.java         # Chat orchestration
│   │   └── OllamaService.java       # Ollama integration
│   ├── client/
│   │   └── WeatherClient.java       # Weather API client
│   ├── model/
│   │   ├── ChatRequest.java
│   │   ├── ChatResponse.java
│   │   └── WeatherData.java
│   └── config/
│       └── RestTemplateConfig.java  # HTTP client config
├── src/main/resources/
│   └── application.properties       # Configuration
│
└── frontend/                        # Angular 20 application
    ├── package.json
    ├── angular.json
    ├── src/
    │   ├── main.ts
    │   ├── index.html
    │   ├── styles.css
    │   └── app/
    │       ├── app.component.ts
    │       ├── app.config.ts
    │       ├── services/
    │       │   └── chat.service.ts
    │       └── components/
    │           ├── chat.component.ts
    │           ├── chat.component.html
    │           └── chat.component.css
    └── tsconfig.json
```

## Features

✅ **Real-time Chat UI** - Modern, responsive chat interface  
✅ **External API Integration** - Weather API example  
✅ **Intent Detection** - Automatically detects weather queries  
✅ **Local LLM Processing** - Ollama for private, offline responses  
✅ **API Data Display** - Shows external API responses in chat  
✅ **Connection Status** - Visual indicator for backend connectivity  
✅ **Message History** - Persistent chat history in session  
✅ **Error Handling** - Graceful error messages  

## Troubleshooting

### Backend won't start
```bash
# Check if port 8080 is in use
lsof -i :8080

# Check Java version
java -version

# Should be Java 17+
```

### Ollama connection error
```bash
# Ensure Ollama is running
curl http://localhost:11434/api/status

# Start Ollama if not running
ollama run llama2
```

### Weather API returns 401
```bash
# Update your API key in application.properties
# Get free key from: https://openweathermap.org/api
```

### Frontend can't connect to backend
```bash
# Ensure backend is running on port 8080
curl http://localhost:8080/api/chat/health

# Check CORS settings in ChatController
```

### Node modules issues
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## Building for Production

### Backend
```bash
mvn clean package
java -jar target/chatbox-1.0-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
# Output in: dist/chatbox-frontend
```

## Adding More External APIs

1. **Create a new client** in `src/main/java/com/chatbox/client/`
2. **Add model classes** for API response in `src/main/java/com/chatbox/model/`
3. **Update ChatService** to detect keywords and call the new API
4. **Update the prompt** to include new data context

Example: Adding a News API
```java
// NewsClient.java
@Service
public class NewsClient {
    public NewsData getNewsByKeyword(String keyword) { ... }
}

// Update ChatService.java
if (lowerMessage.contains("news")) {
    NewsData news = newsClient.getNewsByKeyword(extractKeyword(message));
    apiData = newsClient.formatNewsData(news);
}
```

## Technologies Used

- **Backend**: Spring Boot 3.2, LangChain4j 0.31, Ollama
- **Frontend**: Angular 20, TypeScript, RxJS
- **External APIs**: OpenWeatherMap
- **Build**: Maven, npm
- **Java Version**: 17+

## License

MIT

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review the logs in console
3. Ensure all prerequisites are installed
4. Verify API keys and URLs are correct
