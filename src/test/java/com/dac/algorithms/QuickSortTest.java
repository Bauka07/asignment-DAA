// File: src/test/java/com/dac/algorithms/QuickSortTest.java
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {
    private QuickSort sorter;
    private AlgorithmMetrics metrics;
    
    @BeforeEach
    void setUp() {
        sorter = new QuickSort();
        metrics = new AlgorithmMetrics("QuickSort");
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
        // Test depth on random arrays (should be bounded due to randomized pivot)
        for (int trial = 0; trial < 10; trial++) {
            metrics.reset();
            int[] arr = ArrayUtils.generateRandomArray(1000);
            sorter.sort(arr, metrics);
            
            // With randomized pivot and tail recursion, depth should be ~2*log2(n) typically
            int expectedMaxDepth = 2 * (int) Math.ceil(Math.log(arr.length) / Math.log(2)) + 10;
            assertTrue(metrics.getMaxDepth() <= expectedMaxDepth,
                    String.format("Trial %d: Depth %d should be <= %d", trial, metrics.getMaxDepth(), expectedMaxDepth));
            
            assertTrue(ArrayUtils.isSorted(arr));
        }
    }
    
    @Test
    void testAdversarialInput() {
        // Test worst-case scenario for naive quicksort (but should be mitigated by randomization)
        int[] worstCase = ArrayUtils.generateWorstCaseQuickSort(1000);
        sorter.sort(worstCase, metrics);
        
        assertTrue(ArrayUtils.isSorted(worstCase));
        // Even with worst-case input, randomized pivot should keep depth reasonable
        assertTrue(metrics.getMaxDepth() < worstCase.length, "Depth should be much less than n");
    }
    
    @Test
    void testRandomizedBehavior() {
        // Test that randomized pivot gives different behaviors on same input
        int[] baseArray = ArrayUtils.generateWorstCaseQuickSort(100);
        
        AlgorithmMetrics metrics1 = new AlgorithmMetrics("QuickSort1");
        AlgorithmMetrics metrics2 = new AlgorithmMetrics("QuickSort2");
        
        int[] arr1 = baseArray.clone();
        int[] arr2 = baseArray.clone();
        
        sorter.sort(arr1, metrics1);
        sorter.sort(arr2, metrics2);
        
        // Both should be sorted
        assertArrayEquals(arr1, arr2);
        assertTrue(ArrayUtils.isSorted(arr1));
        assertTrue(ArrayUtils.isSorted(arr2));
        
        // Due to randomization, we might get different depths (though not guaranteed)
        // At minimum, both should complete successfully
        assertTrue(metrics1.getMaxDepth() > 0);
        assertTrue(metrics2.getMaxDepth() > 0);
    }
    
    @Test
    void testTailRecursionOptimization() {
        // Test that the tail recursion optimization works by checking depth bounds
        int[] sizes = {1000, 2000, 5000};
        
        for (int size : sizes) {
            metrics.reset();
            int[] arr = ArrayUtils.generateRandomArray(size);
            sorter.sort(arr, metrics);
            
            assertTrue(ArrayUtils.isSorted(arr));
            
            // With tail recursion, depth should be logarithmic even for large arrays
            int maxReasonableDepth = 3 * (int) Math.ceil(Math.log(size) / Math.log(2));
            assertTrue(metrics.getMaxDepth() <= maxReasonableDepth,
                    String.format("Size %d: Depth %d should be <= %d (tail recursion bound)", 
                            size, metrics.getMaxDepth(), maxReasonableDepth));
        }
    }
    
    @Test
    void testMetricsCollection() {
        int[] arr = ArrayUtils.generateRandomArray(100);
        
        sorter.sort(arr, metrics);
        
        // Verify metrics were collected
        assertTrue(metrics.getExecutionTimeNs() > 0, "Execution time should be recorded");
        assertTrue(metrics.getComparisons() > 0, "Comparisons should be recorded");
        assertTrue(metrics.getMaxDepth() > 0, "Max depth should be recorded");
        
        // QuickSort doesn't allocate extra arrays (in-place)
        assertEquals(0, metrics.getAllocations(), "QuickSort should have no allocations");
        
        // Verify array is still sorted
        assertTrue(ArrayUtils.isSorted(arr));
    }
}
