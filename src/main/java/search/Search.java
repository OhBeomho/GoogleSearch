package search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Search {
    public static Map<String, String> search(String searchText, int numberOfResults) throws IOException {
        Map<String, String> results = new HashMap<>();

        Document document = Jsoup.connect("https://www.google.com/search?q=" + searchText + "&num=" + numberOfResults).get();

        Elements g = document.getElementsByClass("g");

        for (Element element : g) {
            Elements a = element.getElementsByTag("a"),
                    t = element.getElementsByClass("LC20lb MBeuO DKV0Md");

            String link = a.attr("href"),
                    title = t.text();
            results.put(title, link);
        }

        return results;
    }
}
