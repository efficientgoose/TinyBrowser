# TinyBrowser - Hybrid Architecture

A browser engine with Java backend (HTML/CSS parsing) and Electron + React frontend.

## Architecture

- **Backend (Java)**: HTML parsing, CSS parsing, DOM construction, style computation
- **Frontend (Electron + React)**: Modern UI with DOM tree visualization and style inspector
- **Communication**: REST API (HTTP on port 8080)

## Features

- ✅ Full HTML parsing with tokenizer
- ✅ CSS parsing with selector matching
- ✅ Style engine with inheritance and specificity
- ✅ DOM tree visualization
- ✅ Computed styles inspector
- ✅ Modern Electron + React UI
- ✅ REST API for frontend-backend communication
- ✅ Dark theme matching VS Code

## Prerequisites

- Java 24
- Maven 3.9+
- Node.js 18+ and npm

## Running the Application

### 1. Start the Backend Server

```bash
cd backend
mvn clean compile exec:java
```

The backend will start on `http://localhost:8080`

You should see:
```
TinyBrowser backend server running on http://localhost:8080
API endpoint: POST http://localhost:8080/api/parse
```

### 2. Start the Electron Frontend

In a new terminal:

```bash
cd frontend
npm install  # Only needed first time
npm run electron:dev
```

This will:
- Build the Electron main process
- Start Vite development server
- Launch the Electron app

## Usage

1. The app opens with a default path to `../backend/src/main/resources/sample.html`
2. Click "Load" or press Enter to parse and display the HTML file
3. The DOM tree appears on the left (expandable/collapsible)
4. The raw HTML content appears in the center panel
5. Click any node in the tree to see its computed styles at the bottom

## Project Structure

```
TinyBrowser/
├── backend/                            # Java backend
│   ├── pom.xml                         # Maven configuration
│   └── src/main/java/com/tinybrowser/
│       ├── parser/                     # HTML tokenizer & parser
│       ├── css/                        # CSS tokenizer & parser
│       ├── dom/                        # DOM structures (Node, Element, TextNode)
│       ├── style/                      # Style engine with inheritance
│       ├── server/                     # REST API (Javalin)
│       ├── util/                       # JSON serializer
│       └── ui/                         # Original JavaFX UI (preserved)
│
└── frontend/                           # Electron frontend
    ├── src/
    │   ├── main/                       # Electron main process
    │   ├── preload/                    # Electron preload script
    │   └── renderer/                   # React UI
    │       ├── components/             # React components
    │       │   ├── Toolbar.tsx
    │       │   ├── DomTreeView.tsx
    │       │   ├── ContentArea.tsx
    │       │   └── StylesPanel.tsx
    │       ├── services/               # API client (Axios)
    │       ├── types/                  # TypeScript interfaces
    │       └── styles/                 # CSS (dark theme)
    │
    └── dist/                           # Build output
```

## API Endpoints

### GET /

Health check endpoint

### POST /api/parse

Parses an HTML file and returns the DOM tree with computed styles.

**Request:**
```json
{
  "filePath": "../backend/src/main/resources/sample.html"
}
```
Note: Path can be relative to where backend is running or absolute.

**Response:**
```json
{
  "htmlContent": "<!DOCTYPE html>...",
  "styledTree": {
    "type": "element",
    "tagName": "html",
    "attributes": {},
    "styles": {
      "color": "#000000",
      "display": "block",
      "font-size": "16px",
      ...
    },
    "children": [...]
  }
}
```

## Development

### Backend Development

All commands should be run from the `backend/` directory:

```bash
cd backend

# Run tests
mvn test

# Compile only
mvn compile

# Package as JAR
mvn package

# Run with JavaFX (original UI)
# (Modify Main.java to launch BrowserWindow.launchApp(args))
mvn javafx:run
```

### Frontend Development

```bash
cd frontend

# Development mode (with hot reload)
npm run dev

# Build for production
npm run build

# Type checking
npx tsc --noEmit
```

## Technology Stack

**Backend:**
- Java 24
- Maven 3.9
- Javalin 5.6 (HTTP server)
- Jackson 2.15 (JSON serialization)
- SLF4J (logging)

**Frontend:**
- Electron 28
- React 18
- TypeScript 5
- Vite 5
- Axios 1.6 (HTTP client)

## Testing

```bash
# Backend tests (run from backend directory)
cd backend
mvn test

# All tests still pass with hybrid architecture
```

Current test coverage:
- HTML tokenizer and parser
- CSS tokenizer and parser
- DOM structures
- Style engine
- Selector matching
- Specificity computation

## Sample HTML File

The project includes `backend/src/main/resources/sample.html` with various HTML elements and embedded CSS to demonstrate:
- Element hierarchy
- CSS selectors (type, class, id)
- Style inheritance
- Specificity resolution

## Notes

- The backend must be running before starting the frontend
- The frontend makes HTTP requests to `http://localhost:8080`
- All existing Java unit tests still pass
- The original JavaFX UI code is preserved in `backend/src/main/java/com/tinybrowser/ui/BrowserWindow.java`
- CORS is enabled on the backend for frontend communication

## Troubleshooting

**Backend won't start:**
- Make sure port 8080 is not in use
- Check Java version: `java -version` (should be 24)
- Try: `cd backend && mvn clean compile exec:java`

**Frontend won't connect:**
- Verify backend is running: `curl http://localhost:8080/`
- Check browser console for errors
- Ensure no firewall blocking localhost:8080

**Electron window is blank:**
- Check if Vite dev server is running on port 5173
- Look for errors in Electron DevTools (opens automatically in dev mode)

## License

Educational project - MIT License
