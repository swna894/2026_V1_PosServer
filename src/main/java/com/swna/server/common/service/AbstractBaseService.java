package com.swna.server.common.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.common.exception.ExceptionUtils;

import lombok.NonNull;

public abstract class AbstractBaseService<T, I> implements BaseService<T, I> {

    protected abstract JpaRepository<T, I> getRepository();

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public T findById(@NonNull I id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found: " + id));
    }

    @Override
    public T save(@NonNull T entity) {
        return getRepository().save(entity);
    }

    @Override
    public T update(@NonNull I id, @NonNull T entity) {
        if (!getRepository().existsById(id)) {
            // 제네릭 타입 T의 클래스명을 리소스 타입으로, id를 식별자로 전달
            throw ExceptionUtils.resourceNotFound(
                entity.getClass().getSimpleName(), 
                String.valueOf(id)
            );
        }
        return getRepository().save(entity);
    }

    @Override
    public void delete(@NonNull I id) {
        getRepository().deleteById(id);
    }
}
