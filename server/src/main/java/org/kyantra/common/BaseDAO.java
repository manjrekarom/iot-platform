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

    public abstract T get(Integer id);

    public abstract T add(T t);

    public abstract T update(Integer id, T t);

    public abstract void delete(T t);
}
