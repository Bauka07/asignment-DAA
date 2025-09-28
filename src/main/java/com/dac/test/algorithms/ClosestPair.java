// ClosestPair.java - Divide and conquer closest pair algorithm
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

class PointPair {
    final Point p1, p2;
    final double distance;
    
    PointPair(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = p1.distanceTo(p2);
    }
}

public class ClosestPair {
    public static PointPair findClosestPair(Point[] points, AlgorithmMetrics metrics) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }
        
        metrics.startTiming();
        
        // Sort by x-coordinate
        Point[] pointsByX = points.clone();
        metrics.incrementAllocations(points.length);
        Arrays.sort(pointsByX, Comparator.comparingDouble(p -> p.x));
        
        // Create array sorted by y-coordinate
        Point[] pointsByY = points.clone();
        metrics.incrementAllocations(points.length);
        Arrays.sort(pointsByY, Comparator.comparingDouble(p -> p.y));
        
        PointPair result = closestPairRec(pointsByX, pointsByY, 0, points.length - 1, metrics);
        metrics.endTiming();
        return result;
    }
    
    private static PointPair closestPairRec(Point[] px, Point[] py, int lo, int hi, 
                                          AlgorithmMetrics metrics) {
        metrics.enterRecursion();
        
        int n = hi - lo + 1;
        
        // Base case: brute force for small arrays
        if (n <= 3) {
            PointPair result = bruteForce(px, lo, hi, metrics);
            metrics.exitRecursion();
            return result;
        }
        
        // Divide
        int mid = lo + (hi - lo) / 2;
        Point midPoint = px[mid];
        
        // Create left and right y-sorted arrays
        Point[] pyl = new Point[mid - lo + 1];
        Point[] pyr = new Point[hi - mid];
        metrics.incrementAllocations(pyl.length + pyr.length);
        
        int li = 0, ri = 0;
        for (Point p : py) {
            if (p.x <= midPoint.x && li < pyl.length) {
                pyl[li++] = p;
            } else if (ri < pyr.length) {
                pyr[ri++] = p;
            }
        }
        
        // Conquer
        PointPair leftClosest = closestPairRec(px, pyl, lo, mid, metrics);
        PointPair rightClosest = closestPairRec(px, pyr, mid + 1, hi, metrics);
        
        // Find minimum of the two
        PointPair minPair = (leftClosest.distance <= rightClosest.distance) ? 
                           leftClosest : rightClosest;
        double minDist = minPair.distance;
        
        // Check strip around the dividing line
        Point[] strip = new Point[n];
        metrics.incrementAllocations(n);
        int stripSize = 0;
        
        for (Point p : py) {
            if (Math.abs(p.x - midPoint.x) < minDist) {
                strip[stripSize++] = p;
            }
        }
        
        // Find closest points in strip
        PointPair stripClosest = closestInStrip(strip, stripSize, minDist, metrics);
        
        PointPair result = (stripClosest != null && stripClosest.distance < minDist) ? 
                          stripClosest : minPair;
        
        metrics.exitRecursion();
        return result;
    }
    
    private static PointPair bruteForce(Point[] points, int lo, int hi, AlgorithmMetrics metrics) {
        double minDist = Double.POSITIVE_INFINITY;
        PointPair closest = null;
        
        for (int i = lo; i <= hi; i++) {
            for (int j = i + 1; j <= hi; j++) {
                metrics.incrementComparisons();
                double dist = points[i].distanceTo(points[j]);
                if (dist < minDist) {
                    minDist = dist;
                    closest = new PointPair(points[i], points[j]);
                }
            }
        }
        
        return closest;
    }
    
    private static PointPair closestInStrip(Point[] strip, int size, double minDist, 
                                          AlgorithmMetrics metrics) {
        PointPair closest = null;
        
        for (int i = 0; i < size; i++) {
            // Check at most 7 neighbors (theoretical bound)
            for (int j = i + 1; j < size && j < i + 8; j++) {
                if (strip[j].y - strip[i].y >= minDist) {
                    break; // No more candidates
                }
                
                metrics.incrementComparisons();
                double dist = strip[i].distanceTo(strip[j]);
                if (dist < minDist) {
                    minDist = dist;
                    closest = new PointPair(strip[i], strip[j]);
                }
            }
        }
        
        return closest;
    }
    
    // Generate random points for testing
    public static Point[] generateRandomPoints(int n) {
        Random random = new Random();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(random.nextDouble() * 1000, random.nextDouble() * 1000);
        }
        return points;
    }
    
    // Brute force O(n²) for validation
    public static PointPair bruteForceClosestPair(Point[] points, AlgorithmMetrics metrics) {
        if (points.length < 2) return null;
        
        metrics.startTiming();
        PointPair result = bruteForce(points, 0, points.length - 1, metrics);
        metrics.endTiming();
        return result;
    }
}
