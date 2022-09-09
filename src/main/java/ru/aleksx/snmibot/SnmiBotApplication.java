package ru.aleksx.snmibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ru.aleksx.snmibot.props.ProxyProperties;

import java.net.InetSocketAddress;
import java.net.Proxy;

@SpringBootApplication
@EnableScheduling
public class SnmiBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnmiBotApplication.class, args);
    }


    @Bean
    public DefaultBotOptions defaultBotOptions(ProxyProperties proxyProperties) {
        var options = new DefaultBotOptions();
        if (proxyProperties.isEnabled()) {
            options.setProxyHost(proxyProperties.getHost());
            options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            options.setProxyPort(proxyProperties.getPort());
        }

        return options;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,SimpleClientHttpRequestFactory simpleClientHttpRequestFactory) {
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

}
