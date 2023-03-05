package ru.aleksx.articleprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.aleksx.articleprovider.article.ArticleSender;
import ru.aleksx.articleprovider.article.ArticleSenderImpl;
import ru.aleksx.articleprovider.article.FetchJsoupDocumentService;
import ru.aleksx.articleprovider.article.FetchService;
import ru.aleksx.articleprovider.props.ArticleProviderProperties;
import ru.aleksx.model.Article;

@SpringBootApplication
public class ArticleProviderApplication {


    public static void main(String[] args) {
        SpringApplication.run(ArticleProviderApplication.class, args);
    }


    @Bean
    public FetchService<Article> articleFetchService(ArticleProviderProperties articleProviderProperties) {
        return new FetchJsoupDocumentService(articleProviderProperties);
    }

    @Bean
    public ArticleSender articleSender(ArticleProviderProperties articleProviderProperties,
                                       RestTemplate restTemplate) {
        return new ArticleSenderImpl(articleProviderProperties, restTemplate);
    }


    @Bean
    public RestTemplate restTemplate(ArticleProviderProperties articleProviderProperties) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.rootUri(articleProviderProperties.getGateWayUrl())
                .build();
    }

}




