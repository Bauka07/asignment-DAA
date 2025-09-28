// AlgorithmMetrics.java - Core metrics collection
package com.dac.metrics;

public class AlgorithmMetrics {
    private long comparisons;
    private long swaps;
    private int maxDepth;
    private int currentDepth;
    private long startTime;
    private long endTime;
    private long memoryAllocations;
    
    public AlgorithmMetrics() {
        reset();
    }
    
    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.maxDepth = 0;
        this.currentDepth = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.memoryAllocations = 0;
    }
    
    public void startTiming() {
        this.startTime = System.nanoTime();
    }
    
    public void endTiming() {
        this.endTime = System.nanoTime();
    }
    
    public void enterRecursion() {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }
    
    public void exitRecursion() {
        currentDepth--;
    }
    
    public void incrementComparisons() {
        comparisons++;
    }
    
    public void incrementSwaps() {
        swaps++;
    }
    
    public void incrementAllocations(int size) {
        memoryAllocations += size;
    }
    
    // Getters
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public int getMaxDepth() { return maxDepth; }
    public long getExecutionTimeNanos() { return endTime - startTime; }
    public double getExecutionTimeMs() { return (endTime - startTime) / 1_000_000.0; }
    public long getMemoryAllocations() { return memoryAllocations; }
}
