package ru.aleksx.snmibot.service;

public interface EntityService<T> {


    boolean isEntityUpdated(T entity);
    T save(T entity);
}
