// QuickSort.java - Robust quicksort with optimizations
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;
import java.util.Random;

public class QuickSort {
    private static final int INSERTION_SORT_CUTOFF = 16;
    private static final Random random = new Random();
    
    public static void sort(int[] arr, AlgorithmMetrics metrics) {
        if (arr == null || arr.length <= 1) return;
        
        metrics.startTiming();
        ArrayUtils.shuffle(arr); // Randomize to avoid worst case
        sort(arr, 0, arr.length - 1, metrics);
        metrics.endTiming();
    }
    
    private static void sort(int[] arr, int lo, int hi, AlgorithmMetrics metrics) {
        while (hi > lo) {
            if (hi <= lo + INSERTION_SORT_CUTOFF) {
                insertionSort(arr, lo, hi, metrics);
                return;
            }
            
            metrics.enterRecursion();
            
            // Randomized pivot selection
            int pivotIndex = lo + random.nextInt(hi - lo + 1);
            ArrayUtils.swap(arr, lo, pivotIndex, metrics);
            
            int partitionPoint = partition(arr, lo, hi, metrics);
            
            // Recurse on smaller partition, iterate on larger
            if (partitionPoint - lo < hi - partitionPoint) {
                sort(arr, lo, partitionPoint - 1, metrics);
                lo = partitionPoint + 1;
            } else {
                sort(arr, partitionPoint + 1, hi, metrics);
                hi = partitionPoint - 1;
            }
            
            metrics.exitRecursion();
        }
    }
    
    private static int partition(int[] arr, int lo, int hi, AlgorithmMetrics metrics) {
        int pivot = arr[lo];
        int i = lo + 1;
        int j = hi;
        
        while (true) {
            while (i <= j && ArrayUtils.less(arr[i], pivot, metrics)) i++;
            while (j >= i && ArrayUtils.less(pivot, arr[j], metrics)) j--;
            
            if (i >= j) break;
            ArrayUtils.swap(arr, i, j, metrics);
            i++;
            j--;
        }
        
        ArrayUtils.swap(arr, lo, j, metrics);
        return j;
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
