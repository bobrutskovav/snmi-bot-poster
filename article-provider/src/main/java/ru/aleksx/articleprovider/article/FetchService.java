package ru.aleksx.articleprovider.article;

import java.util.List;

public interface FetchService <T> {

    T fetch();

    List<T> fetchLastN(int count);
}
