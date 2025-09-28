// File: src/test/java/com/dac/algorithms/ClosestPairTest.java
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ClosestPairTest {
    private ClosestPair cp;
    private AlgorithmMetrics metrics;
    
    @BeforeEach
    void setUp() {
        cp = new ClosestPair();
        metrics = new AlgorithmMetrics("ClosestPair");
    }
    
    @Test
    void testCorrectness() {
        // Test against brute force for small inputs
        for (int trial = 0; trial < 50; trial++) {
            Point[] points = ArrayUtils.generateRandomPoints(20);
            metrics.reset();
            
            double fastResult = cp.findClosestDistance(points, metrics);
            double bruteResult = cp.bruteForce(points);
            
            assertEquals(bruteResult, fastResult, 1e-9, 
                    String.format("Trial %d: Fast=%.9f, Brute=%.9f", trial, fastResult, bruteResult));
        }
    }
    
    @Test
    void testSpecialCases() {
        // Two points
        Point[] two = {new Point(0, 0), new Point(3, 4)};
        assertEquals(5.0, cp.findClosestDistance(two, metrics), 1e-9);
        
        // Three points forming a triangle
        Point[] triangle = {
            new Point(0, 0),
            new Point(1, 0), 
            new Point(0.5, Math.sqrt(3)/2)
        };
        assertEquals(1.0, cp.findClosestDistance(triangle, metrics), 1e-9);
        
        // Collinear points
        Point[] collinear = ArrayUtils.generateCollinearPoints(10);
        double expected = Math.sqrt(2); // Distance between consecutive points on y=x line
        assertEquals(expected, cp.findClosestDistance(collinear, metrics), 1e-9);
        
        // Points with same coordinates
        Point[] duplicate = {
            new Point(1, 1),
            new Point(2, 2),
            new Point(1, 1), // Duplicate
            new Point(3, 3)
        };
        assertEquals(0.0, cp.findClosestDistance(duplicate, metrics), 1e-9);
    }
    
    @Test
    void testReturnsPair() {
        Point[] points = {
            new Point(0, 0),
            new Point(1, 0),
            new Point(0, 1),
            new Point(1, 1)
        };
        
        PointPair result = cp.findClosestPair(points, metrics);
        assertNotNull(result);
        assertEquals(1.0, result.getDistance(), 1e-9);
        
        // Verify the pair consists of adjacent points
        assertTrue(
            (result.p1.equals(new Point(0, 0)) && result.p2.equals(new Point(1, 0))) ||
            (result.p1.equals(new Point(1, 0)) && result.p2.equals(new Point(0, 0))) ||
            (result.p1.equals(new Point(0, 0)) && result.p2.equals(new Point(0, 1))) ||
            (result.p1.equals(new Point(0, 1)) && result.p2.equals(new Point(0, 0))) ||
            (result.p1.equals(new Point(1, 0)) && result.p2.equals(new Point(1, 1))) ||
            (result.p1.equals(new Point(1, 1)) && result.p2.equals(new Point(1, 0))) ||
            (result.p1.equals(new Point(0, 1)) && result.p2.equals(new Point(1, 1))) ||
            (result.p1.equals(new Point(1, 1)) && result.p2.equals(new Point(0, 1)))
        );
    }
    
    @Test
    void testLargeInput() {
        // Test performance on larger input
        int[] sizes = {1000, 2000, 5000};
        
        for (int size : sizes) {
            metrics.reset();
            Point[] points = ArrayUtils.generateRandomPoints(size);
            
            long startTime = System.nanoTime();
            double result = cp.findClosestDistance(points, metrics);
            long endTime = System.nanoTime();
            
            assertTrue(result > 0, "Should find a valid distance");
            assertTrue(result < Double.MAX_VALUE, "Should find a finite distance");
            
            double timeSeconds = (endTime - startTime) / 1_000_000_000.0;
            assertTrue(timeSeconds < 5.0, 
                    String.format("Size %d took %.3f seconds, should complete under 5s", size, timeSeconds));
            
            // Verify depth is logarithmic
            int expectedMaxDepth = (int) Math.ceil(Math.log(size) / Math.log(2)) + 5;
            assertTrue(metrics.getMaxDepth() <= expectedMaxDepth,
                    String.format("Size %d: Depth %d should be <= %d", size, metrics.getMaxDepth(), expectedMaxDepth));
        }
    }
    
    @Test
    void testDifferentDistributions() {
        int size = 500;
        
        // Random uniform distribution
        Point[] uniform = ArrayUtils.generateRandomPoints(size);
        double uniformResult = cp.findClosestDistance(uniform, metrics);
        assertTrue(uniformResult > 0);
        
        // Points in a small area (should have closer pairs)
        Point[] clustered = ArrayUtils.generateRandomPoints(size, 10.0);
        double clusteredResult = cp.findClosestDistance(clustered, metrics);
        assertTrue(clusteredResult > 0);
        
        // Generally, clustered points should have smaller minimum distances
        // (though not guaranteed due to randomness)
    }
    
    @Test
    void testStripOptimization() {
        // Test case where closest pair is in the strip between two halves
        Point[] points = {
            new Point(0, 0),   // Left side
            new Point(1, 0),
            new Point(4.9, 0), // Right side of left half
            new Point(5.1, 0), // Left side of right half
            new Point(10, 0),  // Right side
            new Point(11, 0)
        };
        
        double result = cp.findClosestDistance(points, metrics);
        assertEquals(0.2, result, 1e-9); // Distance between (4.9,0) and (5.1,0)
    }
    
    @Test
    void testComparisonWithBruteForce() {
        // Compare performance: O(n log n) vs O(n²)
        int[] sizes = {100, 500, 1000};
        
        for (int size : sizes) {
            Point[] points = ArrayUtils.generateRandomPoints(size);
            
            // Fast algorithm
            metrics.reset();
            long fastStart = System.nanoTime();
            double fastResult = cp.findClosestDistance(points, metrics);
            long fastEnd = System.nanoTime();
            
            // Brute force (only for smaller sizes)
            if (size <= 1000) {
                long bruteStart = System.nanoTime();
                double bruteResult = cp.bruteForce(points);
                long bruteEnd = System.nanoTime();
                
                assertEquals(bruteResult, fastResult, 1e-9, "Results should match");
                
                double fastTime = (fastEnd - fastStart) / 1_000_000.0;
                double bruteTime = (bruteEnd - bruteStart) / 1_000_000.0;
                
                System.out.printf("Size %d: Fast %.2fms, Brute %.2fms, Speedup %.2fx%n", 
                        size, fastTime, bruteTime, bruteTime / fastTime);
                
                // For larger sizes, fast should be significantly faster
                if (size >= 500) {
                    assertTrue(fastTime < bruteTime, "Fast algorithm should be faster for large inputs");
                }
            }
        }
    }
    
    @Test
    void testMetricsCollection() {
        Point[] points = ArrayUtils.generateRandomPoints(1000);
        
        double result = cp.findClosestDistance(points, metrics);
        
        // Verify metrics were collected
        assertTrue(metrics.getExecutionTimeNs() > 0, "Execution time should be recorded");
        assertTrue(metrics.getComparisons() > 0, "Comparisons should be recorded");
        assertTrue(metrics.getAllocations() > 0, "Allocations should be recorded");
        assertTrue(metrics.getMaxDepth() > 0, "Max depth should be recorded");
        
        // Verify result is reasonable
        assertTrue(result > 0, "Should find a positive distance");
        assertTrue(result < 1000 * Math.sqrt(2), "Distance should be reasonable for 1000x1000 area");
    }
}
