package ru.aleksx.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.net.InetSocketAddress;
import java.net.Proxy;

@SpringBootApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
public class TelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }



    @Bean
    public DefaultBotOptions defaultBotOptions(ProxyProperties proxyProperties,
                                               BotProperties botProperties) {
        var options = new DefaultBotOptions();
        if (proxyProperties.isEnabled()) {
            options.setProxyHost(proxyProperties.getHost());
            options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            options.setProxyPort(proxyProperties.getPort());
        }

        var builder = new ExponentialBackOff.Builder();
        var backoff = builder
                .setInitialIntervalMillis(botProperties.getExponentBackoffInitialInterval())
                .setMultiplier(botProperties.getExponentBackoffMultiplier())
                .build();
        options.setBackOff(backoff);
        return options;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, SimpleClientHttpRequestFactory simpleClientHttpRequestFactory) {
        return restTemplateBuilder.requestFactory(() -> simpleClientHttpRequestFactory).build();
    }


    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(ProxyProperties proxyProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (proxyProperties.isEnabled()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort()));
            requestFactory.setProxy(proxy);
        }
        return requestFactory;
    }





    @Bean
    public EntityService<Article> articleEntityService(ArticleRepository articleRepository) {
        return new ArticleService(articleRepository);
    }

}
