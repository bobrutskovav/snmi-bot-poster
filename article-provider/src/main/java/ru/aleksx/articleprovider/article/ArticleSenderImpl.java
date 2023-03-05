package ru.aleksx.articleprovider.article;

import org.springframework.web.client.RestTemplate;
import ru.aleksx.articleprovider.exception.UnsuccessfulArticleSendException;
import ru.aleksx.articleprovider.props.ArticleProviderProperties;
import ru.aleksx.model.Article;

public class ArticleSenderImpl implements ArticleSender {

    private final ArticleProviderProperties articleProviderProperties;
    private final RestTemplate restTemplate;

    public ArticleSenderImpl(ArticleProviderProperties articleProviderProperties,
                             RestTemplate restTemplate) {
        this.articleProviderProperties = articleProviderProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendArticle(Article article) {
      var response = restTemplate.postForEntity(articleProviderProperties.getBotEndpoint(),article, String.class);
      if (!response.getStatusCode().is2xxSuccessful()) {
          throw new UnsuccessfulArticleSendException("Problem with sending article ");
      }
    }
}
