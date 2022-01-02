package com.jackbracey.recipe.Scrapers;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
public abstract class Scraper {

    public final String baseUrl;
    public final List<String> scrapedUrls;

    public Scraper(String baseUrl, List<String> scrapedUrls) {
        this.baseUrl = baseUrl;
        this.scrapedUrls = scrapedUrls;
    }

    public abstract void run();

    public Document GetPage() {
        return GetPage(null, null);
    }

    public Document GetPage(String urlExtension, Integer pageNumber) {
        String url;
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        if (Strings.isNullOrEmpty(urlExtension) && pageNumber == null)
            url = baseUrl;
        else
            url = String.format("%s%s?page=%d", baseUrl, urlExtension, pageNumber);
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return Jsoup.parse(sb.toString());
        } catch (IOException e) {
            log.error("GetPage error", e);
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("GetPage error", e);
                }
        }
    }

    public Document GetPageByUrl(String url) {
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return Jsoup.parse(sb.toString());
        } catch (IOException e) {
            log.error("GetPage error", e);
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("GetPage error", e);
                }
        }
    }

    public String GetHref(Element element) {
        return element.attributes().get("href");
    }

}
