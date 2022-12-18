package ru.aleksx.snmibot.service;

import java.util.List;

public interface FetchService <T> {

    T fetch();

    List<T> fetchLastN(int count);
}
