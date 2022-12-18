package ru.aleksx.snmibot.props;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "bot")
@Component
@Data
@Validated
public class BotProperties {

    @NotEmpty
    @NotBlank
    private String botToken;
    @NotEmpty
    @NotBlank
    private String botUserName;
    @NotEmpty
    @NotBlank
    private String channelUuid;

    @NotEmpty
    @NotBlank
    private String adminUuid;

    @NotEmpty
    @URL
    private String targetUrl;

    @NotEmpty
    @URL
    private String targetUrlArchive;

    @Max(value = Integer.MAX_VALUE)
    private int articleCount;
}
