// ArrayUtils.java - Utility functions
package com.dac.util;

import com.dac.metrics.AlgorithmMetrics;
import java.util.Random;

public class ArrayUtils {
    private static final Random random = new Random();
    
    public static void swap(int[] arr, int i, int j, AlgorithmMetrics metrics) {
        if (metrics != null) metrics.incrementSwaps();
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    public static boolean less(int a, int b, AlgorithmMetrics metrics) {
        if (metrics != null) metrics.incrementComparisons();
        return a < b;
    }
    
    public static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swap(arr, i, j, null);
        }
    }
    
    public static int[] generateRandomArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = random.nextInt(n * 10);
        }
        return arr;
    }
    
    public static int[] generateSortedArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }
    
    public static int[] generateReverseSortedArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }
    
    public static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i-1]) return false;
        }
        return true;
    }
    
    public static int[] copyArray(int[] arr) {
        int[] copy = new int[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }
}
