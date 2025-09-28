// File: src/main/java/com/dac/metrics/CSVWriter.java
package com.dac.metrics;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVWriter {
    private static final String RESULTS_DIR = "results";
    private static final String CSV_HEADER = "Algorithm,InputSize,Time_ns,Comparisons,Allocations,MaxDepth";
    
    public CSVWriter() {
        try {
            Files.createDirectories(Paths.get(RESULTS_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create results directory: " + e.getMessage());
        }
    }
    
    public void writeMetrics(String filename, List<AlgorithmMetrics> metrics, List<Integer> inputSizes) {
        String fullPath = RESULTS_DIR + "/" + filename;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
            writer.println(CSV_HEADER);
            
            if (metrics.size() != inputSizes.size()) {
                throw new IllegalArgumentException("Metrics and input sizes must have same length");
            }
            
            for (int i = 0; i < metrics.size(); i++) {
                writer.println(metrics.get(i).toCsvRow(inputSizes.get(i)));
            }
            
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }
    
    public void writeMetricsWithTimestamp(String algorithmName, List<AlgorithmMetrics> metrics, List<Integer> inputSizes) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("%s_%s.csv", algorithmName, timestamp);
        writeMetrics(filename, metrics, inputSizes);
    }
    
    public void appendMetrics(String filename, AlgorithmMetrics metrics, int inputSize) {
        String fullPath = RESULTS_DIR + "/" + filename;
        boolean fileExists = Files.exists(Paths.get(fullPath));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath, true))) {
            if (!fileExists) {
                writer.println(CSV_HEADER);
            }
            writer.println(metrics.toCsvRow(inputSize));
            
        } catch (IOException e) {
            System.err.println("Failed to append to CSV file: " + e.getMessage());
        }
    }
    
    public void writeBenchmarkResults(String filename, MetricsCollector collector) {
        String fullPath = RESULTS_DIR + "/" + filename;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
            writer.println("Algorithm,AvgTime_ns,AvgComparisons,AvgMaxDepth,Runs");
            
            for (String algorithm : collector.getAllHistory().keySet()) {
                List<AlgorithmMetrics> history = collector.getHistory(algorithm);
                if (!history.isEmpty()) {
                    writer.printf("%s,%.2f,%.2f,%.2f,%d%n",
                            algorithm,
                            collector.getAverageExecutionTime(algorithm),
                            collector.getAverageComparisons(algorithm),
                            collector.getAverageMaxDepth(algorithm),
                            history.size()
                    );
                }
            }
            
        } catch (IOException e) {
            System.err.println("Failed to write benchmark results: " + e.getMessage());
        }
    }
}
