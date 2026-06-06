package oshxona.oshxona.service.base;

/**
 * @param <R> repository
 * @param <M> mapper
 * @param <V> validator
 */
public abstract class AbstractService<R, M, V> {
    protected final R repository;
    protected final M mapper;
    protected final V validator;

    public AbstractService(R repository, M mapper, V validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }
}