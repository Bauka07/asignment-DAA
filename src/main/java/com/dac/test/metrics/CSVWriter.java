// CSVWriter.java - Metrics output to CSV
package com.dac.metrics;

import java.io.*;
import java.util.*;

public class CSVWriter {
    private final String filename;
    private final List<String[]> data;
    private String[] headers;
    
    public CSVWriter(String filename) {
        this.filename = filename;
        this.data = new ArrayList<>();
    }
    
    public void setHeaders(String... headers) {
        this.headers = headers;
    }
    
    public void addRow(String... row) {
        data.add(row);
    }
    
    public void writeToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write headers
            if (headers != null) {
                writer.println(String.join(",", headers));
            }
            
            // Write data
            for (String[] row : data) {
                writer.println(String.join(",", row));
            }
        }
    }
    
    public void clear() {
        data.clear();
    }
}
