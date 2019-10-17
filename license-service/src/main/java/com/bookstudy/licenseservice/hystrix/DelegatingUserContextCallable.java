package com.bookstudy.licenseservice.hystrix;

import com.bookstudy.licenseservice.utils.UserContext;
import com.bookstudy.licenseservice.utils.UserContextHolder;

import java.util.concurrent.Callable;

public class DelegatingUserContextCallable<V> implements Callable<V> {

    private final Callable<V> delegate ;
    private UserContext originUserContext;

    public DelegatingUserContextCallable(Callable<V> delegate, UserContext originUserContext) {
        this.delegate = delegate;
        this.originUserContext = originUserContext;
    }

    @Override
    public V call() throws Exception {
        //当前UserContextHolder中的ThreadLocal特定于当前线程
        UserContextHolder.setContext(originUserContext);
        try {
            return delegate.call();
        } finally {
            this.originUserContext = null;
        }
    }

    public static <V> DelegatingUserContextCallable<V> create(Callable<V> delegate,UserContext userContext){
        return new DelegatingUserContextCallable<V>(delegate,userContext);
    }
}
