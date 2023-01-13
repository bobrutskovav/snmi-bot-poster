package ru.aleksx.snmibot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aleksx.snmibot.props.BotProperties;
import ru.aleksx.snmibot.service.EntityService;
import ru.aleksx.snmibot.service.FetchService;
import ru.aleksx.snmibot.service.model.Article;
import ru.aleksx.snmibot.service.model.ArticlePart;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SnmiBot extends TelegramLongPollingBot {


    private final BotProperties botProperties;

    private final FetchService<Article> fetchService;

    private final EntityService<Article> entityService;


    public SnmiBot(DefaultBotOptions options,
                   BotProperties botProperties,
                   FetchService<Article> fetchService,
                   EntityService<Article> entityService) {
        super(options);
        this.botProperties = botProperties;
        this.fetchService = fetchService;
        this.entityService = entityService;
    }


    @Override
    public String getBotUsername() {
        return botProperties.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);

    }


    @Scheduled(fixedDelayString = "${spring.scheduled.get-page.delay:#{300}}", timeUnit = TimeUnit.SECONDS, initialDelay = 10)
    public void getPageScheduled() {
        try {
            var articles = fetchService.fetchLastN(botProperties.getArticleCount());

            articles.forEach(article -> {
                //ToDo refactor this to service
                var isEntityAlreadySend = entityService.isEntityAlreadySend(article);
                if (!isEntityAlreadySend) { //Send to Channel
                    var subArticleList = article.getSubArticles();
                    var textBuilder = new StringBuilder();
                    String text;
                    for (int i = 0; i < subArticleList.size(); i++) {
                        var subArticle = subArticleList.get(i);
                        var subArticleParts = subArticle.getArticleParts();

                        if (i == 0) { //first subArticle, need title
                            text = String.format("<b>%s\n\n<a href=\"%s\">%s</a></b>\n\n",
                                    article.getDateAsText(),
                                    article.getArticleUrl(),
                                    article.getTitle()); //ToDo make this title as link on site.

                            textBuilder.append(text);
                        }

                        for (int j = 0, subArticlePartsSize = subArticleParts.size(); j < subArticlePartsSize; j++) {
                            ArticlePart articlePart = subArticleParts.get(j);
                            var currentLength = textBuilder.length();
                            if (j == 0) { //header of subarticle
                                text = String.format("<u>%s</u>\n\n%s",
                                        subArticle.getHeader(),
                                        new String(articlePart.getText(), StandardCharsets.UTF_8));
                            } else {
                                text = new String(articlePart.getText(), StandardCharsets.UTF_8);
                            }

                            if (currentLength + text.length() <= 4075) {
                                textBuilder.append("\n\n");
                                var format = articlePart.isQuote() ? "<i>%s</i>" : "%s";
                                textBuilder.append(String.format(format, text));
                            } else { //More than 4096
                                textBuilder.append("\n\n<b>...Далее...</b>");
                                try {
                                    sendHtmlMessage(textBuilder);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                                textBuilder.setLength(0);
                                textBuilder.append(text);
                            }
                        }
                    }
                    if (!textBuilder.isEmpty()) {
                        try {
                            sendHtmlMessage(textBuilder);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    entityService.save(article);

                }
            });

        } catch (Exception e) {
            log.error("Some Exception in process, send alarm to admin", e);
            var msg = new SendMessage(botProperties.getAdminUuid(), String.format("Exception in snmi-bot \n %s \n %s. \n Bot is down", e.getMessage(), e.getCause()));
            try {
                execute(msg);
            } catch (Exception exception) {
                log.error("Error on sending msg to admin", e);
            }
        }

    }

    private void sendHtmlMessage(StringBuilder textBuilder) throws TelegramApiException {
        var msg = new SendMessage(botProperties.getChannelUuid(), textBuilder.toString());
        msg.enableHtml(true);
        execute(msg);
    }
}
