package com.omarathon.riotapicrawler.src.lib.filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class MemorisingFilter<F> implements Filter<F> {
    private LoadingCache<F, Boolean> cache;

    public MemorisingFilter(CacheBuilder<Object, Object> cacheBuilder) {
        cache = cacheBuilder.build(new CacheLoader<F, Boolean>() {
            @Override
            public Boolean load(F o) throws Exception {
                return filter(o);
            }
        });
    }

    public MemorisingFilter() {
        this(CacheBuilder.newBuilder()
                        .expireAfterWrite(3, TimeUnit.HOURS)
                        .maximumSize(100000));
    }

    public boolean apply(F filterItem) {
        try {
            return cache.get(filterItem);
        }
        catch (ExecutionException | UncheckedExecutionException | ExecutionError e) {
            return filter(filterItem);
        }
    }

    public LoadingCache<F, Boolean> getCache() {
        return cache;
    }
}
