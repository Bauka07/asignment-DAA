// File: src/test/java/com/dac/algorithms/DeterministicSelectTest.java
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class DeterministicSelectTest {
    private DeterministicSelect selector;
    private AlgorithmMetrics metrics;
    
    @BeforeEach
    void setUp() {
        selector = new DeterministicSelect();
        metrics = new AlgorithmMetrics("DeterministicSelect");
    }
    
    @Test
    void testCorrectness() {
        // Test against Arrays.sort for 100 random trials
        for (int trial = 0; trial < 100; trial++) {
            int size = 50 + trial % 200; // Varying sizes from 50 to 250
            int[] arr = ArrayUtils.generateRandomArray(size);
            int[] sorted = arr.clone();
            java.util.Arrays.sort(sorted);
            
            // Test several k values for each array
            for (int k = 0; k < Math.min(size, 10); k++) {
                metrics.reset();
                int[] testArr = arr.clone();
                int result = selector.select(testArr, k, metrics);
                assertEquals(sorted[k], result, 
                        String.format("Trial %d, k=%d: Expected %d, got %d", trial, k, sorted[k], result));
            }
            
            // Test edge positions
            int[] testArr1 = arr.clone();
            int[] testArr2 = arr.clone();
            assertEquals(sorted[0], selector.select(testArr1, 0, metrics)); // minimum
            assertEquals(sorted[size-1], selector.select(testArr2, size-1, metrics)); // maximum
        }
    }
    
    @Test
    void testEdgeCases() {
