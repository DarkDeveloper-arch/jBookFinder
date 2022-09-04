package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil.getResource;

public class BookItemController implements FXMLController {

    @FXML
    private Button downloadBtn;
    @FXML
    private VBox operationVbox;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookAuthor;
    @FXML
    private Label bookPublisher;
    @FXML
    private Label bookFormat;
    @FXML
    private Label bookSize;
    @FXML
    private Label bookPages;
    @FXML
    private Label bookYear;
    @FXML
    private Label bookLanguage;
    @FXML
    private ImageView bookImage;

    private BookModel bookModel;
    private static final BookUtils bookUtils = new BookUtils();


    @Override
    public void initialize() {

    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
        bookUtils.displayData(bookTitle, bookAuthor, bookPublisher, bookFormat,
                bookSize, bookPages, bookYear, bookLanguage, bookModel);
        hideExtraInfo();
        var bookUtils = new BookUtils();
        bookUtils.fetchAndSetImageAsync(bookModel.getImageUrl(), bookModel.getTitle(), bookImage);
    }

    private void hideExtraInfo() {
        bookPublisher.setVisible(false);
        bookPublisher.managedProperty().bind(bookPublisher.visibleProperty());
        bookYear.setVisible(false);
        bookYear.managedProperty().bind(bookYear.visibleProperty());
        bookLanguage.setVisible(false);
        bookLanguage.managedProperty().bind(bookLanguage.visibleProperty());
    }


    @FXML
    private void downloadBook() {
        if (bookModel == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")) {
            var bookUtils = new BookUtils();
            bookUtils.downloadBookAndAddProgress(bookModel, operationVbox);
            return;
        }

        System.out.println("Show Book");
    }


    @FXML
    private void moreDetails() throws IOException {
        var stage = new Stage();
        HBox root = FXMLLoader.load(getResource("fxml/ListBookItem.fxml"));
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setMinWidth(root.getMinWidth());
        stage.setMinHeight(root.getMinHeight());
        bookUtils.setDataForDetails(root, bookModel);
        stage.show();
        var vBox = (VBox) root.getChildren().get(1);
        vBox.setPrefWidth(800);
        stage.heightProperty().addListener((o, ol, newVal) -> vBox.setPrefHeight((Double) newVal));
        stage.widthProperty().addListener((o, ol, newVal) -> vBox.setPrefWidth((Double) newVal));
    }


}
