package ru.aleksx.telegrambot.sendtext;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aleksx.telegrambot.SnmiBot;
import ru.aleksx.telegrambot.sendtext.dto.SendTextRequest;
import ru.aleksx.telegrambot.sendtext.dto.SendTextResponse;

@RestController
@Slf4j
public class SendTextController {

    private final SnmiBot snmiBot;

    public SendTextController(SnmiBot snmiBot) {
        this.snmiBot = snmiBot;
    }

    @PostMapping
    public SendTextResponse sendText(SendTextRequest sendTextRequest) {
        log.debug("Send Messages {}", sendTextRequest);//ToDo spring trace id here
        return SendTextResponse.builder()
                .isSent(snmiBot.sendHtmlMessages(sendTextRequest.getMessages()))
                .build();


    }
}
