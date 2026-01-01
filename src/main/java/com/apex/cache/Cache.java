package com.apex.cache;

public interface Cache<T> {
    /**
     * Если объект не кеширован - кеширует и отдает его же. А если объект есть в кеше, то подменяет и увеличивает счетчик использования
     * @param cacheObject - объект для кеширования
     * @return объект из кеша/тот же объект
     */
    T smartCache(String name, T cacheObject);

    void cache(String name, T cacheObject);

    boolean existsInCache(String name);

    T getFromCache(String name);

    void deleteFromCacheIfNotUsedElseDecreaseUsage(String name);

    void deleteFromCache(String name);
}
