// File: src/test/java/com/dac/benchmarks/SortingBenchmark.java
package com.dac.benchmarks;

import com.dac.algorithms.MergeSort;
import com.dac.algorithms.QuickSort;
import com.dac.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class SortingBenchmark {
    
    @Param({"1000", "5000", "10000", "25000", "50000"})
    private int size;
    
    private int[] randomArray;
    private int[] sortedArray;
    private int[] reverseSortedArray;
    private int[] duplicateArray;
    
    private MergeSort mergeSort;
    private QuickSort quickSort;
    
    @Setup
    public void setup() {
        randomArray = ArrayUtils.generateRandomArray(size);
        
        sortedArray = new int[size];
        for (int i = 0; i < size; i++) sortedArray[i] = i;
        
        reverseSortedArray = ArrayUtils.generateReverseSortedArray(size);
        duplicateArray = ArrayUtils.generateDuplicateArray(size, 100);
        
        mergeSort = new MergeSort();
        quickSort = new QuickSort();
    }
    
    // MergeSort benchmarks
    @Benchmark
    public void benchmarkMergeSortRandom() {
        int[] arr = randomArray.clone();
        mergeSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkMergeSortSorted() {
        int[] arr = sortedArray.clone();
        mergeSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkMergeSortReverse() {
        int[] arr = reverseSortedArray.clone();
        mergeSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkMergeSortDuplicates() {
        int[] arr = duplicateArray.clone();
        mergeSort.sort(arr);
    }
    
    // QuickSort benchmarks
    @Benchmark
    public void benchmarkQuickSortRandom() {
        int[] arr = randomArray.clone();
        quickSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkQuickSortSorted() {
        int[] arr = sortedArray.clone();
        quickSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkQuickSortReverse() {
        int[] arr = reverseSortedArray.clone();
        quickSort.sort(arr);
    }
    
    @Benchmark
    public void benchmarkQuickSortDuplicates() {
        int[] arr = duplicateArray.clone();
        quickSort.sort(arr);
    }
    
    // Built-in Java sort for comparison
    @Benchmark
    public void benchmarkArraysSortRandom() {
        int[] arr = randomArray.clone();
        Arrays.sort(arr);
    }
    
    @Benchmark
    public void benchmarkArraysSortSorted() {
        int[] arr = sortedArray.clone();
        Arrays.sort(arr);
    }
    
    @Benchmark
    public void benchmarkArraysSortReverse() {
        int[] arr = reverseSortedArray.clone();
        Arrays.sort(arr);
    }
    
    @Benchmark
    public void benchmarkArraysSortDuplicates() {
        int[] arr = duplicateArray.clone();
        Arrays.sort(arr);
    }
    
    // Head-to-head comparison
    @Benchmark
    public void benchmarkMergeVsQuickRandom() {
        int[] arr1 = randomArray.clone();
        int[] arr2 = randomArray.clone();
        
        mergeSort.sort(arr1);
        quickSort.sort(arr2);
    }
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SortingBenchmark.class.getSimpleName())
                .build();
        
        new Runner(opt).run();
    }
}
