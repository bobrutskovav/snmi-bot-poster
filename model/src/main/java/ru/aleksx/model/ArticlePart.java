package ru.aleksx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticlePart {

    private byte[] text;
    private boolean isQuote;
}
