package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.AppUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;

public class LibraryController implements FXMLController, ThemeObserver {

    @FXML
    private ScrollPane scroll;
    @FXML
    private MenuBar menuBar;
    @FXML
    private VBox contentVbox;
    @FXML
    private FlowPane booksContainer;

    private Stage stage;

    private final List<HBox> itemParents = new ArrayList<>();
    private List<BookModel> booksList;

    @FXML
    private void getHome() {
        var stage = (Stage) menuBar.getScene().getWindow();
        FxUtils.switchSceneToMain(stage, "main.fxml");
    }

    @FXML
    private void showSettings() {
        var controller = (SettingsController) FxUtils
                .newStageAndReturnController("settings.fxml", "Settings", 450, 500);
        if (controller != null) {
            controller.setNotToDeleteBooks(booksList);
            Configs.getThemeSubject().addObserver(controller);
        }
    }

    @Override
    public void initialize() {
        final double SPEED = 0.003;
        scroll.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scroll.setVvalue(scroll.getVvalue() - deltaY);
        });
    }

    public void initAfterStageSet() {
        booksContainer.getChildren().clear();
        var fetchedBooks = BooksRepo.getBooks();
        if (fetchedBooks != null) {
            booksList = new ArrayList<>(fetchedBooks);
            fetchedBooks.forEach(book -> {
                try {
                    var fxmlLoader = new FXMLLoader(getResource("fxml/bookItemLibrary.fxml"));
                    HBox root = fxmlLoader.load();
                    itemParents.add(root);
                    LibraryItemController itemController = fxmlLoader.getController();
                    itemController.setBookModel(book);
                    itemController.setLibController(this);
                    itemController.setStage(stage);
                    Configs.getThemeSubject().addObserver(itemController);
                    booksContainer.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        updateTheme(Configs.getTheme());
    }

    public void resizeListViewByStage() {
        booksContainer.setPrefHeight(stage.getHeight());
        booksContainer.setPrefWidth(stage.getWidth());
        stage.widthProperty().addListener((obs, old, newVal) -> booksContainer.setPrefWidth((Double) newVal));
        stage.heightProperty().addListener((obs, old, newVal) -> booksContainer.setPrefHeight((Double) newVal));
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        initAfterStageSet();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void updateTheme(String theme) {
        FxUtils.updateThemeForBooks(theme, booksContainer, contentVbox, itemParents);
    }

    @FXML
    private void showAbout() {
        AppUtils.showAbout();
    }
}
