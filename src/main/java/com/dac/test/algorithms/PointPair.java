// File: src/main/java/com/dac/algorithms/PointPair.java
package com.dac.algorithms;

public class PointPair {
    public final Point p1, p2;
    public final double distance;
    
    public PointPair(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = p1.distanceTo(p2);
    }
    
    public double getDistance() {
        return distance;
    }
    
    @Override
    public String toString() {
        return String.format("PointPair{%s <-> %s, distance=%.6f}", p1, p2, distance);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PointPair pair = (PointPair) obj;
        return ((p1.equals(pair.p1) && p2.equals(pair.p2)) || 
                (p1.equals(pair.p2) && p2.equals(pair.p1)));
    }
}
