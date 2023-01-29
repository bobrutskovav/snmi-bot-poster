package ru.aleksx.snmibot.service;

import ru.aleksx.snmibot.repository.ArticleRepository;
import ru.aleksx.snmibot.service.model.Article;


public class ArticleService implements EntityService<Article> {



    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
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
        return  articleRepository.save(entity);
    }
}
