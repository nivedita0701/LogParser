package com.logparser;

import org.json.JSONObject;
import java.util.*;
import java.io.FileWriter;

public class RequestLogParser {
    public void aggregateAndWrite(List<String> logs, String outputFile) {
        Map<String, List<Integer>> responseTimes = new HashMap<>();
        Map<String, Map<String, Integer>> statusCodes = new HashMap<>();

        for (String log : logs) {
            String[] parts = log.split(" ");
            String url = null;
            int time = -1;
            String status = null;

            for (String part : parts) {
                if (part.startsWith("request_url=")) {
                    url = part.split("=")[1].replace("\"", "");
                } else if (part.startsWith("response_time_ms=")) {
                    time = Integer.parseInt(part.split("=")[1]);
                } else if (part.startsWith("response_status=")) {
                    status = part.split("=")[1];
                }
            }

            if (url == null || time == -1 || status == null) {
                continue;
            }

            responseTimes.putIfAbsent(url, new ArrayList<>());
            responseTimes.get(url).add(time);

            statusCodes.putIfAbsent(url, new HashMap<>());
            String category = status.charAt(0) + "XX";
            statusCodes.get(url).put(category, statusCodes.get(url).getOrDefault(category, 0) + 1);
        }

        JSONObject result = new JSONObject();
        for (String url : responseTimes.keySet()) {
            List<Integer> times = responseTimes.get(url);
            Collections.sort(times);

            int min = times.get(0);
            int max = times.get(times.size() - 1);
            double median = times.size() % 2 == 0
                    ? (times.get(times.size() / 2 - 1) + times.get(times.size() / 2)) / 2.0
                    : times.get(times.size() / 2);
            double p90 = calculatePercentile(times, 90);
            double p95 = calculatePercentile(times, 95);
            double p99 = calculatePercentile(times, 99);

            Map<String, Object> responseTimesMap = new HashMap<>();
            responseTimesMap.put("min", min);
            responseTimesMap.put("max", max);
            responseTimesMap.put("50_percentile", median);
            responseTimesMap.put("90_percentile", p90);
            responseTimesMap.put("95_percentile", p95);
            responseTimesMap.put("99_percentile", p99);

            JSONObject urlData = new JSONObject();
            urlData.put("response_times", new JSONObject(responseTimesMap));
            urlData.put("status_codes", new JSONObject(statusCodes.get(url)));

            result.put(url, urlData);
        }

        try (FileWriter file = new FileWriter(outputFile)) {
            System.out.println("Writing Request JSON to: " + outputFile);
            System.out.println("JSON Content: " + result.toString(4));
            file.write(result.toString(4));
        } catch (Exception e) {
            System.err.println("Error writing Request JSON: " + e.getMessage());
        }
    }

    private double calculatePercentile(List<Integer> sortedList, double percentile) {
        if (sortedList.isEmpty()) {
            return 0.0;
        }
        double position = (percentile / 100.0) * (sortedList.size() - 1);
        int lowerIndex = (int) Math.floor(position);
        int upperIndex = (int) Math.ceil(position);

        // Interpolate between the two indices if necessary
        if (lowerIndex == upperIndex) {
            return sortedList.get(lowerIndex);
        } else {
            double lowerValue = sortedList.get(lowerIndex);
            double upperValue = sortedList.get(upperIndex);
            return lowerValue + (position - lowerIndex) * (upperValue - lowerValue);
        }
    }
}