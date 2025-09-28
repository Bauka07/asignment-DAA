// File: src/test/java/com/dac/benchmarks/ClosestPairBenchmark.java
package com.dac.benchmarks;

import com.dac.algorithms.ClosestPair;
import com.dac.algorithms.Point;
import com.dac.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
public class ClosestPairBenchmark {
    
    @Param({"100", "500", "1000", "2000", "5000", "10000"})
    private int size;
    
    private Point[] randomPoints;
    private Point[] clusteredPoints;
    private Point[] collinearPoints;
    
    private ClosestPair closestPair;
    
    @Setup
    public void setup() {
        randomPoints = ArrayUtils.generateRandomPoints(size, 1000.0);
        clusteredPoints = ArrayUtils.generateRandomPoints(size, 100.0); // More clustered
        collinearPoints = ArrayUtils.generateCollinearPoints(size);
        
        closestPair = new ClosestPair();
    }
    
    @Benchmark
    public double benchmarkClosestPairRandom() {
        Point[] points = randomPoints.clone();
        return closestPair.findClosestDistance(points);
    }
    
    @Benchmark
    public double benchmarkClosestPairClustered() {
        Point[] points = clusteredPoints.clone();
        return closestPair.findClosestDistance(points);
    }
    
    @Benchmark
    public double benchmarkClosestPairCollinear() {
        Point[] points = collinearPoints.clone();
        return closestPair.findClosestDistance(points);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double benchmarkClosestPairWithPair() {
        Point[] points = randomPoints.clone();
        return closestPair.findClosestPair(points).getDistance();
    }
    
    // Compare with brute force for small sizes only
    @Benchmark
    public double benchmarkBruteForceSmall() {
        if (size <= 2000) { // Only run brute force for small sizes
            Point[] points = randomPoints.clone();
            return closestPair.bruteForce(points);
        }
        return 0.0; // Skip for large sizes
    }
    
    // Test different coordinate ranges
    @Benchmark
    public double benchmarkLargeCoordinates() {
        Point[] points = ArrayUtils.generateRandomPoints(size, 100000.0);
        return closestPair.findClosestDistance(points);
    }
    
    @Benchmark
    public double benchmarkSmallCoordinates() {
        Point[] points = ArrayUtils.generateRandomPoints(size, 10.0);
        return closestPair.findClosestDistance(points);
    }
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ClosestPairBenchmark.class.getSimpleName())
                .build();
        
        new Runner(opt).run();
    }
}
