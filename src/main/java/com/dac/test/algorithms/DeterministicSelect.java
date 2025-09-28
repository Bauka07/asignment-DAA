// DeterministicSelect.java - Median of Medians algorithm
package com.dac.algorithms;

import com.dac.metrics.AlgorithmMetrics;
import com.dac.util.ArrayUtils;

public class DeterministicSelect {
    private static final int GROUP_SIZE = 5;
    
    public static int select(int[] arr, int k, AlgorithmMetrics metrics) {
        if (arr == null || k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("Invalid array or k");
        }
        
        metrics.startTiming();
        int result = select(arr, 0, arr.length - 1, k, metrics);
        metrics.endTiming();
        return result;
    }
    
    private static int select(int[] arr, int lo, int hi, int k, AlgorithmMetrics metrics) {
        metrics.enterRecursion();
        
        int n = hi - lo + 1;
        
        // Base case
        if (n <= GROUP_SIZE) {
            insertionSort(arr, lo, hi, metrics);
            metrics.exitRecursion();
            return arr[lo + k];
        }
        
        // Find median of medians
        int numGroups = (n + GROUP_SIZE - 1) / GROUP_SIZE;
        int[] medians = new int[numGroups];
        metrics.incrementAllocations(numGroups);
        
        for (int i = 0; i < numGroups; i++) {
            int groupStart = lo + i * GROUP_SIZE;
            int groupEnd = Math.min(groupStart + GROUP_SIZE - 1, hi);
            insertionSort(arr, groupStart, groupEnd, metrics);
            medians[i] = arr[groupStart + (groupEnd - groupStart) / 2];
        }
        
        // Recursively find median of medians
        int pivotValue = select(medians, 0, numGroups - 1, numGroups / 2, metrics);
        
        // Find pivot position in original array
        int pivotIndex = -1;
        for (int i = lo; i <= hi; i++) {
            if (arr[i] == pivotValue) {
                pivotIndex = i;
                break;
            }
        }
        
        // Move pivot to start
        ArrayUtils.swap(arr, lo, pivotIndex, metrics);
        
        // Partition around pivot
        int partitionPoint = partition(arr, lo, hi, metrics);
        int rank = partitionPoint - lo;
        
        if (rank == k) {
            metrics.exitRecursion();
            return arr[partitionPoint];
        } else if (k < rank) {
            int result = select(arr, lo, partitionPoint - 1, k, metrics);
            metrics.exitRecursion();
            return result;
        } else {
            int result = select(arr, partitionPoint + 1, hi, k - rank - 1, metrics);
            metrics.exitRecursion();
            return result;
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
