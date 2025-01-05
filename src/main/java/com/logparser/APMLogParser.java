package com.logparser;

import org.json.JSONObject;
import java.util.*;
import java.io.FileWriter;

public class APMLogParser {
    public void aggregateAndWrite(List<String> logs, String outputFile) {
        Map<String, List<Double>> metrics = new HashMap<>();

        for (String log : logs) {
            try {
                String[] parts = log.split(" ");
                String metric = null;
                Double value = null;

                for (String part : parts) {
                    if (part.startsWith("metric=")) {
                        String[] keyValue = part.split("=");
                        if (keyValue.length == 2) {
                            metric = keyValue[1];
                        }
                    } else if (part.startsWith("value=")) {
                        String[] keyValue = part.split("=");
                        if (keyValue.length == 2) {
                            value = Double.parseDouble(keyValue[1]);
                        }
                    }
                }

                // Skip malformed entries
                if (metric == null || value == null) {
                    System.err.println("Skipping malformed APM log: " + log);
                    continue;
                }

                metrics.putIfAbsent(metric, new ArrayList<>());
                metrics.get(metric).add(value);
            } catch (Exception e) {
                System.err.println("Error processing APM log: " + log);
            }
        }

        JSONObject result = new JSONObject();
        for (String metric : metrics.keySet()) {
            List<Double> values = metrics.get(metric);
            Collections.sort(values);

            double min = values.get(0);
            double max = values.get(values.size() - 1);
            double avg = values.stream().mapToDouble(val -> val).average().orElse(0.0);
            double median = values.size() % 2 == 0
                    ? (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2.0
                    : values.get(values.size() / 2);

            JSONObject stats = new JSONObject();
            stats.put("minimum", min);
            stats.put("median", median);
            stats.put("average", avg);
            stats.put("max", max);

            result.put(metric, stats);
        }

        try (FileWriter file = new FileWriter(outputFile)) {
            System.out.println("Writing APM JSON to: " + outputFile);
            file.write(result.toString(4));
        } catch (Exception e) {
            System.err.println("Error writing APM JSON: " + e.getMessage());
        }
    }
}
