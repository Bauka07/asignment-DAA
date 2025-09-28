// File: src/test/java/com/dac/benchmarks/SelectBenchmark.java
package com.dac.benchmarks;

import com.dac.algorithms.DeterministicSelect;
import com.dac.metrics.AlgorithmMetrics;
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
public class SelectBenchmark {
    
    @Param({"1000", "5000", "10000", "20000", "50000"})
    private int size;
    
    private int[] randomArray;
    private DeterministicSelect selector;
    private int k;
    
    @Setup
    public void setup() {
        randomArray = ArrayUtils.generateRandomArray(size);
        selector = new DeterministicSelect();
        k = size / 2; // Select median
    }
    
    @Benchmark
    public int benchmarkDeterministicSelect() {
        int[] arr = randomArray.clone();
        return selector.select(arr, k);
    }
    
    @Benchmark
    public int benchmarkSelectViaSort() {
        int[] arr = randomArray.clone();
        Arrays.sort(arr);
        return arr[k];
    }
    
    @Benchmark
    public int benchmarkSelectDifferentK() {
        int[] arr = randomArray.clone();
        // Test different positions: min, 25%, median, 75%, max
        int[] positions = {0, size/4, size/2, 3*size/4, size-1};
        int pos = positions[arr.length % positions.length];
        return selector.select(arr, pos);
    }
    
    // Specialized benchmarks for different input types
    @Benchmark
    public int benchmarkSelectWorstCase() {
        int[] arr = ArrayUtils.generateWorstCaseQuickSort(size);
        return selector.select(arr, k);
    }
    
    @Benchmark
    public int benchmarkSelectDuplicates() {
        int[] arr = ArrayUtils.generateDuplicateArray(size, 100); // Many duplicates
        return selector.select(arr, k);
    }
    
    @Benchmark
    public int benchmarkSelectSorted() {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = i;
        return selector.select(arr, k);
    }
    
    @Benchmark
    public int benchmarkSelectReverseSorted() {
        int[] arr = ArrayUtils.generateReverseSortedArray(size);
        return selector.select(arr, k);
    }
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SelectBenchmark.class.getSimpleName())
                .build();
        
        new Runner(opt).run();
    }
}
