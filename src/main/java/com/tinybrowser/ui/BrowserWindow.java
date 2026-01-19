package com.tinybrowser.ui;

import com.tinybrowser.dom.Node;
import com.tinybrowser.parser.HtmlParser;
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
    private TreeView<String> domTreeView;

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

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private TreeView<String> createDomTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("DOM Tree");
        rootItem.setExpanded(true);

        TreeItem<String> placeholderItem = new TreeItem<>("Load an HTML file to see DOM structure");
        rootItem.getChildren().add(placeholderItem);

        TreeView<String> treeView = new TreeView<>(rootItem);
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

            // Parse HTML (currently just a placeholder)
            HtmlParser parser = new HtmlParser();
            Node domRoot = parser.parse(htmlContent);

            // Update DOM tree view with placeholder
            updateDomTreeView(domRoot);

            showInfo("File loaded successfully: " + filePath);

        } catch (IOException e) {
            showError("Error reading file: " + e.getMessage());
            contentArea.setText("Error: Could not read file\n\n" + e.getMessage());
        }
    }

    private void updateDomTreeView(Node domRoot) {
        TreeItem<String> rootItem = new TreeItem<>("DOM Tree");
        rootItem.setExpanded(true);

        // For now, just show a placeholder message
        TreeItem<String> messageItem = new TreeItem<>("Parsed: " + domRoot.toString());
        messageItem.setExpanded(true);

        TreeItem<String> noteItem = new TreeItem<>("(Full DOM visualization coming soon)");
        messageItem.getChildren().add(noteItem);

        rootItem.getChildren().add(messageItem);
        domTreeView.setRoot(rootItem);
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
