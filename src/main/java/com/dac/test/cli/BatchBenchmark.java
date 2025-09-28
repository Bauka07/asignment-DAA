// File: src/main/java/com/dac/cli/BatchBenchmark.java
package com.dac.cli;

import com.dac.algorithms.*;
import com.dac.metrics.*;
import com.dac.util.ArrayUtils;
import java.util.*;

public class BatchBenchmark {
    private final MetricsCollector collector;
    private final CSVWriter csvWriter;
    
    public BatchBenchmark() {
        this.collector = new MetricsCollector();
        this.csvWriter = new CSVWriter();
    }
    
    public static void main(String[] args) {
        BatchBenchmark batch = new BatchBenchmark();
        
        if (args.length > 0 && args[0].equals("--comprehensive")) {
            batch.runComprehensiveBenchmark();
        } else {
            batch.runStandardBenchmark();
        }
    }
    
    public void runStandardBenchmark() {
        int[] sizes = {100, 500, 1000, 2000, 5000, 10000};
        String[] algorithms = {"mergesort", "quicksort", "select", "closest"};
        int iterations = 10;
        
        System.out.println("=== Standard Batch Benchmark ===");
        
        for (String algorithm : algorithms) {
            System.out.println("\n--- " + algorithm.toUpperCase() + " ---");
            runAlgorithmBenchmark(algorithm, sizes, iterations);
        }
        
        // Generate summary
        csvWriter.writeBenchmarkResults("batch_summary.csv", collector);
        System.out.println("\nBatch benchmark complete! Results saved to results/");
    }
    
    public void runComprehensiveBenchmark() {
        int[] smallSizes = {10, 20, 50, 100, 200, 500};
        int[] largeSizes = {1000, 2000, 5000, 10000, 20000};
        String[] algorithms = {"mergesort", "quicksort", "select"};
        
        System.out.println("=== Comprehensive Benchmark ===");
        
        // Small sizes with more iterations
        System.out.println("\n-- Small Input Sizes (50 iterations) --");
        for (String algorithm : algorithms) {
            runAlgorithmBenchmark(algorithm, smallSizes, 50);
        }
        
        // Large sizes with fewer iterations
        System.out.println("\n-- Large Input Sizes (5 iterations) --");
        for (String algorithm : algorithms) {
            runAlgorithmBenchmark(algorithm, largeSizes, 5);
        }
        
        // Closest pair with smaller sizes (it's slower)
        int[] closestSizes = {100, 500, 1000, 2000, 5000};
        System.out.println("\n-- Closest Pair Benchmark --");
        runAlgorithmBenchmark("closest", closestSizes, 10);
        
        csvWriter.writeBenchmarkResults("comprehensive_summary.csv", collector);
        System.out.println("\nComprehensive benchmark complete!");
    }
    
    private void runAlgorithmBenchmark(String algorithm, int[] sizes, int iterations) {
        List<AlgorithmMetrics> allMetrics = new ArrayList<>();
        List<Integer> allSizes = new ArrayList<>();
        
        for (int size : sizes) {
            System.out.printf("  Size %d: ", size);
            
            for (int i = 0; i < iterations; i++) {
                AlgorithmMetrics metrics = collector.startCollection(algorithm);
                
                try {
                    switch (algorithm) {
                        case "mergesort":
                            benchmarkMergeSort(size, metrics);
                            break;
                        case "quicksort":
                            benchmarkQuickSort(size, metrics);
                            break;
                        case "select":
                            benchmarkSelect(size, metrics);
                            break;
                        case "closest":
                            benchmarkClosestPair(size, metrics);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
                    }
                } catch (Exception e) {
                    System.err.printf("Error in %s with size %d: %s%n", algorithm, size, e.getMessage());
                    continue;
                }
                
                collector.endCollection(algorithm);
                allMetrics.add(metrics);
                allSizes.add(size);
            }
            
            // Print average for this size
            double avgTime = collector.getHistory(algorithm).stream()
                    .skip(Math.max(0, collector.getHistory(algorithm).size() - iterations))
                    .mapToLong(AlgorithmMetrics::getExecutionTimeNs)
                    .average().orElse(0.0);
            
            System.out.printf("%.2f ms avg%n", avgTime / 1_000_000.0);
        }
        
        // Save individual algorithm results
        csvWriter.writeMetricsWithTimestamp(algorithm + "_batch", allMetrics, allSizes);
    }
    
    // Individual algorithm benchmarking methods
    private void benchmarkMergeSort(int size, AlgorithmMetrics metrics) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        new MergeSort().sort(arr, metrics);
    }
    
    private void benchmarkQuickSort(int size, AlgorithmMetrics metrics) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        new QuickSort().sort(arr, metrics);
    }
    
    private void benchmarkSelect(int size, AlgorithmMetrics metrics) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        int k = new Random().nextInt(size);
        new DeterministicSelect().select(arr, k, metrics);
    }
    
    private void benchmarkClosestPair(int size, AlgorithmMetrics metrics) {
        Point[] points = ArrayUtils.generateRandomPoints(size);
        new ClosestPair().findClosestDistance(points, metrics);
    }
    
    // Specialized benchmarks
    public void runWorstCaseBenchmark() {
        System.out.println("=== Worst Case Benchmark ===");
        int[] sizes = {100, 500, 1000, 2000};
        
        // QuickSort worst case
        System.out.println("\n-- QuickSort Worst Case --");
        for (int size : sizes) {
            AlgorithmMetrics metrics = collector.startCollection("quicksort_worst");
            int[] arr = ArrayUtils.generateWorstCaseQuickSort(size);
            new QuickSort().sort(arr, metrics);
            collector.endCollection("quicksort_worst");
            System.out.printf("Size %d: %.2f ms, Depth: %d%n", 
                    size, metrics.getExecutionTimeMs(), metrics.getMaxDepth());
        }
    }
    
    public void runSelectComparison() {
        System.out.println("=== Select vs Sort Comparison ===");
        int[] sizes = {1000, 5000, 10000, 20000};
        
        for (int size : sizes) {
            System.out.printf("Size %d:%n", size);
            
            // Deterministic Select
            AlgorithmMetrics selectMetrics = collector.startCollection("select");
            int[] arr1 = ArrayUtils.generateRandomArray(size);
            int k = size / 2;
            new DeterministicSelect().select(arr1, k, selectMetrics);
            collector.endCollection("select");
            
            // Arrays.sort then select
            AlgorithmMetrics sortMetrics = collector.startCollection("sort_select");
            sortMetrics.startTiming();
            int[] arr2 = ArrayUtils.generateRandomArray(size);
            Arrays.sort(arr2);
            int result = arr2[k];
            sortMetrics.endTiming();
            collector.endCollection("sort_select");
            
            System.out.printf("  Select: %.2f ms%n", selectMetrics.getExecutionTimeMs());
            System.out.printf("  Sort:   %.2f ms%n", sortMetrics.getExecutionTimeMs());
            System.out.printf("  Speedup: %.2fx%n", 
                    sortMetrics.getExecutionTimeMs() / selectMetrics.getExecutionTimeMs());
        }
    }
}
