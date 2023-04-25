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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import reactor.util.function.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.openkoda.core.flow.BasePageAttributes.bindingResult;
import static com.openkoda.core.flow.BasePageAttributes.isError;
import static java.util.Optional.ofNullable;

public class PageModelMap extends HashMap<String, Object> {

    private static final long serialVersionUID = -6492787151065187523L;

    final Map<String, Object> added = new HashMap<>();
    final Map<String, Object> removed = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(PageAttr<T> key) {
        return (T) super.get(key.name);
    }

    public <F, T> PageModelMap convert(PageAttr<F> fromKey, PageAttr<T> targetKey, Function<F, T> remappingFunction) {
        F from = this.get(fromKey);
        if (from == null) {
            return this;
        } else {
            T result = remappingFunction.apply(from);
            this.remove(fromKey);
            this.put(targetKey, result);
            return this;
        }
    }

    public Map<String, Object> getAsMap(PageAttr... keys) {
        Map<String, Object> result = new HashMap<>(keys.length);
        result.put(isError.name, get(isError));
        for (PageAttr key : keys) {
            result.put(key.name, get(key));
        }
        result.remove(bindingResult.name);
        return result;
    }


    @SuppressWarnings("unchecked")
    public <T1, T2> Tuple2<T1, T2> get(PageAttr<T1> key1, PageAttr<T2> key2) {
        return Tuples.of((T1) super.get(key1.name), (T2) super.get(key2.name));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3> Tuple3<T1, T2, T3> get(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3) {
        return Tuples.of((T1) super.get(key1.name), (T2) super.get(key2.name), (T3) super.get(key3.name));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> get(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4) {
        return Tuples.of((T1) super.get(key1.name), (T2) super.get(key2.name), (T3) super.get(key3.name), (T4) super.get(key4.name));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> get(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5) {
        return Tuples.of((T1) super.get(key1.name), (T2) super.get(key2.name), (T3) super.get(key3.name), (T4) super.get(key4.name), (T5) super.get(key5.name));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> get(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, PageAttr<T6> key6) {
        return Tuples.of((T1) super.get(key1.name), (T2) super.get(key2.name), (T3) super.get(key3.name), (T4) super.get(key4.name), (T5) super.get(key5.name), (T6) super.get(key6.name));
    }

    @SuppressWarnings("unchecked")
    public <T> T put(PageAttr<T> key, T value) {
        return (T) this.put(key.name, value);
    }

    public Object put(String key, Object value) {
        if (value == null) {
            Object t = super.remove(key);
            if (t != null) {
                removed.put(key, t);
            }
            return t;
        }

        added.put(key, value);
        super.put(key, value);
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T1, T2> Tuple2<T1, T2> put(PageAttr<T1> key1, PageAttr<T2> key2, Tuple2<T1, T2> value) {
        return Tuples.of(this.put(key1, value.getT1()), this.put(key2, value.getT2()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2> Tuple2<T1, T2> put(PageAttr<T1> key1, PageAttr<T2> key2, Tuple value) {
        return Tuples.of(this.put(key1, (T1) value.getT1()), this.put(key2, (T2) value.getT2()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2> Tuple2<T1, T2> put(PageAttr<T1> key1, PageAttr<T2> key2, T1 v1, T2 v2) {
        return Tuples.of(this.put(key1, v1), this.put(key2, v2));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3> Tuple3<T1, T2, T3> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, T1 v1, T2 v2, T3 v3) {
        return Tuples.of(this.put(key1, v1), this.put(key2, v2), this.put(key3, v3));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3> Tuple3<T1, T2, T3> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, Tuple3<T1, T2, T3> value) {
        return Tuples.of(this.put(key1, value.getT1()), this.put(key2, value.getT2()), this.put(key3, value.getT3()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3> Tuple3<T1, T2, T3> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, Tuple value) {
        return Tuples.of(this.put(key1, (T1) value.getT1()), this.put(key2, (T2) value.getT2()), this.put(key3, (T3) value.getT3()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, Tuple4<T1, T2, T3, T4> value) {
        return Tuples.of(this.put(key1, value.getT1()), this.put(key2, value.getT2()), this.put(key3, value.getT3()), this.put(key4, value.getT4()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, Tuple value) {
        return Tuples.of(this.put(key1, (T1) value.getT1()), this.put(key2, (T2) value.getT2()), this.put(key3, (T3) value.getT3()), this.put(key4, (T4) value.getT4()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, T1 v1, T2 v2, T3 v3, T4 v4) {
        return Tuples.of(this.put(key1, v1), this.put(key2, v2), this.put(key3, v3), this.put(key4, v4));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, Tuple5<T1, T2, T3, T4, T5> value) {
        return Tuples.of(this.put(key1, value.getT1()), this.put(key2, value.getT2()), this.put(key3, value.getT3()), this.put(key4, value.getT4()), this.put(key5, value.getT5()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, Tuple value) {
        return Tuples.of(this.put(key1, (T1) value.getT1()), this.put(key2, (T2) value.getT2()), this.put(key3, (T3) value.getT3()), this.put(key4, (T4) value.getT4()), this.put(key5, (T5) value.getT5()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5) {
        return Tuples.of(this.put(key1, v1), this.put(key2, v2), this.put(key3, v3), this.put(key4, v4), this.put(key5, v5));
    }


    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, PageAttr<T6> key6, Tuple6<T1, T2, T3, T4, T5, T6> value) {
        return Tuples.of(this.put(key1, value.getT1()), this.put(key2, value.getT2()), this.put(key3, value.getT3()), this.put(key4, value.getT4()), this.put(key5, value.getT5()), this.put(key6, value.getT6()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, PageAttr<T6> key6, Tuple value) {
        return Tuples.of(this.put(key1, (T1) value.getT1()), this.put(key2, (T2) value.getT2()), this.put(key3, (T3) value.getT3()), this.put(key4, (T4) value.getT4()), this.put(key5, (T5) value.getT5()), this.put(key6, (T6) value.getT6()));
    }

    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> put(PageAttr<T1> key1, PageAttr<T2> key2, PageAttr<T3> key3, PageAttr<T4> key4, PageAttr<T5> key5, PageAttr<T6> key6, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6) {
        return Tuples.of(this.put(key1, v1), this.put(key2, v2), this.put(key3, v3), this.put(key4, v4), this.put(key5, v5), this.put(key6, v6));
    }


    @SuppressWarnings("unchecked")
    public <T> T remove(PageAttr<T> key) {
        T t = (T) super.remove(key.name);
        removed.put(key.name, t);
        return t;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(PageAttr<T> key, Object defaultValue) {
        return (T) super.getOrDefault(key.name, defaultValue);
    }

    public <T> boolean has(PageAttr<T> key) {
        return super.containsKey(key.name);
    }

    void clearTrace() {
        added.clear();
        removed.clear();
    }

    public boolean isValidationOk() {
        return false;
    }

    public Object mav(HttpServletRequest request) {
        return mav(request, null);
    }

    public Object mav(
            HttpServletRequest request,
            String viewNameForSuccess, String viewNameForValidationError,
            PageModelFunction<Object> forSuccess, PageModelFunction<Object> forValidationError
            ) {
        return mav(request, (r) -> Tuples.of(
                ofNullable(viewNameForSuccess),
                ofNullable(viewNameForValidationError),
                ofNullable(forSuccess),
                ofNullable(forValidationError)));
    }

    public Object mav(
            HttpServletRequest request,
            String viewName,
            PageModelFunction<Object> forJson
            ) {
        return mav(request, (r) -> Tuples.of(
                ofNullable(viewName),
                ofNullable(viewName),
                ofNullable(forJson),
                ofNullable(forJson)));
    }

    private Object mav(
            HttpServletRequest request,
            ViewAndObjectProvider viewProvider) {
        PageModelMap model = this;
        if (viewProvider == null) {
            return model;
        }

        boolean error = model.has(isError) ? model.get(isError) : false;

        Tuple4<
                Optional<String>,
                Optional<String>,
                Optional<PageModelFunction>,
                Optional<PageModelFunction>> p
                = viewProvider.apply(request);

        if (error) {
            if (p.getT2().isPresent()) {
                //there is error and we know view name
                return new ModelAndView(p.getT2().get(), model);
            }

            if (p.getT4().isPresent()) {
                //there is error and we know what model we want to return
                return p.getT4().get().getResult(model);
            }
        }

        if (p.getT1().isPresent()) {
            //no error or no error handler and we know view name
            return new ModelAndView(p.getT1().get(), model);
        }

        if (p.getT3().isPresent()) {
            //no error or no error handler and we know what model we want to return
            return p.getT3().get().getResult(model);
        }

        return model;
    }


    /**
     * Executes the stack and returns {@link ModelAndView} depending on validation result
     *
     * @param viewNameForSuccess         view name returned when validation passes
     * @param viewNameForValidationError view name returned when validation fails
     * @return ModelAndView object
     */
    public ModelAndView mav(String viewNameForSuccess, String viewNameForValidationError) {
        PageModelMap model = this;
        return model.get(isError) ? new ModelAndView(viewNameForValidationError, model) : new ModelAndView(viewNameForSuccess, model);
    }

    /**
     * Executes the stack and returns object depending on validation result
     *
     * @param forSuccess         object provider used to return object when validation passes
     * @param forValidationError object provider used to return object when validation fails
     * @return object provided by one of given providers
     */
    public Object mav(PageModelFunction<Object> forSuccess, PageModelFunction<Object> forValidationError) {
        PageModelMap model = this;
        return model.get(isError) ? forValidationError.getResult(model) : forSuccess.getResult(model);
    }

    /**
     * Executes the stack and returns object depending on validation result
     *
     * @param forSuccess                 object provider used to return object when validation passes
     * @param viewNameForValidationError view name returned when validation fails
     * @return object provided by one of given providers
     */
    public Object mav(PageModelFunction<Object> forSuccess, String viewNameForValidationError) {
        PageModelMap model = this;
        return model.get(isError) ? new ModelAndView(viewNameForValidationError, model) : forSuccess.getResult(model);
    }

    /**
     * Executes the stack and returns object depending on validation result
     *
     * @param viewNameForSuccess view name returned when validation passes
     * @param forValidationError object provider used to return object when validation fails
     * @return object provided by one of given providers
     */
    public Object mav(String viewNameForSuccess, PageModelFunction<Object> forValidationError) {
        PageModelMap model = this;
        return model.get(isError) ? forValidationError.getResult(model) : new ModelAndView(viewNameForSuccess, model);
    }

    /**
     * Executes the stack and returns object returned by given provider
     *
     * @param resultProvider
     * @return object returned by given provider
     */
    public Object mav(PageModelFunction<Object> resultProvider) {
        return mav(resultProvider, resultProvider);
    }

    /**
     * Executes the stack and returns {@link ModelAndView} object with the result
     *
     * @param viewName name of the view
     * @return ModelAndView object
     */
    public ModelAndView mav(String viewName) {
        return mav(viewName, viewName);
    }


}