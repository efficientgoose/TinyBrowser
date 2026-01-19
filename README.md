# TinyBrowser

A toy browser engine built with Java 24 and JavaFX 21 to understand how browsers work internally.

## Features

- **JavaFX GUI** with dark theme
- **File loader** for HTML files
- **DOM tree visualization** (TreeView on left)
- **Content display area** (currently shows raw HTML)
- **Basic DOM classes** (Node, Element, TextNode)
- **HTML parser scaffolding** (placeholder implementation)

## Project Structure

```
TinyBrowser/
├── src/main/java/com/tinybrowser/
│   ├── Main.java                 # Entry point
│   ├── ui/
│   │   └── BrowserWindow.java   # JavaFX GUI window
│   ├── dom/
│   │   ├── Node.java            # Abstract base node class
│   │   ├── Element.java         # DOM element node
│   │   └── TextNode.java        # Text content node
│   ├── parser/
│   │   └── HtmlParser.java      # HTML parser (placeholder)
│   └── css/, layout/, render/, util/ # Future packages
├── src/main/resources/
│   └── sample.html              # Sample HTML file for testing
└── src/test/java/
    └── TinyBrowserTest.java     # JUnit 5 tests
```

## Requirements

- **Java 24** or later
- **Maven 3.9+**
- **JavaFX 21** (automatically downloaded by Maven)

## Running the Application

### Option 1: Using Maven (Recommended)

```bash
mvn javafx:run
```

This will:
1. Compile the project
2. Download JavaFX dependencies if needed
3. Launch the GUI window

### Option 2: Building and Running JAR

```bash
# Build the project
mvn clean package

# Run with JavaFX
mvn javafx:run
```

## Using the Application

1. **Launch the application** using `mvn javafx:run`
2. **Enter file path** in the text field (default: `src/main/resources/sample.html`)
3. **Click "Load"** or press Enter to load the HTML file
4. **View content** in the center text area
5. **View DOM tree** in the left TreeView (placeholder for now)

## Current Capabilities

- ✅ JavaFX GUI with dark theme (1200x800 window)
- ✅ File loading and display
- ✅ Basic DOM data structures
- ✅ DOM tree viewer (placeholder)
- ✅ JUnit 5 test suite
- ❌ HTML parsing (not yet implemented)
- ❌ CSS parsing (not yet implemented)
- ❌ Layout engine (not yet implemented)
- ❌ Rendering engine (not yet implemented)

## Testing

Run all tests:

```bash
mvn test
```

Current test coverage:
- Element creation and attributes
- TextNode creation
- Node hierarchy (appendChild, removeChild)
- HtmlParser instantiation

## Development Roadmap

1. **HTML Parser** - Tokenization and DOM tree construction
2. **CSS Parser** - Style parsing and computation
3. **Layout Engine** - Box model and positioning
4. **Rendering** - Canvas-based rendering
5. **JavaScript** - Basic scripting support (future)

## IDE Setup

### IntelliJ IDEA

1. Open the project folder
2. Maven will auto-import dependencies
3. Run configuration:
   - Main class: `com.tinybrowser.Main`
   - VM options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.graphics`
   - Or simply use: `mvn javafx:run`

### VS Code

1. Install "Extension Pack for Java"
2. Open the project folder
3. Use terminal: `mvn javafx:run`

## License

Educational project - MIT License

## Contributing

This is a learning project. Feel free to fork and experiment!
