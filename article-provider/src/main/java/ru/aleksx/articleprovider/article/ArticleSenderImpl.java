package ru.aleksx.articleprovider.article;

import org.springframework.web.client.RestTemplate;
import ru.aleksx.articleprovider.props.ArticleProviderProperties;
import ru.aleksx.model.Article;

public class ArticleSenderImpl implements ArticleSender {

    private ArticleProviderProperties articleProviderProperties;
    private RestTemplate restTemplate;

    public ArticleSenderImpl(ArticleProviderProperties articleProviderProperties,
                             RestTemplate restTemplate) {
        this.articleProviderProperties = articleProviderProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendArticle(Article article) {
        restTemplate.exchange()
    }
}
