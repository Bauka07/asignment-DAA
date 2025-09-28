package com.dac.algorithms;

/**
 * Represents a pair of points and their distance.
 */
public class PointPair {
    public final Point p1, p2;
    public final double distance;
    
    public PointPair(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = p1.distanceTo(p2);
    }
    
    @Override
    public String toString() {
        return String.format("PointPair{%s, %s, distance=%.6f}", p1, p2, distance);
    }
}
