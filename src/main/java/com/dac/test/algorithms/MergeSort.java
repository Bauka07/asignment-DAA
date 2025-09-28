// MergeSort.java - Optimized merge sort implementation
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;

public class MergeSort {
    private static final int INSERTION_SORT_CUTOFF = 16;
    
    public static void sort(int[] arr, AlgorithmMetrics metrics) {
        if (arr == null || arr.length <= 1) return;
        
        metrics.startTiming();
        int[] aux = new int[arr.length];
        metrics.incrementAllocations(arr.length);
        sort(arr, aux, 0, arr.length - 1, metrics);
        metrics.endTiming();
    }
    
    private static void sort(int[] arr, int[] aux, int lo, int hi, AlgorithmMetrics metrics) {
        metrics.enterRecursion();
        
        if (hi <= lo + INSERTION_SORT_CUTOFF) {
            insertionSort(arr, lo, hi, metrics);
            metrics.exitRecursion();
            return;
        }
        
        int mid = lo + (hi - lo) / 2;
        sort(arr, aux, lo, mid, metrics);
        sort(arr, aux, mid + 1, hi, metrics);
        
        // Skip merge if already sorted
        if (!ArrayUtils.less(arr[mid + 1], arr[mid], metrics)) {
            metrics.exitRecursion();
            return;
        }
        
        merge(arr, aux, lo, mid, hi, metrics);
        metrics.exitRecursion();
    }
    
    private static void merge(int[] arr, int[] aux, int lo, int mid, int hi, AlgorithmMetrics metrics) {
        // Copy to auxiliary array
        System.arraycopy(arr, lo, aux, lo, hi - lo + 1);
        
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                arr[k] = aux[j++];
            } else if (j > hi) {
                arr[k] = aux[i++];
            } else if (ArrayUtils.less(aux[j], aux[i], metrics)) {
                arr[k] = aux[j++];
            } else {
                arr[k] = aux[i++];
            }
        }
    }
    
    private static void insertionSort(int[] arr, int lo, int hi, AlgorithmMetrics metrics) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= lo && ArrayUtils.less(key, arr[j], metrics)) {
                arr[j + 1] = arr[j];
                metrics.incrementSwaps();
                j--;
            }
            arr[j + 1] = key;
        }
    }
}
