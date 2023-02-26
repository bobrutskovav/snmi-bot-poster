package ru.aleksx.telegrambot.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "proxy")
@Component
@Data
public class ProxyProperties {
    private boolean isEnabled;
    private String proxyType;
    private String host;
    private int port;
    private String user;
    private String password;


}
