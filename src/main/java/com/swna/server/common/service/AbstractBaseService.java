package com.swna.server.common.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractBaseService<T, ID> implements BaseService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found: " + id));
    }

    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Not Found: " + id);
        }
        return getRepository().save(entity);
    }

    @Override
    public void delete(ID id) {
        getRepository().deleteById(id);
    }
}
