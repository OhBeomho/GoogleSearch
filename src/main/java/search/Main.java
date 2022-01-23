package search;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import search.animNodes.*;

import java.io.*;
import java.util.*;

public class Main extends Application {
    private static final int SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480;
    private static final File SETTINGS_FILE = new File("C:\\GoogleSearchSettings\\settings.txt");

    private final Scene scene;
    private final StackPane searchPane;
    private final StackPane settingsPane;

    // Search Pane
    private final AnimatedField searchField;
    private final AnimatedButton searchButton;
    private final AnimatedButton settingsButton;
    private final AnimatedButton clearResultsButton;
    private final VBox resultVBox;
    private final ScrollPane resultPane;

    // Settings Pane
    // nor = numberOfResults
    private final Slider norSlider;
    private final AnimatedButton okButton;
    private final AnimatedField norField;

    private int numberOfResults;
    private boolean confirmExit;
    private Pane currentPane;
    private final SimpleDoubleProperty stageHeight;

    public Main() {
        searchPane = new StackPane();
        settingsPane = new StackPane();
        currentPane = searchPane;

        scene = new Scene(searchPane, SCREEN_WIDTH, SCREEN_HEIGHT);

        searchField = new AnimatedField(300, 30, "Search Anything...");
        norField = new AnimatedField(50, 30, "");

        searchButton = new AnimatedButton(64, 30, "");
        settingsButton = new AnimatedButton(100, 20, "SETTINGS");
        okButton = new AnimatedButton(40, 30, "OK");
        clearResultsButton = new AnimatedButton(130,20,"CLEAR RESULTS");

        norSlider = new Slider();

        resultVBox = new VBox();

        resultPane = new ScrollPane();

        stageHeight = new SimpleDoubleProperty();

        loadSettings();
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(scene);
        stage.setTitle("SEARCH FROM GOOGLE");
        stage.setOnCloseRequest(e -> {
            if (confirmExit) {
                Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);

                confirmExit.setTitle("CONFIRM EXIT");
                confirmExit.setHeaderText("ARE YOU SURE YOU WANT TO QUIT?");

                confirmExit.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        saveSettings();
                        System.exit(0);
                    } else {
                        e.consume();
                    }
                });
            } else {
                saveSettings();
                System.exit(0);
            }
        });
        stageHeight.bind(stage.heightProperty());

        // Search Pane UI
        VBox searchVBox = new VBox();
        HBox searchHBox = new HBox();

        Label titleLabel = new Label("GOOGLE SEARCH");
        titleLabel.setFont(new Font(titleLabel.getFont().getFamily(), 32));

        resultPane.setContent(resultVBox);

        searchHBox.getChildren().addAll(searchField, searchButton);
        searchVBox.getChildren().addAll(titleLabel, searchHBox, resultPane);

        searchVBox.setAlignment(Pos.CENTER);
        searchHBox.setAlignment(Pos.CENTER);
        resultVBox.setAlignment(Pos.CENTER);

        resultVBox.setSpacing(10);
        searchVBox.setSpacing(10);
        searchHBox.setSpacing(4);

        ImageView buttonImage = new ImageView(new Image(
                "C:\\javaWork\\workspace\\GoogleSearchV2\\src\\main\\java\\search\\images\\searchButton.png"));
        buttonImage.setFitWidth(searchButton.getPrefHeight());
        buttonImage.setFitHeight(searchButton.getPrefHeight());
        searchButton.setGraphic(buttonImage);

        EventHandler<ActionEvent> searchEvent = e -> {
            if (searchField.getText().equals("")) {
                e.consume();
                return;
            }

            search(searchField.getText());
        };

        settingsButton.setOnAction(e -> switchPane(settingsPane));
        searchButton.setOnAction(searchEvent);
        searchField.setOnAction(searchEvent);
        clearResultsButton.setOnAction(e -> resultVBox.getChildren().clear());

        resultPane.maxHeightProperty().bind(stage.heightProperty().divide(2));

        searchPane.getChildren().addAll(searchVBox, settingsButton, clearResultsButton);

        StackPane.setAlignment(settingsButton, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(clearResultsButton,Pos.BOTTOM_LEFT);

        searchPane.setStyle("-fx-background-color: white;");

        // Settings Pane UI
        VBox settingsVBox = new VBox();
        HBox sliderHBox = new HBox(), confirmExitHBox = new HBox();

        sliderHBox.getChildren().addAll(new Label("NUMBER OF RESULTS TO DISPLAY: "), norSlider, norField);

        norField.setText(String.valueOf(numberOfResults));

        norField.setOnAction(e -> {
            numberOfResults = Integer.parseInt(norField.getText());
            norSlider.setValue(numberOfResults);
        });

        norField.setAlignment(Pos.CENTER);

        norSlider.setMax(100);
        norSlider.setMin(1);
        norSlider.setValue(numberOfResults);
        norSlider.setShowTickLabels(true);

        norSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberOfResults = newValue.intValue();
            norField.setText(String.valueOf(numberOfResults));
        });

        ChoiceBox<Boolean> setConfirmExit = new ChoiceBox<>(FXCollections.observableArrayList(true, false));
        setConfirmExit.getSelectionModel().select(confirmExit ? 0 : 1);
        setConfirmExit.valueProperty().addListener((observable, oldValue, newValue) -> confirmExit = newValue);

        confirmExitHBox.getChildren().addAll(new Label("CONFIRM EXIT: "), setConfirmExit);

        settingsVBox.getChildren().addAll(sliderHBox, confirmExitHBox, okButton);

        okButton.setOnAction(e -> switchPane(searchPane));

        settingsVBox.setAlignment(Pos.CENTER);
        sliderHBox.setAlignment(Pos.CENTER);
        confirmExitHBox.setAlignment(Pos.CENTER);

        settingsVBox.setSpacing(10);
        sliderHBox.setSpacing(5);
        confirmExitHBox.setSpacing(5);

        settingsPane.getChildren().add(settingsVBox);

        settingsPane.setStyle("-fx-background-color: lightgray;");

        stage.show();
    }

    private void switchPane(Pane nextPane) {
        currentPane.getChildren().add(nextPane);
        nextPane.setTranslateY(stageHeight.get());

        KeyValue value = new KeyValue(nextPane.translateYProperty(),0,Interpolator.SPLINE(1,0,0,1));
        KeyFrame frame = new KeyFrame(Duration.seconds(1.5), value);
        Timeline timeline = new Timeline(frame);
        timeline.play();
        timeline.setOnFinished(e -> {
            currentPane.getChildren().remove(nextPane);
            scene.setRoot(nextPane);
            currentPane = nextPane;
        });
    }

    private void saveSettings() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE));

            writer.write("nor=" + numberOfResults + "\nconfirmexit=" + confirmExit);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("nor=")) {
                    numberOfResults = Integer.parseInt(line.substring(4));
                } else if (line.startsWith("confirmexit=")) {
                    confirmExit = Boolean.parseBoolean(line.substring(12));
                }
            }

            reader.close();
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setHeaderText("FAILED TO LOAD SETTINGS.");
            error.setContentText("INSTEAD, IT IS SET TO THE DEFAULT.");
            error.show();
        }
    }

    private void search(String text) {
        Map<String, String> results;

        try {
            results = Search.search(text, numberOfResults);
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("ERROR");
            error.setHeaderText("FAILED TO SEARCH '" + text + "'.");
            error.setContentText("PLEASE TRY AGAIN.");
            error.show();
            return;
        }

        if (results.size() != 0) {
            VBox[] resultVBoxes = new VBox[results.size()];

            int i = 0;
            for (String title : results.keySet()) {
                VBox vbox = new VBox();
                String link = results.get(title);
                Hyperlink hyperlink = new Hyperlink(link);
                hyperlink.setOnAction(e -> {
                    Runtime runtime = Runtime.getRuntime();

                    try {
                        runtime.exec("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe " + link);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                vbox.getChildren().addAll(new Label(title), hyperlink);
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(1);

                resultVBoxes[i] = vbox;
                i++;
            }

            resultVBox.getChildren().addAll(resultVBoxes);
        } else {
            Alert noResults = new Alert(Alert.AlertType.ERROR);
            noResults.setTitle("NOTICE");
            noResults.setHeaderText("NO RESULTS WERE FOUND FOR YOUR SEARCH.");
            noResults.show();

            resultVBox.getChildren().clear();
        }

        resultPane.setHvalue(0.5);
    }

    public static void main(String[] args) {
        launch();
    }
}