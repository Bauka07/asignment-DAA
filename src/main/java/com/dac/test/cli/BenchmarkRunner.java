// BenchmarkRunner.java - CLI for running benchmarks
package com.dac.cli;

import com.dac.algorithms.*;
import com.dac.metrics.*;
import com.dac.util.ArrayUtils;
import java.io.IOException;
import java.util.Arrays;

public class BenchmarkRunner {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            runFullBenchmark();
        } else {
            String algorithm = args[0];
            int size = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
            runSingleBenchmark(algorithm, size);
        }
    }
    
    public static void runFullBenchmark() {
        System.out.println("Running comprehensive benchmark suite...");
        
        int[] sizes = {100, 500, 1000, 2000, 5000, 10000, 20000, 50000};
        CSVWriter csvWriter = new CSVWriter("results/benchmark_results.csv");
        csvWriter.setHeaders("Algorithm", "InputSize", "InputType", "TimeMs", "MaxDepth", 
                           "Comparisons", "Swaps", "MemoryAllocations");
        
        for (int size : sizes) {
            System.out.printf("Benchmarking size: %d%n", size);
            
            // Test different input types
            benchmarkSorting(csvWriter, size, "Random", ArrayUtils.generateRandomArray(size));
            benchmarkSorting(csvWriter, size, "Sorted", ArrayUtils.generateSortedArray(size));
            benchmarkSorting(csvWriter, size, "Reverse", ArrayUtils.generateReverseSortedArray(size));
            
            // Benchmark Select
            benchmarkSelect(csvWriter, size);
            
            // Benchmark Closest Pair (smaller sizes due to complexity)
            if (size <= 10000) {
                benchmarkClosestPair(csvWriter, size);
            }
        }
        
        try {
            csvWriter.writeToFile();
            System.out.println("Results written to results/benchmark_results.csv");
        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }
    
    private static void benchmarkSorting(CSVWriter csvWriter, int size, String inputType, int[] baseArray) {
        String[] algorithms = {"MergeSort", "QuickSort"};
        
        for (String algorithm : algorithms) {
            int[] arr = ArrayUtils.copyArray(baseArray);
            AlgorithmMetrics metrics = new AlgorithmMetrics();
            
            switch (algorithm) {
                case "MergeSort":
                    MergeSort.sort(arr, metrics);
                    break;
                case "QuickSort":
                    QuickSort.sort(arr, metrics);
                    break;
            }
            
            // Verify correctness
            if (!ArrayUtils.isSorted(arr)) {
                System.err.println("ERROR: " + algorithm + " failed to sort array correctly!");
            }
            
            csvWriter.addRow(
                algorithm,
                String.valueOf(size),
                inputType,
                String.format("%.3f", metrics.getExecutionTimeMs()),
                String.valueOf(metrics.getMaxDepth()),
                String.valueOf(metrics.getComparisons()),
                String.valueOf(metrics.getSwaps()),
                String.valueOf(metrics.getMemoryAllocations())
            );
        }
    }
    
    private static void benchmarkSelect(CSVWriter csvWriter, int size) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        int k = size / 2; // Find median
        
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        int result = DeterministicSelect.select(ArrayUtils.copyArray(arr), k, metrics);
        
        // Verify correctness
        int[] sorted = ArrayUtils.copyArray(arr);
        Arrays.sort(sorted);
        if (result != sorted[k]) {
            System.err.println("ERROR: DeterministicSelect returned incorrect result!");
        }
        
        csvWriter.addRow(
            "DeterministicSelect",
            String.valueOf(size),
            "Random",
            String.format("%.3f", metrics.getExecutionTimeMs()),
            String.valueOf(metrics.getMaxDepth()),
            String.valueOf(metrics.getComparisons()),
            String.valueOf(metrics.getSwaps()),
            String.valueOf(metrics.getMemoryAllocations())
        );
    }
    
    private static void benchmarkClosestPair(CSVWriter csvWriter, int size) {
        Point[] points = ClosestPair.generateRandomPoints(size);
        
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        PointPair result = ClosestPair.findClosestPair(points, metrics);
        
        // Verify with brute force for smaller sizes
        if (size <= 2000) {
            AlgorithmMetrics bruteMetrics = new AlgorithmMetrics();
            PointPair bruteResult = ClosestPair.bruteForceClosestPair(points, bruteMetrics);
            
            if (Math.abs(result.distance - bruteResult.distance) > 1e-9) {
                System.err.println("ERROR: ClosestPair returned incorrect result!");
            }
        }
        
        csvWriter.addRow(
            "ClosestPair",
            String.valueOf(size),
            "Random",
            String.format("%.3f", metrics.getExecutionTimeMs()),
            String.valueOf(metrics.getMaxDepth()),
            String.valueOf(metrics.getComparisons()),
            String.valueOf(metrics.getSwaps()),
            String.valueOf(metrics.getMemoryAllocations())
        );
    }
    
    public static void runSingleBenchmark(String algorithm, int size) {
        System.out.printf("Running %s benchmark with size %d%n", algorithm, size);
        
        switch (algorithm.toLowerCase()) {
            case "mergesort":
                testMergeSort(size);
                break;
            case "quicksort":
                testQuickSort(size);
                break;
            case "select":
                testSelect(size);
                break;
            case "closest":
                testClosestPair(size);
                break;
            default:
                System.err.println("Unknown algorithm: " + algorithm);
                System.err.println("Available: mergesort, quicksort, select, closest");
        }
    }
    
    private static void testMergeSort(int size) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        
        System.out.println("Before: " + (size <= 20 ? Arrays.toString(arr) : "Array of size " + size));
        
        MergeSort.sort(arr, metrics);
        
        System.out.println("After: " + (size <= 20 ? Arrays.toString(arr) : "Sorted array of size " + size));
        System.out.printf("Time: %.3f ms%n", metrics.getExecutionTimeMs());
        System.out.printf("Max Depth: %d%n", metrics.getMaxDepth());
        System.out.printf("Comparisons: %d%n", metrics.getComparisons());
        System.out.printf("Memory Allocations: %d%n", metrics.getMemoryAllocations());
        System.out.printf("Correctly sorted: %b%n", ArrayUtils.isSorted(arr));
    }
    
    private static void testQuickSort(int size) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        
        System.out.println("Before: " + (size <= 20 ? Arrays.toString(arr) : "Array of size " + size));
        
        QuickSort.sort(arr, metrics);
        
        System.out.println("After: " + (size <= 20 ? Arrays.toString(arr) : "Sorted array of size " + size));
        System.out.printf("Time: %.3f ms%n", metrics.getExecutionTimeMs());
        System.out.printf("Max Depth: %d%n", metrics.getMaxDepth());
        System.out.printf("Comparisons: %d%n", metrics.getComparisons());
        System.out.printf("Swaps: %d%n", metrics.getSwaps());
        System.out.printf("Correctly sorted: %b%n", ArrayUtils.isSorted(arr));
        
        // Check depth bound
        double expectedMaxDepth = 2 * Math.floor(Math.log(size) / Math.log(2)) + 10;
        System.out.printf("Depth within expected bound (≤%.0f): %b%n", 
                         expectedMaxDepth, metrics.getMaxDepth() <= expectedMaxDepth);
    }
    
    private static void testSelect(int size) {
        int[] arr = ArrayUtils.generateRandomArray(size);
        int k = size / 2; // Find median
        
        System.out.println("Finding " + k + "th smallest element in array of size " + size);
        
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        int result = DeterministicSelect.select(ArrayUtils.copyArray(arr), k, metrics);
        
        // Verify correctness
        Arrays.sort(arr);
        int expected = arr[k];
        
        System.out.printf("Result: %d, Expected: %d%n", result, expected);
        System.out.printf("Time: %.3f ms%n", metrics.getExecutionTimeMs());
        System.out.printf("Max Depth: %d%n", metrics.getMaxDepth());
        System.out.printf("Comparisons: %d%n", metrics.getComparisons());
        System.out.printf("Correct: %b%n", result == expected);
    }
    
    private static void testClosestPair(int size) {
        Point[] points = ClosestPair.generateRandomPoints(size);
        
        System.out.printf("Finding closest pair among %d points%n", size);
        
        AlgorithmMetrics metrics = new AlgorithmMetrics();
        PointPair result = ClosestPair.findClosestPair(points, metrics);
        
        System.out.printf("Closest pair: %s and %s%n", result.p1, result.p2);
        System.out.printf("Distance: %.6f%n", result.distance);
        System.out.printf("Time: %.3f ms%n", metrics.getExecutionTimeMs());
        System.out.printf("Max Depth: %d%n", metrics.getMaxDepth());
        System.out.printf("Comparisons: %d%n", metrics.getComparisons());
        
        // Verify with brute force for smaller sizes
        if (size <= 2000) {
            AlgorithmMetrics bruteMetrics = new AlgorithmMetrics();
            PointPair bruteResult = ClosestPair.bruteForceClosestPair(points, bruteMetrics);
            System.out.printf("Brute force distance: %.6f%n", bruteResult.distance);
            System.out.printf("Results match: %b%n", 
                             Math.abs(result.distance - bruteResult.distance) < 1e-9);
            System.out.printf("Speedup: %.2fx%n", 
                             bruteMetrics.getExecutionTimeMs() / metrics.getExecutionTimeMs());
        }
    }
}
