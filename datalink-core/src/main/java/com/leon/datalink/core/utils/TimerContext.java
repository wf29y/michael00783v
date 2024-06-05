

package com.leon.datalink.core.utils;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple task time calculation，Currently only the task time statistics task that supports synchronizing code blocks is
 * supported.
 *
 * @author Leon
 */
public class TimerContext {
    
    private static final ThreadLocal<Map<String, Long>> TIME_RECORD = ThreadLocal.withInitial(() -> new HashMap<>(2));
    
    /**
     * Record context start time.
     *
     * @param name context name
     */
    public static void start(final String name) {
        TIME_RECORD.get().put(name, System.currentTimeMillis());
    }
    
    public static void end(final String name, final Logger logger) {
        end(name, logger, LoggerUtils.DEBUG);
    }
    
    /**
     * End the task and print based on the log level.
     *
     * @param name   context name
     * @param logger logger
     * @param level  logger level
     */
    public static void end(final String name, final Logger logger, final String level) {
        Map<String, Long> record = TIME_RECORD.get();
        long contextTime = System.currentTimeMillis() - record.remove(name);
        if (StringUtils.equals(level, LoggerUtils.DEBUG)) {
            LoggerUtils.printIfDebugEnabled(logger, "{} cost time : {} ms", name, contextTime);
        }
        if (StringUtils.equals(level, LoggerUtils.INFO)) {
            LoggerUtils.printIfInfoEnabled(logger, "{} cost time : {} ms", name, contextTime);
        }
        if (StringUtils.equals(level, LoggerUtils.TRACE)) {
            LoggerUtils.printIfTraceEnabled(logger, "{} cost time : {} ms", name, contextTime);
        }
        if (StringUtils.equals(level, LoggerUtils.ERROR)) {
            LoggerUtils.printIfErrorEnabled(logger, "{} cost time : {} ms", name, contextTime);
        }
        if (StringUtils.equals(level, LoggerUtils.WARN)) {
            LoggerUtils.printIfWarnEnabled(logger, "{} cost time : {} ms", name, contextTime);
        }
        if (record.isEmpty()) {
            TIME_RECORD.remove();
        }
    }
    
    /**
     * Execution with time-consuming calculations for {@link Runnable}.
     *
     * @param job    runnable
     * @param name   job name
     * @param logger logger
     */
    public static void run(final Runnable job, final String name, final Logger logger) {
        start(name);
        try {
            job.run();
        } finally {
            end(name, logger);
        }
    }
    
    /**
     * Execution with time-consuming calculations for {@link Supplier}.
     *
     * @param job    Supplier
     * @param name   job name
     * @param logger logger
     */
    public static <V> V run(final Supplier<V> job, final String name, final Logger logger) {
        start(name);
        try {
            return job.get();
        } finally {
            end(name, logger);
        }
    }
    
    /**
     * Execution with time-consuming calculations for {@link Function}.
     *
     * @param job    Function
     * @param args   args
     * @param name   job name
     * @param logger logger
     */
    public static <T, R> R run(final Function<T, R> job, T args, final String name, final Logger logger) {
        start(name);
        try {
            return job.apply(args);
        } finally {
            end(name, logger);
        }
    }
    
    /**
     * Execution with time-consuming calculations for {@link Consumer}.
     *
     * @param job    Consumer
     * @param args   args
     * @param name   job name
     * @param logger logger
     */
    public static <T> void run(final Consumer<T> job, T args, final String name, final Logger logger) {
        start(name);
        try {
            job.accept(args);
        } finally {
            end(name, logger);
        }
    }
    
}
