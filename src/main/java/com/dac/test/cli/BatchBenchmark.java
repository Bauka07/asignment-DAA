// File: src/main/java/com/dac/cli/BenchmarkRunner.java
package com.dac.cli;

import com.dac.algorithms.*;
import com.dac.metrics.*;
import com.dac.util.ArrayUtils;
import java.util.*;

public class BenchmarkRunner {
    private final MetricsCollector collector;
    private final CSVWriter csvWriter;
    
    public BenchmarkRunner() {
        this.collector = new MetricsCollector();
        this.csvWriter = new CSVWriter();
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java BenchmarkRunner <algorithm> <size> [iterations]");
            System.out.println("Algorithms: mergesort, quicksort, select, closest");
            System.exit(1);
        }
        
        String algorithm = args[0].toLowerCase();
        int size = Integer.parseInt(args[1]);
        int iterations = args.length > 2 ? Integer.parseInt(args[2]) : 10;
        
        BenchmarkRunner runner = new BenchmarkRunner();
        runner.runSingleBenchmark(algorithm, size, iterations);
    }
    
    public void runSingleBenchmark(String algorithm, int size, int iterations) {
        System.out.println("Algorithm,Size,Iteration,Time_ns,Comparisons,Allocations,MaxDepth");
        
        for (int i = 0; i < iterations; i++) {
            AlgorithmMetrics metrics = collector.startCollection(algorithm);
            
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
                    System.err.println("Unknown algorithm: " + algorithm);
                    return;
            }
            
            collector.endCollection(algorithm);
            
            System.out.printf("%s,%d,%d,%d,%d,%d,%d%n",
                    algorithm, size, i + 1,
                    metrics.getExecutionTimeNs(),
                    metrics.getComparisons(),
                    metrics.getAllocations(),
                    metrics.getMaxDepth());
        }
        
        // Write results to CSV
        List<AlgorithmMetrics> history = collector.getHistory(algorithm);
        List<Integer> sizes = Collections.nCopies(history.size(), size);
        csvWriter.writeMetricsWithTimestamp(algorithm, history, sizes);
    }
    
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
    
    // Multi-size benchmarking
    public void runScalingBenchmark(String algorithm, int[] sizes, int iterations) {
        System.out.println("=== Scaling Benchmark: " + algorithm + " ===");
        
        for (int size : sizes) {
            System.out.printf("Size %d: ", size);
            long totalTime = 0;
            
            for (int i = 0; i < iterations; i++) {
                AlgorithmMetrics metrics = collector.startCollection(algorithm + "_" + size);
                
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
                }
                
                collector.endCollection(algorithm + "_" + size);
                totalTime += metrics.getExecutionTimeNs();
            }
            
            System.out.printf("Avg time: %.2f ms%n", totalTime / (iterations * 1_000_000.0));
        }
    }
}
