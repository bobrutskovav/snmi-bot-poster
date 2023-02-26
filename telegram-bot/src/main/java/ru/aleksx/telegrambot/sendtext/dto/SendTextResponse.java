package ru.aleksx.telegrambot.sendtext.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendTextResponse {

    boolean isSent;
}
