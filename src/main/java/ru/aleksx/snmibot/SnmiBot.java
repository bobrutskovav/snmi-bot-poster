package ru.aleksx.snmibot;

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


    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS, initialDelay = 5)
    public void getPageScheduled() throws TelegramApiException {
        var article = fetchService.fetch(); //ToDo refactor this to service
        var isEntityUpdated = entityService.isEntityUpdated(article);
        if (isEntityUpdated) { //Send to Channel
            var subArticleList = article.getSubArticles();
            var textBuilder = new StringBuilder();
            String text;
            for (int i = 0; i < subArticleList.size(); i++) {
                var subArticle = subArticleList.get(i);
                var subArticleParts = subArticle.getArticleParts();

                if (i == 0) { //first subArticle, need title
                    text = String.format("<b>%s\n\n%s</b>\n\n",
                            article.getDateAsText(),
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
                        sendHtmlMessage(textBuilder);
                        textBuilder.setLength(0);
                        textBuilder.append(text);
                    }
                }
            }
            if (!textBuilder.isEmpty()) {
                sendHtmlMessage(textBuilder);
            }
            entityService.save(article);
        }

    }

    private void sendHtmlMessage(StringBuilder textBuilder) throws TelegramApiException {
        var msg = new SendMessage(botProperties.getChannelUuid(), textBuilder.toString());
        msg.enableHtml(true);
        execute(msg);
    }
}
