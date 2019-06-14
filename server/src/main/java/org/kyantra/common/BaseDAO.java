package org.kyantra.common;

import org.kyantra.services.HibernateService;

import java.util.List;
import java.util.Optional;

/* A DAO should contain CRUD operations at the least. */
public abstract class BaseDAO<T> {
    public HibernateService mService;

    public BaseDAO() {
        mService = HibernateService.getInstance();
    }


    public HibernateService getService() {
        return mService;
    }

    abstract public T get(Integer id);

    protected abstract T add(T t);

    protected abstract T update(Integer id, T t);

    abstract void delete(T t);
}
