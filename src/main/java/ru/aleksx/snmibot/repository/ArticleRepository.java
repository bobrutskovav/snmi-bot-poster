package ru.aleksx.snmibot.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.aleksx.snmibot.service.model.Article;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface ArticleRepository extends MongoRepository<Article, ObjectId> {


    Article findFirstByArticleDateTime(ZonedDateTime articleDateTime);
    Article findFirstByOrderByArticleDateTimeDesc();

    boolean existsArticleByArticleDateTime(LocalDateTime zonedDateTime);
}
