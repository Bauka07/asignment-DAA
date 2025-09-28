package com.dac.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsCollector {
    private final Map<String, List<AlgorithmMetrics>> metricsHistory;
    private final Map<String, AlgorithmMetrics> currentMetrics;
    
    public MetricsCollector() {
        this.metricsHistory = new ConcurrentHashMap<>();
        this.currentMetrics = new ConcurrentHashMap<>();
    }
    
    public AlgorithmMetrics startCollection(String algorithmName) {
        AlgorithmMetrics metrics = new AlgorithmMetrics(algorithmName);
        currentMetrics.put(algorithmName, metrics);
        return metrics;
    }
    
    public void endCollection(String algorithmName) {
        AlgorithmMetrics metrics = currentMetrics.get(algorithmName);
        if (metrics != null) {
            metrics.endTiming();
            metricsHistory.computeIfAbsent(algorithmName, k -> new ArrayList<>()).add(metrics);
        }
    }
    
    public AlgorithmMetrics getCurrentMetrics(String algorithmName) {
        return currentMetrics.get(algorithmName);
    }
    
    public List<AlgorithmMetrics> getHistory(String algorithmName) {
        return metricsHistory.getOrDefault(algorithmName, new ArrayList<>());
    }
    
    public Map<String, List<AlgorithmMetrics>> getAllHistory() {
        return new HashMap<>(metricsHistory);
    }
    
    public void clearHistory() {
        metricsHistory.clear();
        currentMetrics.clear();
    }
    
    public void clearHistory(String algorithmName) {
        metricsHistory.remove(algorithmName);
        currentMetrics.remove(algorithmName);
    }
    
    // Statistical analysis
    public double getAverageExecutionTime(String algorithmName) {
        List<AlgorithmMetrics> history = getHistory(algorithmName);
        if (history.isEmpty()) return 0.0;
        
        return history.stream()
                .mapToLong(AlgorithmMetrics::getExecutionTimeNs)
                .average()
                .orElse(0.0);
    }
    
    public double getAverageComparisons(String algorithmName) {
        List<AlgorithmMetrics> history = getHistory(algorithmName);
        if (history.isEmpty()) return 0.0;
        
        return history.stream()
                .mapToLong(AlgorithmMetrics::getComparisons)
                .average()
                .orElse(0.0);
    }
    
    public double getAverageMaxDepth(String algorithmName) {
        List<AlgorithmMetrics> history = getHistory(algorithmName);
        if (history.isEmpty()) return 0.0;
        
        return history.stream()
                .mapToInt(AlgorithmMetrics::getMaxDepth)
                .average()
                .orElse(0.0);
    }
}
