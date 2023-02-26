package ru.aleksx.articleservice.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.aleksx.snmibot.service.model.Article;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface ArticleRepository extends MongoRepository<Article, ObjectId> {

    boolean existsArticleByArticleDateTime(LocalDateTime zonedDateTime);
}
