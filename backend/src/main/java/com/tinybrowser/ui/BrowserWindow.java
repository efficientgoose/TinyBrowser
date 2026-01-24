package com.tinybrowser.ui;

import com.tinybrowser.css.CssParser;
import com.tinybrowser.css.StyleSheet;
import com.tinybrowser.dom.Document;
import com.tinybrowser.dom.Element;
import com.tinybrowser.dom.Node;
import com.tinybrowser.dom.TextNode;
import com.tinybrowser.parser.HtmlParser;
import com.tinybrowser.style.StyleEngine;
import com.tinybrowser.style.StyledNode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BrowserWindow extends Application {

    private TextField filePathField;
    private Button loadButton;
    private TextArea contentArea;
    private TreeView<Object> domTreeView;
    private Label stylesLabel;
    private StyledNode styledNodeRoot;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TinyBrowser");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Top toolbar
        HBox toolbar = createToolbar();
        root.setTop(toolbar);

        // Left side - DOM Tree View
        domTreeView = createDomTreeView();
        root.setLeft(domTreeView);

        // Center - Content display area
        contentArea = createContentArea();
        root.setCenter(contentArea);

        // Bottom - Computed styles display
        stylesLabel = createStylesLabel();
        root.setBottom(stylesLabel);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createStylesLabel() {
        Label label = new Label("Computed Styles: Select an element in the DOM tree to see its computed styles");
        label.setStyle(
            "-fx-background-color: #252525; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-padding: 10; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 12px; " +
            "-fx-max-height: 150; " +
            "-fx-wrap-text: true;"
        );
        label.setMaxHeight(150);
        return label;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: #2d2d2d;");

        Label label = new Label("File Path:");
        label.setStyle("-fx-text-fill: #cccccc;");

        filePathField = new TextField();
        filePathField.setPromptText("Enter HTML file path or drag & drop here");
        filePathField.setPrefWidth(600);
        filePathField.setStyle(
            "-fx-background-color: #3c3c3c; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-prompt-text-fill: #888888;"
        );

        // Set default path to sample.html
        filePathField.setText("src/main/resources/sample.html");

        loadButton = new Button("Load");
        loadButton.setStyle(
            "-fx-background-color: #0e639c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 5 15 5 15;"
        );
        loadButton.setOnAction(e -> loadHtmlFile());

        // Allow Enter key to trigger load
        filePathField.setOnAction(e -> loadHtmlFile());

        HBox.setHgrow(filePathField, Priority.ALWAYS);
        toolbar.getChildren().addAll(label, filePathField, loadButton);

        return toolbar;
    }

    private TreeView<Object> createDomTreeView() {
        TreeItem<Object> rootItem = new TreeItem<>("DOM Tree");
        rootItem.setExpanded(true);

        TreeItem<Object> placeholderItem = new TreeItem<>("Load an HTML file to see DOM structure");
        rootItem.getChildren().add(placeholderItem);

        TreeView<Object> treeView = new TreeView<>(rootItem);
        treeView.setPrefWidth(300);
        treeView.setStyle(
            "-fx-background-color: #252525; " +
            "-fx-control-inner-background: #252525; " +
            "-fx-text-fill: #cccccc;"
        );

        return treeView;
    }

    private TextArea createContentArea() {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle(
            "-fx-control-inner-background: #1e1e1e; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-font-size: 13px;"
        );
        textArea.setText("Welcome to TinyBrowser!\n\n" +
                        "Load an HTML file to see its content here.\n" +
                        "The DOM tree will appear on the left.\n\n" +
                        "Default file: src/main/resources/sample.html");

        return textArea;
    }

    private void loadHtmlFile() {
        String filePath = filePathField.getText().trim();

        if (filePath.isEmpty()) {
            showError("Please enter a file path");
            return;
        }

        try {
            String htmlContent = Files.readString(Paths.get(filePath));

            // Display raw HTML in the content area
            contentArea.setText(htmlContent);

            // Parse HTML
            HtmlParser parser = new HtmlParser();
            Document doc = parser.parseDocument(htmlContent);
            Node domRoot = doc.getRootElement();

            // Extract CSS from <style> tags
            String extractedCss = extractCssFromDocument(domRoot);

            // Parse CSS
            CssParser cssParser = new CssParser();
            StyleSheet styleSheet = cssParser.parse(extractedCss);

            // Compute styles
            StyleEngine styleEngine = new StyleEngine();
            styledNodeRoot = styleEngine.computeStyles(doc, styleSheet);

            // Update DOM tree view
            updateDomTreeView(domRoot);

            showInfo("File loaded successfully: " + filePath + " (" + styleSheet.getRules().size() + " CSS rules)");

        } catch (IOException e) {
            showError("Error reading file: " + e.getMessage());
            contentArea.setText("Error: Could not read file\n\n" + e.getMessage());
        }
    }

    private String extractCssFromDocument(Node node) {
        StringBuilder css = new StringBuilder();

        if (node instanceof Element) {
            Element element = (Element) node;
            if ("style".equals(element.getTagName())) {
                // Extract text from style tag
                for (Node child : element.getChildren()) {
                    if (child instanceof TextNode) {
                        css.append(((TextNode) child).getText()).append("\n");
                    }
                }
            }
        }

        // Recursively extract from children
        for (Node child : node.getChildren()) {
            css.append(extractCssFromDocument(child));
        }

        return css.toString();
    }

    private void updateDomTreeView(Node domRoot) {
        TreeItem<Object> rootItem = new TreeItem<>("DOM Tree");
        rootItem.setExpanded(true);

        if (domRoot != null) {
            TreeItem<Object> domTreeItem = buildTreeItem(domRoot, styledNodeRoot);
            domTreeItem.setExpanded(true);
            rootItem.getChildren().add(domTreeItem);
        }

        domTreeView.setRoot(rootItem);

        // Add selection listener to show computed styles
        domTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue() instanceof NodeWithStyles) {
                NodeWithStyles nodeWithStyles = (NodeWithStyles) newValue.getValue();
                updateStylesDisplay(nodeWithStyles.styledNode);
            }
        });
    }

    private void updateStylesDisplay(StyledNode styledNode) {
        if (styledNode == null) {
            stylesLabel.setText("No styles computed");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Computed Styles:\n");

        var styles = styledNode.getComputedStyles();
        if (styles.isEmpty()) {
            sb.append("  (no styles)");
        } else {
            // Sort styles alphabetically for easier reading
            styles.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append("  ").append(entry.getKey())
                      .append(": ").append(entry.getValue())
                      .append(";\n");
                });
        }

        stylesLabel.setText(sb.toString());
    }

    // Helper class to store node label with associated StyledNode
    private static class NodeWithStyles {
        final String label;
        final StyledNode styledNode;

        NodeWithStyles(String label, StyledNode styledNode) {
            this.label = label;
            this.styledNode = styledNode;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private TreeItem<Object> buildTreeItem(Node node, StyledNode styledNode) {
        TreeItem<Object> treeItem;
        String labelStr;

        if (node instanceof Element) {
            Element element = (Element) node;
            StringBuilder label = new StringBuilder("<" + element.getTagName());

            // Add attributes to the label
            if (!element.getAttributes().isEmpty()) {
                for (var entry : element.getAttributes().entrySet()) {
                    label.append(" ").append(entry.getKey())
                         .append("=\"").append(entry.getValue()).append("\"");
                }
            }
            label.append(">");

            labelStr = label.toString();
            treeItem = new TreeItem<>(new NodeWithStyles(labelStr, styledNode));
            treeItem.setExpanded(true);

            // Add children
            if (styledNode != null) {
                int childIndex = 0;
                for (Node child : node.getChildren()) {
                    StyledNode childStyled = childIndex < styledNode.getChildren().size()
                        ? styledNode.getChildren().get(childIndex)
                        : null;
                    treeItem.getChildren().add(buildTreeItem(child, childStyled));
                    childIndex++;
                }
            } else {
                for (Node child : node.getChildren()) {
                    treeItem.getChildren().add(buildTreeItem(child, null));
                }
            }
        } else if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            String text = textNode.getText().trim();

            if (!text.isEmpty()) {
                // Truncate long text for display
                if (text.length() > 50) {
                    text = text.substring(0, 47) + "...";
                }
                labelStr = "TEXT: \"" + text + "\"";
            } else {
                labelStr = "TEXT: (whitespace)";
            }
            treeItem = new TreeItem<>(new NodeWithStyles(labelStr, styledNode));
        } else {
            labelStr = "Unknown node type";
            treeItem = new TreeItem<>(new NodeWithStyles(labelStr, styledNode));
        }

        return treeItem;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void launchApp(String[] args) {
        launch(args);
    }
}
