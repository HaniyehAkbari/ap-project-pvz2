package com.pvz2.models.pool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class GenericObjectPool<T extends Resettable> {
    private final List<T> freeObjects = new ArrayList<>();
    private final Set<T> inPoolSet = new HashSet<>();
    private final Map<T, Integer> generations = new IdentityHashMap<>();
    private final Supplier<T> factory;

    public GenericObjectPool(Supplier<T> factory) {
        this.factory = factory;
    }

    public T acquire() {
        T obj;
        if (freeObjects.isEmpty()) {
            obj = factory.get();
        } else {
            obj = freeObjects.removeLast();
            inPoolSet.remove(obj);
        }
        generations.merge(obj, 1, Integer::sum);
        return obj;
    }

    public void release(T obj) {
        if (obj == null) {
            return;
        }

        if (inPoolSet.contains(obj)) {
            return;
        }

        freeObjects.add(obj);
        inPoolSet.add(obj);
    }

    public int getGeneration(T obj) {
        return generations.getOrDefault(obj, 0);
    }

    public void clear() {
        freeObjects.clear();
        inPoolSet.clear();
    }
}
