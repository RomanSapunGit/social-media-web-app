package com.roman.sapun.java.socialmedia.aspect;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

@Slf4j
public class PerformanceTrackerHandler implements ObservationHandler<Observation.Context> {
    @Override
    public void onStart(@Nonnull Observation.Context context) {
        log.info("execution started {}", context.getName());
        context.put("time", System.currentTimeMillis());
    }

    @Override
    public void onError(@Nonnull Observation.Context context) {
        ObservationHandler.super.onError(context);
    }

    @Override
    public void onEvent(@Nonnull Observation.Event event, @Nonnull Observation.Context context) {
        ObservationHandler.super.onEvent(event, context);
    }

    @Override
    public void onScopeOpened(@Nonnull Observation.Context context) {
        ObservationHandler.super.onScopeOpened(context);
    }

    @Override
    public void onScopeClosed(@Nonnull Observation.Context context) {
        ObservationHandler.super.onScopeClosed(context);
    }

    @Override
    public void onScopeReset(@Nonnull Observation.Context context) {
        ObservationHandler.super.onScopeReset(context);
    }

    @Override
    public void onStop(@Nonnull Observation.Context context) {
        log.info("execution finished " + context.getName() +
                " in " +
                (System.currentTimeMillis() - context.getOrDefault("time", 0L)));
    }

    @Override
    public boolean supportsContext(@Nonnull Observation.Context context) {
        return true;
    }
}
