package ru.aleksx.articleservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import ru.aleksx.snmibot.repository.ArticleRepository;
import ru.aleksx.snmibot.service.model.Article;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


public class ArticleService implements EntityService<Article> {



    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    @Override
    public boolean isEntityAlreadySend(Article article) {
        return articleRepository.existsArticleByArticleDateTime(article.getArticleDateTime());
    }

    @Override
    public Article save(Article entity) {
        return  articleRepository.save(entity);
    }

    @Override
    public void sendEntity(Article entity) {
        @Scheduled(fixedDelayString = "${spring.scheduled.get-page.delay:#{300}}", timeUnit = TimeUnit.SECONDS, initialDelay = 10)
        public void getPageScheduled() {
            try {
                var articles = fetchService.fetchLastN(botProperties.getArticleCount());

                articles.forEach(article -> {
                    //ToDo refactor this to service
                    var isEntityAlreadySend = entityService.isEntityAlreadySend(article);
                    if (!isEntityAlreadySend) { //Send to Channel
                        var subArticleList = article.getSubArticles();
                        var textBuilder = new StringBuilder();
                        String text;
                        for (int i = 0; i < subArticleList.size(); i++) {
                            var subArticle = subArticleList.get(i);
                            var subArticleParts = subArticle.getArticleParts();

                            if (i == 0) { //first subArticle, need title
                                text = String.format("<b>%s\n\n<a href=\"%s\">%s</a></b>\n\n",
                                        article.getDateAsText(),
                                        article.getArticleUrl(),
                                        article.getTitle()); //ToDo make this title as link on site.

                                textBuilder.append(text);
                            }

                            for (int j = 0, subArticlePartsSize = subArticleParts.size(); j < subArticlePartsSize; j++) {
                                ArticlePart articlePart = subArticleParts.get(j);
                                var currentLength = textBuilder.length();
                                if (j == 0) { //header of subarticle
                                    text = String.format("<u>%s</u>\n\n%s",
                                            subArticle.getHeader(),
                                            new String(articlePart.getText(), StandardCharsets.UTF_8));
                                } else {
                                    text = new String(articlePart.getText(), StandardCharsets.UTF_8);
                                }

                                if (currentLength + text.length() <= 4075) {
                                    textBuilder.append("\n\n");
                                    var format = articlePart.isQuote() ? "<i>%s</i>" : "%s";
                                    textBuilder.append(String.format(format, text));
                                } else { //More than 4096
                                    textBuilder.append("\n\n<b>...Далее...</b>");
                                    try {
                                        sendHtmlMessage(textBuilder);
                                    } catch (TelegramApiException e) {
                                        throw new RuntimeException(e);
                                    }
                                    textBuilder.setLength(0);
                                    textBuilder.append(text);
                                }
                            }
                        }
                        if (!textBuilder.isEmpty()) {
                            try {
                                sendHtmlMessage(textBuilder);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        entityService.save(article);

                    }
                });
    }
}
