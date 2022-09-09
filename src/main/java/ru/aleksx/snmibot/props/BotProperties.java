package ru.aleksx.snmibot.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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
}
