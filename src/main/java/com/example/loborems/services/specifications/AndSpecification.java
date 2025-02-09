package com.example.loborems.services.specifications;

public class AndSpecification<T> implements Specification<T> {
    private Specification<T> first;
    private Specification<T> second;

    public AndSpecification(Specification<T> first, Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(T item) {
        return first.isSatisfiedBy(item) && second.isSatisfiedBy(item);
    }
}