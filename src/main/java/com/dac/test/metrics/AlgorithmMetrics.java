// File: src/main/java/com/dac/metrics/AlgorithmMetrics.java
package com.dac.metrics;

public class AlgorithmMetrics {
    private long comparisons;
    private long allocations;
    private int maxDepth;
    private int currentDepth;
    private long startTime;
    private long endTime;
    private String algorithmName;
    
    public AlgorithmMetrics(String algorithmName) {
        this.algorithmName = algorithmName;
        reset();
    }
    
    public void reset() {
        comparisons = 0;
        allocations = 0;
        maxDepth = 0;
        currentDepth = 0;
        startTime = 0;
        endTime = 0;
    }
    
    public void startTiming() {
        startTime = System.nanoTime();
    }
    
    public void endTiming() {
        endTime = System.nanoTime();
    }
    
    public void incrementComparisons() {
        comparisons++;
    }
    
    public void addComparisons(long count) {
        comparisons += count;
    }
    
    public void incrementAllocations() {
        allocations++;
    }
    
    public void addAllocations(long count) {
        allocations += count;
    }
    
    public void enterRecursion() {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }
    
    public void exitRecursion() {
        currentDepth--;
    }
    
    // Getters
    public String getAlgorithmName() { return algorithmName; }
    public long getComparisons() { return comparisons; }
    public long getAllocations() { return allocations; }
    public int getMaxDepth() { return maxDepth; }
    public int getCurrentDepth() { return currentDepth; }
    public long getExecutionTimeNs() { return endTime - startTime; }
    public double getExecutionTimeMs() { return (endTime - startTime) / 1_000_000.0; }
    
    @Override
    public String toString() {
        return String.format("%s - Time: %.2f ms, Comparisons: %d, Allocations: %d, Max Depth: %d",
                algorithmName, getExecutionTimeMs(), comparisons, allocations, maxDepth);
    }
    
    public String toCsvRow(int inputSize) {
        return String.format("%s,%d,%d,%d,%d,%d", 
                algorithmName, inputSize, getExecutionTimeNs(), comparisons, allocations, maxDepth);
    }
}
