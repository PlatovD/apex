package com.apex.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Абстрактный класс для кеширования сущностей. По умолчанию не выбрасывает ошибки, тк пользователю
 * не нужно сообщать о внутренностях работы и об ошибках внутри служебных систем
 * @param <T> - тип кешируемого объекта
 */
public abstract class AbstractCache<T> implements Cache<T> {
    private final Map<String, CacheWrapper<T>> cacheMap = new HashMap<>();

    @Override
    public T smartCache(String name, T cacheObject) {
        if (existsInCache(name)) return getFromCache(name);
        cache(name, cacheObject);
        return cacheObject;
    }

    @Override
    public void cache(String name, T cacheObject) {
        if (existsInCache(name)) {
            CacheWrapper<T> wrapper = cacheMap.get(name);
            wrapper.cntUsage++;
            return;
        }
        CacheWrapper<T> wrapper = new CacheWrapper<>(1, cacheObject);
        cacheMap.put(name, wrapper);
    }

    @Override
    public boolean existsInCache(String name) {
        return cacheMap.containsKey(name);
    }

    @Override
    public T getFromCache(String name) {
        if (!existsInCache(name)) return null;
        CacheWrapper<T> wrapper = cacheMap.get(name);
        wrapper.cntUsage++;
        return wrapper.cached;
    }

    @Override
    public void deleteFromCacheIfNotUsedElseDecreaseUsage(String name) {
        if (!existsInCache(name)) return;
        CacheWrapper<T> wrapper = cacheMap.get(name);
        if (wrapper.cntUsage <= 1) {
            deleteFromCache(name);
            return;
        }
        wrapper.cntUsage--;
    }

    @Override
    public void deleteFromCache(String name) {
        if (!existsInCache(name)) return;
        cacheMap.remove(name);
    }
}
