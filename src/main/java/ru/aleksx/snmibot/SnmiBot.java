package ru.aleksx.snmibot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.aleksx.snmibot.props.BotProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class SnmiBot extends TelegramLongPollingBot {

    private final RestTemplate restTemplate;

    private final BotProperties botProperties;


    public SnmiBot(DefaultBotOptions options,
                   RestTemplate restTemplate,
                   BotProperties botProperties) {
        super(options);
        this.restTemplate = restTemplate;
        this.botProperties = botProperties;
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

    private static Document parseResponse(ClientHttpResponse response) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = response.getBody().read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        var page = result.toString(StandardCharsets.UTF_8);
        return Jsoup.parse(page);
    }


    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS, initialDelay = 2)
    public void getPageScheduled() {
        Document mainPage = restTemplate
                .execute("https://снми.рф",
                        HttpMethod.GET,
                        null,
                        SnmiBot::parseResponse);
        System.out.println(mainPage);
    }
}
