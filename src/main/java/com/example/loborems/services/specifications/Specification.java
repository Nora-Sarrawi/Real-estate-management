package com.example.loborems.services.specifications;

public interface Specification<T> {
    boolean isSatisfiedBy(T item);
}