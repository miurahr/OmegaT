package org.omegat.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SegmentProperties<T> implements Map<T, List<T>> {

    private final T[] props;
    
    SegmentProperties(T[] props) {
        assert props != null;
        assert props.length % 2 == 0;
        this.props = props;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        for (int i = 0; i < props.length; i += 2) {
            if (props[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        for (int i = 1; i < props.length; i += 2) {
            if (props[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<java.util.Map.Entry<T, List<T>>> entrySet() {
        if (props.length == 0) {
            return Collections.EMPTY_SET;
        }
        Set<java.util.Map.Entry<T, List<T>>> set = new LinkedHashSet<java.util.Map.Entry<T, List<T>>>();
        Set<T> done = new HashSet<T>();
        for (int i = 0; i < props.length; i += 2) {
            T key = props[i];
            if (done.contains(key)) {
                continue;
            }
            List<T> list = new ArrayList<T>();
            list.add(props[i + 1]);
            for (int j = i + 2; j < props.length; j += 2) {
                if (props[j].equals(key)) {
                    list.add(props[j + 1]);
                }
            }
            set.add(new Entry(key, list));
            done.add(key);
        }
        return set;
    }

    @Override
    public List<T> get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < props.length; i += 2) {
            if (props[i].equals(key)) {
                list.add(props[i + 1]);
            }
        }
        return list.isEmpty() ? null : list;
    }

    @Override
    public boolean isEmpty() {
        return props.length == 0;
    }

    @Override
    public Set<T> keySet() {
        Set<T> set = new HashSet<T>();
        for (int i = 0; i < props.length; i += 2) {
            set.add(props[i]);
        }
        return set;
    }

    @Override
    public List<T> put(T key, List<T> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends T, ? extends List<T>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return props.length / 2;
    }

    @Override
    public Collection<List<T>> values() {
        // TODO Auto-generated method stub
        return null;
    }
    
    private class Entry implements java.util.Map.Entry<T, List<T>> {

        private final T key;
        private final List<T> value;
        
        public Entry(T key, List<T> value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public T getKey() {
            return key;
        }

        @Override
        public List<T> getValue() {
            return value;
        }

        @Override
        public List<T> setValue(List<T> arg0) {
            throw new UnsupportedOperationException();
        }
        
    }
}
