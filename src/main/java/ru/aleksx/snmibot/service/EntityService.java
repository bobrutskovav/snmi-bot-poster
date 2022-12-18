package ru.aleksx.snmibot.service;

public interface EntityService<T> {


    boolean isEntityAlreadySend(T entity);
    T save(T entity);
}
