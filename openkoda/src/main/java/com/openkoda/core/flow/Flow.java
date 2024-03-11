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
import org.graalvm.polyglot.PolyglotException;
import reactor.util.function.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Flow<I, O, CP> implements Function <ResultAndModel<I, CP>, O>, BasePageAttributes, LoggingComponent {


    @Override
    public O apply(ResultAndModel<I, CP> iResultAndModel) {
        return f.apply(iResultAndModel);
    }

    protected final Function <ResultAndModel<I, CP>, O> f;
    private TransactionalExecutor transactionalExecutor = null;
    protected Supplier<TransactionalExecutor> transactionalExecutorProvider = null;
    protected Consumer<Function> onThen = null;

    private static int flowCounter = 0;
    private final int flowNumber = flowCounter;

    public final CP services;

    public final Map<String, Object> params;

    public static boolean FULL_STACKTRACE_IN_ERROR = true;

    public static Map<String, Object> initParamsMap(Map<String, Object> params) {
        if( params == null ) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(params);
    }

    protected <II, IO, ICP> Flow<II, IO, ICP> constructFlow(Map<String, Object> params, ICP services, Function <ResultAndModel<II, ICP>, IO> f, Supplier<TransactionalExecutor> transactionalExecutorProvider,
                                                            Consumer<Function> onThen) {
        return new Flow<>(params, services, f, transactionalExecutorProvider, onThen);
    }

    protected<IR, ICP> ResultAndModel<IR, ICP> constructResultAndModel(PageModelMap model, IR result, ICP services, Map<String, Object> params) {
        return new ResultAndModel<>(model, result, services, params);
    }

    protected Flow(Map<String, Object> params, CP services, Function <ResultAndModel<I, CP>, O> f, Supplier<TransactionalExecutor> transactionalExecutorProvider,
                   Consumer<Function> onThen) {
        this.params = params;
        this.f = f;
        this.transactionalExecutorProvider = transactionalExecutorProvider;
        this.services = services;
        this.onThen = onThen;
        flowCounter++;
    }

    protected Flow(Map<String, Object> params, CP services, Function <ResultAndModel<I, CP>, O> f) {
        this.params = params;
        this.f = f;
        this.services = services;
        flowCounter++;
    }

    protected <N> void copyTransactionalExecutorProvider(Function after){
        if(onThen != null) {
            onThen.accept(after);
        }
        if (after instanceof Flow) {
            this.transactionalExecutorProvider = ((Flow) after).transactionalExecutorProvider;
        }
    }

    public <N, OI, OO> Flow<I,OO,CP> thenWithoutResult(Function <ResultAndModel<OI,CP>, OO> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services,
                (ResultAndModel<I, CP> t) -> after.apply( constructResultAndModel(t.model, (f.apply(t) == null ? null : null), services, params)),
                transactionalExecutorProvider, onThen);
    }


    public <N> Flow<I,N,CP> then(Function <ResultAndModel<O,CP>, N> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params)), transactionalExecutorProvider, onThen);
    }

    public <N> Flow<I,N,CP> thenSetDefault(PageAttr<N> pageAttr) {
        Function <ResultAndModel<O,CP>, N> after = a -> pageAttr.constructor.get();
        return thenSet(pageAttr, after);
    }

    public <N1, N2> Flow<I,Tuple2<N1, N2>,CP> thenSetDefault(PageAttr<N1> pa1, PageAttr<N2> pa2) {
        Function <ResultAndModel<O,CP>, Tuple2<N1, N2>> after =
                a -> Tuples.of(
                        pa1.constructor.get(),
                        pa2.constructor.get()
                );
        return thenSet(pa1, pa2, after);
    }

    public <N1, N2, N3> Flow<I,Tuple3<N1, N2, N3>,CP> thenSetDefault(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3) {
        Function <ResultAndModel<O,CP>, Tuple3<N1, N2, N3>> after =
                a -> Tuples.of(
                        pa1.constructor.get(),
                        pa2.constructor.get(),
                        pa3.constructor.get()
                );
        return thenSet(pa1, pa2, pa3, after);
    }

    public <N1, N2, N3, N4> Flow<I,Tuple4<N1, N2, N3, N4>,CP> thenSetDefault(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4) {
        Function <ResultAndModel<O,CP>, Tuple4<N1, N2, N3, N4>> after =
                a -> Tuples.of(
                        pa1.constructor.get(),
                        pa2.constructor.get(),
                        pa3.constructor.get(),
                        pa4.constructor.get()
                );
        return thenSet(pa1, pa2, pa3, pa4, after);
    }

    public <N1, N2, N3, N4, N5> Flow<I,Tuple5<N1, N2, N3, N4, N5>,CP> thenSetDefault(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4, PageAttr<N5> pa5) {
        Function <ResultAndModel<O,CP>, Tuple5<N1, N2, N3, N4, N5>> after =
                a -> Tuples.of(
                        pa1.constructor.get(),
                        pa2.constructor.get(),
                        pa3.constructor.get(),
                        pa4.constructor.get(),
                        pa5.constructor.get()
                );
        return thenSet(pa1, pa2, pa3, pa4, pa5, after);
    }

    public <N1, N2, N3, N4, N5, N6> Flow<I,Tuple6<N1, N2, N3, N4, N5, N6>,CP> thenSetDefault(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4, PageAttr<N5> pa5, PageAttr<N6> pa6) {
        Function <ResultAndModel<O,CP>, Tuple6<N1, N2, N3, N4, N5, N6>> after =
                a -> Tuples.of(
                        pa1.constructor.get(),
                        pa2.constructor.get(),
                        pa3.constructor.get(),
                        pa4.constructor.get(),
                        pa5.constructor.get(),
                        pa6.constructor.get()
                );
        return thenSet(pa1, pa2, pa3, pa4, pa5, pa6, after);
    }


    public <N> Flow<I,N,CP> thenSet(PageAttr<N> pageAttr, Function <ResultAndModel<O,CP>, N> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pageAttr , after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }

    public <N1, N2> Flow<I,Tuple2<N1, N2>,CP> thenSet(PageAttr<N1> pa1, PageAttr<N2> pa2, Function <ResultAndModel<O,CP>, Tuple2<N1, N2>> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pa1, pa2, after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }
    public <N1, N2, N3> Flow<I,Tuple3<N1, N2, N3>,CP> thenSet(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, Function <ResultAndModel<O,CP>, Tuple3<N1, N2, N3>> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pa1, pa2, pa3, after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }
    public <N1, N2, N3, N4> Flow<I,Tuple4<N1, N2, N3, N4>,CP> thenSet(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4, Function <ResultAndModel<O,CP>, Tuple4<N1, N2, N3, N4>> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pa1, pa2, pa3, pa4, after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }
    public <N1, N2, N3, N4, N5> Flow<I,Tuple5<N1, N2, N3, N4, N5>,CP> thenSet(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4, PageAttr<N5> pa5,  Function <ResultAndModel<O,CP>, Tuple5<N1, N2, N3, N4, N5>> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pa1, pa2, pa3, pa4, pa5, after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }

    public <N1, N2, N3, N4, N5, N6> Flow<I,Tuple6<N1, N2, N3, N4, N5, N6>,CP> thenSet(PageAttr<N1> pa1, PageAttr<N2> pa2, PageAttr<N3> pa3, PageAttr<N4> pa4, PageAttr<N5> pa5, PageAttr<N6> pa6,  Function <ResultAndModel<O,CP>, Tuple6<N1, N2, N3, N4, N5, N6>> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(pa1, pa2, pa3, pa4, pa5, pa6, after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }

    public static <A, CP> Flow<A, A, CP> init() {
        return new Flow<>(initParamsMap(null), null, a -> a.result);
    }

    public static <A, CP> Flow<A, A, CP> init(CP services) {
        return new Flow<>(initParamsMap(null), services, a -> a.result);
    }

    public static <A, CP> Flow<A, A, CP> init(CP services, Map params) {
        return new Flow<>(initParamsMap(params), services, a -> a.result);
    }

    public static <A,CP> Flow<A, A,CP> init(Supplier<TransactionalExecutor> transactionalExecutorProvider) {
        return new Flow<>(initParamsMap(null), null, a -> a.result, transactionalExecutorProvider, null);
    }

    public static <A,CP> Flow<A, A,CP> init(CP services, Supplier<TransactionalExecutor> transactionalExecutorProvider) {
        return new Flow<>(initParamsMap(null), services, a -> a.result, transactionalExecutorProvider, null);
    }

    public static <I, O,CP> Flow<I, O,CP> init(CP services, Function <ResultAndModel<I, CP>, O> f) {
        return new Flow(initParamsMap(null), services, f);
    }

    public static <A,CP> Flow<A, A,CP> init(CP services, A initValue) {
        return new Flow<>(initParamsMap(null), services, a -> initValue);
    }

    public static <A1, A2,CP> Flow<Tuple2<A1, A2>, Tuple2<A1, A2>,CP> init(CP services, A1 a1, A2 a2) {
        return new Flow<>(initParamsMap(null), services, a -> Tuples.of(a1, a2));
    }

    public static <A1, A2, A3,CP> Flow<Tuple3<A1, A2, A3>, Tuple3<A1, A2, A3>,CP> init(CP services, A1 a1, A2 a2, A3 a3) {
        return new Flow<>(initParamsMap(null), services, a -> Tuples.of(a1, a2, a3));
    }

    public static <A1, A2, A3, A4, A5,CP> Flow<Tuple5<A1, A2, A3, A4, A5>, Tuple5<A1, A2, A3, A4, A5>,CP> init(CP services, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return new Flow<>(initParamsMap(null), services, a -> Tuples.of(a1, a2, a3, a4, a5));
    }

    public static <A1, A2, A3, A4, A5, A6,CP> Flow<Tuple6<A1, A2, A3, A4, A5, A6>, Tuple6<A1, A2, A3, A4, A5, A6>,CP> init(CP services, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return new Flow<>(initParamsMap(null), services, a -> Tuples.of(a1, a2, a3, a4, a5, a6));
    }

    public static <A1,CP> Flow<A1, A1, CP> init(PageAttr<A1> a1, A1 t) {
        return Flow.init(null, a1, t);
    }

    public static <A1,CP> Flow<A1, A1, CP> init(CP services, PageAttr<A1> a1, A1 t) {
        return Flow.init(services, t).thenSet(a1, a -> a.result);
    }

    public static <A1, A2,CP> Flow<Tuple2<A1, A2>, Tuple2<A1, A2>,CP> init(CP services, PageAttr<A1> a1, PageAttr<A2> a2, Tuple2<A1, A2> t) {
        return Flow.init(services, t).thenSet(a1, a2, a -> a.result);
    }

    public static <A1, A2, A3,CP> Flow<Tuple3<A1, A2, A3>, Tuple3<A1, A2, A3>,CP> init(CP services, PageAttr<A1> a1, PageAttr<A2> a2, PageAttr<A3> a3, Tuple3<A1, A2, A3> t) {
        return Flow.init(services, t).thenSet(a1, a2, a3, a -> a.result);
    }

    public static <A1, A2, A3, A4,CP> Flow<Tuple4<A1, A2, A3, A4>, Tuple4<A1, A2, A3, A4>,CP> init(CP services, PageAttr<A1> a1, PageAttr<A2> a2, PageAttr<A3> a3, PageAttr<A4> a4, Tuple4<A1, A2, A3, A4> t) {
        return Flow.init(services, t).thenSet(a1, a2, a3, a4, a -> a.result);
    }

    public static <A1, A2, A3, A4, A5,CP> Flow<Tuple5<A1, A2, A3, A4, A5>, Tuple5<A1, A2, A3, A4, A5>,CP> init(CP services, PageAttr<A1> a1, PageAttr<A2> a2, PageAttr<A3> a3, PageAttr<A4> a4, PageAttr<A5> a5, Tuple5<A1, A2, A3, A4, A5> t) {
        return Flow.init(services, t).thenSet(a1, a2, a3, a4, a5, a -> a.result);
    }

    public static <A1, A2, A3, A4, A5, A6,CP> Flow<Tuple6<A1, A2, A3, A4, A5, A6>, Tuple6<A1, A2, A3, A4, A5, A6>,CP> init(CP services, PageAttr<A1> a1, PageAttr<A2> a2, PageAttr<A3> a3, PageAttr<A4> a4, PageAttr<A5> a5, PageAttr<A6> a6, Tuple6<A1, A2, A3, A4, A5, A6> t) {
        return Flow.init(services, t).thenSet(a1, a2, a3, a4, a5, a6, a -> a.result);
    }

    public PageModelMap execute() {
        PageModelMap model = new PageModelMap();
        ResultAndModel<O,CP> result = executeFlow(model);
        return model;
    }

    public PageModelMap execute(PageModelMap model) {
        ResultAndModel<O,CP> result = executeFlow(model);
        return model;
    }

    @Deprecated(forRemoval = true)
    public ResultAndModel<O,CP> executeWithResult() {
        PageModelMap model = new PageModelMap();
        ResultAndModel<O,CP> result = executeFlow(model);
        return result;
    }

    private ResultAndModel<O,CP> executeFlow(PageModelMap model) {
        try {
            O result = null;
            if(transactionalExecutor == null && transactionalExecutorProvider != null) {
                transactionalExecutor = transactionalExecutorProvider.get();
            }
            if(transactionalExecutor == null) {
                result = apply(constructResultAndModel(model, null, services, params));
            } else {
                transactionalExecutor.executeInTransaction(() -> apply(constructResultAndModel(model, null, services, params)));
            }
            model.put(isError, message, false, "");
            applyPostExecuteProcessors(model);
            return constructResultAndModel(model, result, services, params);
        } catch (HttpStatusException e) {
            logError(e);
            model.put(isError, message, error, exception, true, getSimpleErrorMessage(e), getMessageString(e), e);
            throw e;
        } catch (ValidationException e) {
            logError(e);
            model.put(isError, message, error, exception, true, getSimpleErrorMessage(e), getMessageString(e), e);
        } catch (PolyglotException e) {
            String simpleMessage = getSimpleErrorMessage(e);
            JsFlowExecutionException jsException;
            if (e.getSourceLocation() != null) {
                String code = e.getSourceLocation().getCharacters() != null ? e.getSourceLocation().getCharacters().toString() : null;
                String location = e.getSourceLocation().toString();
                jsException = new JsFlowExecutionException(simpleMessage, code, location);
            } else {
                jsException = new JsFlowExecutionException(simpleMessage, e);
            }
            model.put(isError, message, error, exception, true, getSimpleErrorMessage(jsException), getMessageString(jsException), jsException);
            throw jsException;
        } catch (Exception e) {
            logError(e);
            throw e;
        }
        applyPostExecuteProcessors(model);
        return constructResultAndModel(model, null, services, params);
    }

    private void logError(Exception e) {
        String messageString = getSimpleErrorMessage(e);
        StackTraceElement[] st = e.getStackTrace();
        String stLine1 = (st != null && st.length > 0) ? "" + st[0] : "N/A";
        String stLine2 = (st != null && st.length > 1) ? "" + st[1] : "N/A";
        error(e, "[execute] {} {} {}", messageString, stLine1, stLine2);
    }

    private String getSimpleErrorMessage(Exception e) {
        return e.getMessage() != null ? e.getMessage() : "N/A";
    }

    private void applyPostExecuteProcessors(PageModelMap model) {
        for (Object o : model.values()) {
            if (PostExecuteProcessablePageAttr.class.isAssignableFrom(o.getClass())) {
                ((PostExecuteProcessablePageAttr)o).process();
            }
        }
    }

    private String getMessageString(Exception e) {
        String messageString = e.getClass().getSimpleName();
        if(FULL_STACKTRACE_IN_ERROR) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            messageString = sw.toString();
        }
        return messageString;
    }

    public Flow<I, O,CP> setTransactionalExecutor(TransactionalExecutor transactionalExecutor) {
        this.transactionalExecutor = transactionalExecutor;
        return this;
    }

    public Flow<I, O,CP> setTransactionalExecutorProvider(Supplier<TransactionalExecutor> transactionalExecutorProvider) {
        this.transactionalExecutorProvider = transactionalExecutorProvider;
        return this;
    }

    public Flow<I, O, CP> onThen(Consumer<Function> onThen) {
        this.onThen = onThen;
        return this;
    }

    @Override
    public String toString() {
        return "Flow-" + flowNumber;
    }


    public Flow<I,Object,CP> thenSet(String modelKey, Function<ResultAndModel<O,CP>, Object> after) {
        Objects.requireNonNull(after);
        copyTransactionalExecutorProvider(after);
        return constructFlow(params, services, (ResultAndModel<I, CP> t) -> t.model.put(modelKey , after.apply( constructResultAndModel(t.model, f.apply(t), t.services, params))), transactionalExecutorProvider, onThen);
    }
}
