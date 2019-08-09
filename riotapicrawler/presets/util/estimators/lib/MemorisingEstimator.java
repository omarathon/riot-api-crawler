package com.omarathon.riotapicrawler.presets.util.estimators.lib;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// obtains an estimate of type E from an input object of type T
public abstract class MemorisingEstimator<T, E> {
    private LoadingCache<T, Optional<E>> cache;

    public MemorisingEstimator(CacheBuilder<Object, Object> builder) {
        cache = builder.build(new CacheLoader<T, Optional<E>>() {
            @Override
            public Optional<E> load(T t) throws Exception {
                E estimate = estimate(t);
                return Optional.ofNullable(estimate(t));
            }
        });
    }

    public MemorisingEstimator() {
        this(CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.HOURS)
                .maximumSize(10000));
    }

    public E getEstimate(T objectToEstimate) {
        try {
            Optional<E> estimate = cache.get(objectToEstimate);
            if (estimate.isPresent()) return estimate.get();
            else return null;
        }
        catch (ExecutionException | UncheckedExecutionException e) {
            return estimate(objectToEstimate);
        }
    }

    // obtains an estimate of type E for the input object of type T. null return means no estimate (fail)
    protected abstract E estimate(T objectToEstimate);
}
