/*
MIT License

Copyright (c) 2014-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.core.flow;

import reactor.util.function.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * @since 2016-09-25
 */
public class Tuple {
    public final Object[] list;

    public Tuple(Object [] args) {
        list = args;
    }

    public Tuple(Object a1) {        list = new Object[]{a1};    }
    public Tuple(Object a1, Object a2) {        list = new Object[]{a1,a2};    }
    public Tuple(Object a1, Object a2, Object a3) {        list = new Object[]{a1,a2,a3};    }
    public Tuple(Object a1, Object a2, Object a3, Object a4) {        list = new Object[]{a1,a2,a3,a4};    }
    public Tuple(Object a1, Object a2, Object a3, Object a4, Object a5) {        list = new Object[]{a1,a2,a3,a4,a5};    }
    public Tuple(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {        list = new Object[]{a1,a2,a3,a4,a5,a6};    }
    public Tuple(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7) {        list = new Object[]{a1,a2,a3,a4,a5,a6,a7};    }
    public Tuple(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {        list = new Object[]{a1,a2,a3,a4,a5,a6,a7,a8};    }

    public <T> T v(Class<T> c, int index) {
        return (T) list[index];
    }

    public Object getKey() { return v0();}
    public Object getValue() { return v1();}

    public Object v0(){ return list[0]; }
    public Object v1(){ return list[1]; }
    public Object v2(){ return list[2]; }
    public Object v3(){ return list[3]; }
    public Object v4(){ return list[4]; }
    public Object v5(){ return list[5]; }
    public Object v6(){ return list[6]; }
    public Object v7(){ return list[7]; }

    public Object getV0(){ return list[0]; }
    public Object getV1(){ return list[1]; }
    public Object getV2(){ return list[2]; }
    public Object getV3(){ return list[3]; }
    public Object getV4(){ return list[4]; }
    public Object getV5(){ return list[5]; }
    public Object getV6(){ return list[6]; }
    public Object getV7(){ return list[7]; }

    public Object t1() {
        return list[0];
    }
    public Tuple2 t2() {
        return Tuples.of(list[0], list[1]);
    }
    public Tuple3 t3() {
        return Tuples.of(list[0], list[1], list[2]);
    }
    public Tuple4 t4() {
        return Tuples.of(list[0], list[1], list[2], list[3]);
    }
    public Tuple5 t5() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4]);
    }
    public Tuple6 t6() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5]);
    }
    public Tuple7 t7() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5], list[6]);
    }
    public Tuple8 t8() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5], list[6], list[7]);
    }
    public Object getT1() {
        return list[0];
    }
    public Tuple2 getT2() {
        return Tuples.of(list[0], list[1]);
    }
    public Tuple3 getT3() {
        return Tuples.of(list[0], list[1], list[2]);
    }
    public Tuple4 getT4() {
        return Tuples.of(list[0], list[1], list[2], list[3]);
    }
    public Tuple5 getT5() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4]);
    }
    public Tuple6 getT6() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5]);
    }
    public Tuple7 getT7() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5], list[6]);
    }
    public Tuple8 getT8() {
        return Tuples.of(list[0], list[1], list[2], list[3], list[4], list[5], list[6], list[7]);
    }


    public static Tuple collect(Collection<Tuple> tuples, Collector ... collectors) {
        Collection[]result = new Collection[collectors.length];
        int size = tuples.size();
        for (int k = 0; k < collectors.length ; k++) {
            Collector c = collectors[k];
            switch (c) {
                case LIST:
                    result[k] = new ArrayList<>(size); break;
                case SET:
                    result[k] = new HashSet<>(); break;
            }
        }
        for(Tuple t : tuples) {
            for (int k = 0; k < collectors.length ; k++) {
                result[k].add(t);
            }
        }
        return new Tuple(result);
    }

    public static enum Collector {
        LIST, SET
    }

    @Override
    public String toString() {
        return "Tuple: " + Arrays.toString(list);
    }
}
