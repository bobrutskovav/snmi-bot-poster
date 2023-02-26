package ru.aleksx.articleprovider.props;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ArticleProviderProperties {

    @Max(value = Integer.MAX_VALUE)
    private int articleCount;

    @NotEmpty
    @URL
    private String targetUrl;

    @NotEmpty
    @URL
    private String targetUrlArchive;
}
