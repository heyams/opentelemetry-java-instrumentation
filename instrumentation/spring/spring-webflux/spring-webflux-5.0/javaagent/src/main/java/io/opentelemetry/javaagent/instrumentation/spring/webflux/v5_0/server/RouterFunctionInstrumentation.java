/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.webflux.v5_0.server;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.extendsClass;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.instrumentation.spring.webflux.v5_0.server.WebfluxSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RouterFunctionInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<ClassLoader> classLoaderOptimization() {
    return hasClassesNamed("org.springframework.web.reactive.function.server.ServerRequest");
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return not(isAbstract())
        .and(
            extendsClass(
                // TODO: this doesn't handle nested routes (DefaultNestedRouterFunction)
                named(
                    "org.springframework.web.reactive.function.server.RouterFunctions$DefaultRouterFunction")));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("route"))
            .and(
                takesArgument(
                    0, named("org.springframework.web.reactive.function.server.ServerRequest")))
            .and(takesArguments(1)),
        this.getClass().getName() + "$RouteAdvice");
  }

  /**
   * This advice is responsible for setting additional span parameters for routes implemented with
   * functional interface.
   */
  @SuppressWarnings("unused")
  public static class RouteAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.Argument(0) ServerWebExchange exchange,
        @Advice.Argument(1) Object handler,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {

      if (handler == null) {
        return;
      }

      Context parentContext = Context.current();
      if (!instrumenter().shouldStart(parentContext, handler)) {
        return;
      }

      context = instrumenter().start(parentContext, handler);
      scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void methodExit(
        @Advice.This RouterFunction<?> thiz,
        @Advice.Return(readOnly = false) Mono<HandlerFunction<?>> result,
        @Advice.Thrown Throwable throwable) {
      if (throwable == null) {
        result = result.doOnNext(new RouteOnSuccess(thiz));
      }
    }
  }
}
