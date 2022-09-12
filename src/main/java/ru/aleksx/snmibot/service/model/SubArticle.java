package ru.aleksx.snmibot.service.model;

import lombok.Data;

import java.util.List;

@Data
public class SubArticle {

    public SubArticle(String header) {
        this.header = header;
    }

    private String header;
    private List<ArticlePart> articleParts;
}
