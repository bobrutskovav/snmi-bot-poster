package ru.aleksx.snmibot.service;

import ru.aleksx.snmibot.repository.ArticleRepository;
import ru.aleksx.snmibot.service.model.Article;

import javax.annotation.PostConstruct;

public class ArticleService implements EntityService<Article> {

    private Article lastArticle;

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @PostConstruct
    private void initLastEntity() {
        lastArticle = articleRepository.findFirstByOrderByArticleDateTimeDesc();
    }

    public boolean isArticleUpdated(Article newArticle) {
        return articleRepository.existsArticleByArticleDateTime(newArticle.getArticleDateTime());
    }

    @Override
    public boolean isEntityAlreadySend(Article article) {
        return isArticleUpdated(article);
    }

    @Override
    public Article save(Article entity) {
        lastArticle = articleRepository.save(entity);
        return lastArticle;
    }
}
