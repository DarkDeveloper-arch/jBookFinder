package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.controlsfx.control.Notifications;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScraperTask extends Task<Flux<BookModel>> {

    private final String bookName;
    private final int pageNumber;


    public ScraperTask(String bookName, int pageNumber) {
        this.bookName = bookName;
        this.pageNumber = pageNumber;
    }


    @Override
    protected Flux<BookModel> call() throws Exception {
        // retry
        Document doc = null;
        for (int i = 0; i < 3; i++) {
            try {
                doc = Jsoup.connect("https://libgen.rs/search.php")
                        .data("req", bookName)
                        .data("page", String.valueOf(pageNumber))
                        .data("res", Configs.getResultCount())
                        .userAgent("Mozilla")
                        .get();
                break;
            } catch (IOException e) {
                // delay request
                e.printStackTrace();
                Thread.sleep(3000);
            }
        }
        if (doc == null)
            throw new RuntimeException("Not Found");

        var listOfData = doc.select("table.c").get(0)
                .select("tr > td")
                .stream().skip(11).toList();
        var books = new ArrayList<BookModel>();

        for (int i = 0; i < listOfData.size(); i += 12) {
            if (i < listOfData.size() + 10) {
                var fileFormat = listOfData.get(i + 8).text();
                switch (Configs.getFilterResult()) {
                    case "pdf,rar,epub" -> {
                        switch (fileFormat) {
                            case "pdf", "rar", "epub" -> addBooks(listOfData, i, books);
                        }
                    }
                    case "All files" -> addBooks(listOfData, i, books);
                }
            }
        }
        return cleanAndFetchOtherData(books);
    }

    private void addBooks(List<Element> listOfData, int i, List<BookModel> books) {
        var titleLinks = listOfData.get(i + 2).getElementsByTag("a");
        String title = titleLinks.get(0).text();
        if (titleLinks.size() == 2)
            title = titleLinks.get(1).text();

        var book = new BookModel(listOfData.get(i).text(), listOfData.get(i + 1).text(),
                title, listOfData.get(i + 3).text(), listOfData.get(i + 4).text(),
                listOfData.get(i + 5).text(), listOfData.get(i + 6).text(), listOfData.get(i + 7).text(),
                listOfData.get(i + 8).text(),
                listOfData.get(i + 9).select("a").attr("href"));
        books.add(book);
    }


    /**
     * Fetches download link and image link
     */
    private Flux<BookModel> cleanAndFetchOtherData(List<BookModel> books) {
        var baseURL = Configs.getImageBaseUrl();
        if (books.isEmpty()) {
            Platform.runLater(() -> Notifications.create()
                    .title("Search Failed")
                    .text("No books found")
                    .showWarning());
            failed();
            return Flux.empty();
        }
        return Flux.create(fluxSink -> books.forEach(book -> {
            try {
                var downloadPage = Jsoup.connect(book.getMirror()).userAgent("Mozilla").get();
                var downloadDiv = downloadPage.getElementById("download");
                String link = null;
                if (downloadDiv != null) link = downloadDiv.select("h2 > a").attr("href");
                book.setMirror(link);
                var imageUrl = downloadPage.select("img").attr("src");
                if (!imageUrl.startsWith("http"))
                    book.setImageUrl(baseURL + imageUrl);
                else
                    book.setImageUrl(imageUrl);
                fluxSink.next(book);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
