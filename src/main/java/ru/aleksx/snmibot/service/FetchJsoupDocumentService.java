package ru.aleksx.snmibot.service;


import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import ru.aleksx.snmibot.SnmiBot;
import ru.aleksx.snmibot.exception.ParseException;
import ru.aleksx.snmibot.props.BotProperties;
import ru.aleksx.snmibot.service.model.Article;
import ru.aleksx.snmibot.service.model.ArticlePart;
import ru.aleksx.snmibot.service.model.SubArticle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FetchJsoupDocumentService implements FetchService<Article> {

    private final WebClient webClient = new WebClient();
    private final BotProperties botProperties;


    private static final String XPATH_CURRENT_DATE = "//div[@class='paper__info paper__info--top']/div[@class='paper__date date']";
    private static final String XPATH_ARTICLE_BODY = "//div[@class='paper__content']";
    private static final String XPATH_TITLE = "//div[@class='paper__title']";

    //9 сентября 2022 года, 12:07 МСК
    private static final DateTimeFormatter currentZonedDateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy года, HH:mm МСК", new Locale("ru"));



    public FetchJsoupDocumentService(BotProperties botProperties) {
        this.botProperties = botProperties;
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
    }

    @Override
    public Article fetch() {
        Page page;
        try {
         page =  webClient.getPage(botProperties.getTargetUrl());


        Document mainPage = Jsoup.parse(((HtmlPage) page).asXml());
        var currentDateElement = mainPage.selectXpath(XPATH_CURRENT_DATE);
        var papperElement = mainPage.selectXpath(XPATH_ARTICLE_BODY);
        var dateTimeText = currentDateElement.text(); //ToDo first check DB, after parse!
        var dateTime = LocalDateTime.parse(dateTimeText, currentZonedDateTimeFormatter);
        List<SubArticle> subArticles = new ArrayList<>();
        List<ArticlePart> articleParts = new ArrayList<>();
        String title = mainPage.selectXpath(XPATH_TITLE).text();
        if (StringUtil.isBlank(title)) {
            throw new ParseException("Can't parse title of Article");
        }
        SubArticle subArticle = null;
        for (Element childnode : papperElement.get(0).children()) {

            switch (childnode.nodeName()) {
                case "h2", "h1"  -> {
                    if (subArticle != null) {
                        subArticle.setArticleParts(articleParts);
                        subArticles.add(subArticle);

                    }
                    articleParts = new ArrayList<>();
                    subArticle = new SubArticle(childnode.text());
                }
                case ("ul") -> {
                    var stringBuilder = new StringBuilder();
                    for (Element child : childnode.children()) {
                        stringBuilder.append(child.text()).append("\n");
                    }
                    articleParts.add(
                            new ArticlePart(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), false)
                    );

                }
                default -> articleParts.add(new ArticlePart(
                        childnode.text().getBytes(StandardCharsets.UTF_8),
                        childnode.nodeName().equals("blockquote")));
            }

        }
        if (subArticle == null) {
            throw new ParseException("Subarticle is not parced!");
        }
        subArticle.setArticleParts(articleParts);
        subArticles.add(subArticle);
        return new Article(dateTime, title, dateTimeText, subArticles);
        } catch (IOException e) {
            log.error("Error in WebClient!", e);
            throw new RuntimeException(e);

        }
    }


    private Document parseResponse(ClientHttpResponse response) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = response.getBody().read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        var page = result.toString(StandardCharsets.UTF_8);
        return Jsoup.parse(page);
    }


}
