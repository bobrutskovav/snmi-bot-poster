package ru.aleksx.articleservice.service;

public interface EntityService<T> {


    boolean isEntityAlreadySend(T entity);
    T save(T entity);
    void sendEntity(T entity);
}
