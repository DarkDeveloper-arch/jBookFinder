package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JBookFinderApplication extends Application {

    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BooksRepo booksRepo = BooksRepo.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ioUtils.readConfig();
        ioUtils.createSaveLocation();
        FxUtils.switchSceneToMain(stage, "main.fxml");
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
        booksRepo.createTable();
        booksRepo.updateBookRecords();
    }

    // Todo: refactorings

    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }
}
