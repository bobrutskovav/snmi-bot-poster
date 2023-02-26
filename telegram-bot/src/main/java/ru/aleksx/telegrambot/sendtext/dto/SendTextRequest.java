package ru.aleksx.telegrambot.sendtext.dto;

import lombok.Data;

import java.util.List;

@Data
public class SendTextRequest {

    private List<String> messages;

}
