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
        if (entityService.isEntityUpdated(article)) { //Send to Channel
            var subArticleList = article.getSubArticles();
            for (int i = 0; i < subArticleList.size(); i++) {
                var subArticle = subArticleList.get(i);
                var subArticleParts = subArticle.getArticleParts();
                if (i == 0) {
                    var text = String.format("<b>%s\n\n%s</b>\n\n<u>%s</u>\n\n%s",
                            article.getDateAsText(),
                            article.getTitle(), //ToDo make this title as link on site.
                            subArticle.getHeader(),
                            new String(subArticle.getArticleParts().get(0).getText()));
                    var firstMsg = new SendMessage(botProperties.getChannelUuid(), text);
                    firstMsg.enableHtml(true);
                    execute(firstMsg);
                    for (int y = 1; y < subArticleParts.size(); y++) {
                        sendArticlePart(subArticleParts.get(y));
                    }

                } else {
                    for (int x = 0; x < subArticleParts.size(); x++) {
                        if (x == 0) { // first with header
                            var text = String.format("<u>%s</u>\n\n%s",
                                    subArticle.getHeader(),
                                    new String(subArticleParts.get(0).getText(), StandardCharsets.UTF_8));
                            var firstMsg = new SendMessage(botProperties.getChannelUuid(), text);
                            firstMsg.enableHtml(true);
                            execute(firstMsg);

                        } else {
                            sendArticlePart(subArticleParts.get(x));
                        }
                    }

                }
            }
            entityService.save(article);
        }
    }

    private void sendArticlePart(ArticlePart subArticlePart) throws TelegramApiException {
        var format = subArticlePart.isQuote() ? "<i>%s</i>" : "%s";
        var sendMessage = new SendMessage(
                botProperties.getChannelUuid(),
                String.format(format, new String(subArticlePart.getText(), StandardCharsets.UTF_8)));
        sendMessage.enableHtml(true);
        execute(sendMessage);
    }


    private void sendToChannel(Article newArticle) {

    }
}
