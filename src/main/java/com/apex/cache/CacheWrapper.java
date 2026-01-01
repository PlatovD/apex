package com.apex.cache;

public class CacheWrapper<T> {
    int cntUsage;
    T cached;

    public CacheWrapper(int cntUsage, T cached) {
        this.cntUsage = cntUsage;
        this.cached = cached;
    }
}
