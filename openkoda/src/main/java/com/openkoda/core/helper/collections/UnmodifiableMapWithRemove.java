/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.helper.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class UnmodifiableMapWithRemove<K, V> extends HashMap<K, V> {

    @Override
    public V put(K key, V value) { throw new UnsupportedOperationException(); }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) { throw new UnsupportedOperationException(); }

    @Override
    public V putIfAbsent(K key, V value) { throw new UnsupportedOperationException(); }

    @Override
    public boolean replace(K key, V oldValue, V newValue) { throw new UnsupportedOperationException(); }

    @Override
    public V replace(K key, V value) { throw new UnsupportedOperationException(); }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) { throw new UnsupportedOperationException(); }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) { throw new UnsupportedOperationException(); }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) { throw new UnsupportedOperationException(); }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { throw new UnsupportedOperationException(); }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { throw new UnsupportedOperationException(); }

    public Set entrySet() { return new UnmodifiableSetWithRemove(super.entrySet()); }

    public Set keySet() { return new UnmodifiableSetWithRemove(super.keySet()); }

    public Collection values() { return new UnmodifiableSetWithRemove(super.values()); }

    public UnmodifiableMapWithRemove(Map<K, V> m) {
        super(m.size());
        for (Entry<K, V> e : m.entrySet()) super.put(e.getKey(), e.getValue());
    }
}
