package ru.aleksx.snmibot.service.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Document
public class Article {

    @Id
    private ObjectId objectId;
    private String title;
    @Transient
    private String dateAsText;
    private LocalDateTime articleDateTime;
    private List<SubArticle> subArticles;




    public Article(LocalDateTime articleDateTime,
                   String title,
                   String dateAsText,
                   List<SubArticle> subArticles) {
        this.title = title;
        this.dateAsText = dateAsText;
        this.objectId = ObjectId.getSmallestWithDate(new Date());
        this.articleDateTime = articleDateTime;
        this.subArticles = subArticles;

    }

    @PersistenceCreator
    public Article(ObjectId objectId, String title, LocalDateTime articleDateTime, List<SubArticle> subArticles) {
        this.objectId = objectId;
        this.title = title;
        this.articleDateTime = articleDateTime;
        this.subArticles = subArticles;
    }
}
