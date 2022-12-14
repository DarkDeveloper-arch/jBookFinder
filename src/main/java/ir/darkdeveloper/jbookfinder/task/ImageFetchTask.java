package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;

public class ImageFetchTask extends Task<File> {

    private final String imageUrl;
    private final ImageView bookImage;
    private final String fileName;
    private final VBox imageBox;
    private final ProgressIndicator imageProgress;

    private File file;

    public ImageFetchTask(String imageUrl, String fileName, ImageView bookImage, VBox imageBox, ProgressIndicator imageProgress) {
        this.imageUrl = imageUrl;
        this.bookImage = bookImage;
        this.fileName = fileName;
        this.imageBox = imageBox;
        this.imageProgress = imageProgress;
    }

    @Override
    protected File call() throws Exception {
        if (imageUrl == null)
            return null;
        var imageFile = new File(Configs.getBookCoverLocation() + fileName);
        if (imageFile.exists()) {
            file = imageFile;
            return imageFile;
        }
        FileUtils.copyURLToFile(
                new URL(imageUrl),
                imageFile
        );
        file = imageFile;
        return imageFile;
    }

    @Override
    protected void succeeded() {
        try {
            var finalFile = file;
            if (file == null)
                finalFile = new File(getResource("images/blank.png").toExternalForm());
            var inputStream = new FileInputStream(finalFile);
            var image = new Image(inputStream);
            imageBox.getChildren().remove(imageProgress);
            bookImage.setFitHeight(imageBox.getPrefHeight());
            bookImage.setFitWidth(imageBox.getPrefWidth());
            bookImage.setImage(image);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
