package com.logparser;

import org.json.JSONObject;
import java.io.FileWriter;
import java.util.*;

public class ApplicationLogParser {
    public void aggregateAndWrite(List<String> logs, String outputFile) {
        Map<String, Integer> severityCounts = new HashMap<>();

        for (String log : logs) {
            String[] parts = log.split(" ");
            for (String part : parts) {
                if (part.startsWith("level=")) {
                    String level = part.split("=")[1];
                    severityCounts.put(level, severityCounts.getOrDefault(level, 0) + 1);
                }
            }
        }

        JSONObject result = new JSONObject(severityCounts);

        try (FileWriter file = new FileWriter(outputFile)) {
            System.out.println("Writing Application JSON to: " + outputFile);
            System.out.println("JSON Content: " + result.toString(4));
            file.write(result.toString(4));
        } catch (Exception e) {
            System.err.println("Error writing Application JSON: " + e.getMessage());
        }
    }
}
