package utilities;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance monitoring utility for tracking test execution metrics
 * including timing, memory usage, and performance statistics.
 */
public class PerformanceMonitor {
    
    private static final ThreadLocal<Map<String, Instant>> TIMERS = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> METRICS = new ThreadLocal<>();
    private static final Map<String, PerformanceStats> GLOBAL_STATS = new ConcurrentHashMap<>();
    
    static {
        TIMERS.set(new HashMap<>());
        METRICS.set(new HashMap<>());
    }
    
    /**
     * Start timing a specific operation
     * @param operationName Name of the operation to time
     */
    public static void startTimer(String operationName) {
        TIMERS.get().put(operationName, Instant.now());
    }
    
    /**
     * End timing and return duration in milliseconds
     * @param operationName Name of the operation to end timing for
     * @return Duration in milliseconds
     */
    public static long endTimer(String operationName) {
        Instant startTime = TIMERS.get().get(operationName);
        if (startTime == null) {
            return -1;
        }
        
        Duration duration = Duration.between(startTime, Instant.now());
        long millis = duration.toMillis();
        
        // Store metric
        METRICS.get().put(operationName + "_duration_ms", millis);
        
        // Update global statistics
        GLOBAL_STATS.computeIfAbsent(operationName, k -> new PerformanceStats())
                   .addSample(millis);
        
        // Remove timer
        TIMERS.get().remove(operationName);
        
        return millis;
    }
    
    /**
     * Record a custom metric
     * @param metricName Name of the metric
     * @param value Metric value
     */
    public static void recordMetric(String metricName, Object value) {
        METRICS.get().put(metricName, value);
    }
    
    /**
     * Get current memory usage in MB
     * @return Memory usage in MB
     */
    public static double getCurrentMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return usedMemory / (1024.0 * 1024.0); // Convert to MB
    }
    
    /**
     * Get maximum memory available in MB
     * @return Maximum memory in MB
     */
    public static double getMaxMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() / (1024.0 * 1024.0); // Convert to MB
    }
    
    /**
     * Record memory usage
     * @param label Label for the memory measurement
     */
    public static void recordMemoryUsage(String label) {
        double memoryUsage = getCurrentMemoryUsage();
        METRICS.get().put(label + "_memory_mb", memoryUsage);
    }
    
    /**
     * Get all metrics for current thread
     * @return Map of all metrics
     */
    public static Map<String, Object> getMetrics() {
        return new HashMap<>(METRICS.get());
    }
    
    /**
     * Get performance statistics for an operation
     * @param operationName Name of the operation
     * @return Performance statistics
     */
    public static PerformanceStats getStats(String operationName) {
        return GLOBAL_STATS.get(operationName);
    }
    
    /**
     * Get all global performance statistics
     * @return Map of all performance statistics
     */
    public static Map<String, PerformanceStats> getAllStats() {
        return new HashMap<>(GLOBAL_STATS);
    }
    
    /**
     * Clear all metrics for current thread
     */
    public static void clearMetrics() {
        TIMERS.get().clear();
        METRICS.get().clear();
    }
    
    /**
     * Generate performance report
     * @return Formatted performance report string
     */
    public static String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== PERFORMANCE REPORT ===\n");
        
        // Current thread metrics
        Map<String, Object> metrics = getMetrics();
        if (!metrics.isEmpty()) {
            report.append("Current Thread Metrics:\n");
            metrics.forEach((key, value) -> 
                report.append(String.format("  %s: %s\n", key, value)));
            report.append("\n");
        }
        
        // Global statistics
        Map<String, PerformanceStats> stats = getAllStats();
        if (!stats.isEmpty()) {
            report.append("Global Performance Statistics:\n");
            stats.forEach((operation, perfStats) -> 
                report.append(String.format("  %s: %s\n", operation, perfStats)));
        }
        
        // Memory information
        report.append(String.format("Current Memory Usage: %.2f MB\n", getCurrentMemoryUsage()));
        report.append(String.format("Max Memory Available: %.2f MB\n", getMaxMemory()));
        
        report.append("========================\n");
        return report.toString();
    }
    
    /**
     * Performance statistics for an operation
     */
    public static class PerformanceStats {
        private long totalSamples = 0;
        private long totalTime = 0;
        private long minTime = Long.MAX_VALUE;
        private long maxTime = Long.MIN_VALUE;
        
        public synchronized void addSample(long timeMs) {
            totalSamples++;
            totalTime += timeMs;
            minTime = Math.min(minTime, timeMs);
            maxTime = Math.max(maxTime, timeMs);
        }
        
        public double getAverageTime() {
            return totalSamples > 0 ? (double) totalTime / totalSamples : 0;
        }
        
        public long getMinTime() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }
        
        public long getMaxTime() {
            return maxTime == Long.MIN_VALUE ? 0 : maxTime;
        }
        
        public long getTotalSamples() {
            return totalSamples;
        }
        
        @Override
        public String toString() {
            return String.format(
                "samples=%d, avg=%.2fms, min=%dms, max=%dms",
                totalSamples, getAverageTime(), getMinTime(), getMaxTime()
            );
        }
    }
}