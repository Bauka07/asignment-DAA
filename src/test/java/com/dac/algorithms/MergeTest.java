/ File: src/test/java/com/dac/algorithms/MergeSortTest.java
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortTest {
    private MergeSort sorter;
    private AlgorithmMetrics metrics;
    
    @BeforeEach
    void setUp() {
        sorter = new MergeSort();
        metrics = new AlgorithmMetrics("MergeSort");
    }
    
    @Test
    void testCorrectness() {
        // Test random arrays
        for (int i = 0; i < 100; i++) {
            int[] arr = ArrayUtils.generateRandomArray(100);
            int[] expected = arr.clone();
            java.util.Arrays.sort(expected);
            
            sorter.sort(arr, metrics);
            assertArrayEquals(expected, arr, "Random array " + i + " not sorted correctly");
            metrics.reset();
        }
    }
    
    @Test
    void testEdgeCases() {
        // Empty array
        int[] empty = {};
        sorter.sort(empty, metrics);
        assertEquals(0, empty.length);
        
        // Single element
        int[] single = {42};
        sorter.sort(single, metrics);
        assertArrayEquals(new int[]{42}, single);
        
        // Two elements
        int[] two = {2, 1};
        sorter.sort(two, metrics);
        assertArrayEquals(new int[]{1, 2}, two);
        
        // Already sorted
        int[] sorted = {1, 2, 3, 4, 5};
        sorter.sort(sorted, metrics);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, sorted);
        
        // Reverse sorted
        int[] reverse = {5, 4, 3, 2, 1};
        sorter.sort(reverse, metrics);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, reverse);
        
        // Duplicates
        int[] duplicates = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        sorter.sort(duplicates, metrics);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, duplicates);
        
        // All same elements
        int[] same = {7, 7, 7, 7, 7};
        sorter.sort(same, metrics);
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, same);
    }
    
    @Test
    void testDepthBounds() {
        int[] sizes = {100, 500, 1000, 2000, 5000};
        
        for (int size : sizes) {
            metrics.reset();
            int[] arr = ArrayUtils.generateRandomArray(size);
            sorter.sort(arr, metrics);
            
            // MergeSort depth should be approximately log2(n) + buffer for cutoff
            int expectedMaxDepth = (int) Math.ceil(Math.log(size) / Math.log(2)) + 5;
            assertTrue(metrics.getMaxDepth() <= expectedMaxDepth,
                    String.format("Size %d: Depth %d should be <= %d", size, metrics.getMaxDepth(), expectedMaxDepth));
        }
    }
    
    @Test
    void testStability() {
        // Test that equal elements maintain relative order
        int[] arr = {3, 1, 3, 2, 3};
        // Create array of indices to track original positions
        Integer[] indices = {0, 1, 2, 3, 4};
        int[] values = arr.clone();
        
        sorter.sort(arr, metrics);
        
        // Verify sorting worked
        assertTrue(ArrayUtils.isSorted(arr));
        
        // For a more thorough stability test with custom objects would be needed
        // This is a basic sanity check
        assertArrayEquals(new int[]{1, 2, 3, 3, 3}, arr);
    }
    
    @Test
    void testMetricsCollection() {
        int[] arr = ArrayUtils.generateRandomArray(100);
        
        sorter.sort(arr, metrics);
        
        // Verify metrics were collected
        assertTrue(metrics.getExecutionTimeNs() > 0, "Execution time should be recorded");
        assertTrue(metrics.getComparisons() > 0, "Comparisons should be recorded");
        assertTrue(metrics.getAllocations() > 0, "Allocations should be recorded");
        assertTrue(metrics.getMaxDepth() > 0, "Max depth should be recorded");
        
        // Verify array is still sorted
        assertTrue(ArrayUtils.isSorted(arr));
    }
    
    @Test
    void testLargeArrays() {
        int[] sizes = {10000, 50000};
        
        for (int size : sizes) {
            metrics.reset();
            int[] arr = ArrayUtils.generateRandomArray(size);
            
            long startTime = System.nanoTime();
            sorter.sort(arr, metrics);
            long endTime = System.nanoTime();
            
            assertTrue(ArrayUtils.isSorted(arr));
            assertTrue((endTime - startTime) < 10_000_000_000L, // 10 seconds
                    "Should complete large array in reasonable time");
        }
    }
}
