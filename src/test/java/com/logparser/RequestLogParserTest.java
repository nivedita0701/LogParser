package com.logparser;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RequestLogParserTest {

    @Test
    public void testAggregateAndWrite_validLogs() throws IOException {
        RequestLogParser parser = new RequestLogParser();
        List<String> logs = Arrays.asList(
                "timestamp=2024-11-30T12:00:00Z request_method=GET request_url=\"/api/status\" response_status=200 response_time_ms=100",
                "timestamp=2024-11-30T12:01:00Z request_method=POST request_url=\"/api/status\" response_status=500 response_time_ms=300",
                "timestamp=2024-11-30T12:02:00Z request_method=GET request_url=\"/api/status\" response_status=200 response_time_ms=150",
                "timestamp=2024-11-30T12:03:00Z request_method=POST request_url=\"/api/update\" response_status=404 response_time_ms=200"
        );
        String outputFile = "test_request.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should contain /api/status", result.has("/api/status"));
        assertTrue("Result should contain /api/update", result.has("/api/update"));

        JSONObject statusData = result.getJSONObject("/api/status").getJSONObject("response_times");
        assertEquals(100, statusData.getInt("min"));
        assertEquals(300, statusData.getInt("max"));
        assertEquals(150.0, statusData.getDouble("50_percentile"), 0.001);
        assertEquals(270.0, statusData.getDouble("90_percentile"), 0.001);
        assertEquals(285.0, statusData.getDouble("95_percentile"), 0.001);
        assertEquals(297.0, statusData.getDouble("99_percentile"), 0.001);

        JSONObject updateData = result.getJSONObject("/api/update").getJSONObject("response_times");
        assertEquals(200, updateData.getInt("min"));
        assertEquals(200, updateData.getInt("max"));
        assertEquals(200.0, updateData.getDouble("50_percentile"), 0.001);


        // Clean up
        file.delete();
    }

    @Test
    public void testAggregateAndWrite_emptyLogs() throws IOException {
        RequestLogParser parser = new RequestLogParser();
        List<String> logs = Arrays.asList(); // Empty log list
        String outputFile = "test_request.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should be empty for empty logs", result.isEmpty());

        // Clean up
        file.delete();
    }

    @Test
    public void testAggregateAndWrite_invalidLogs() throws IOException {
        RequestLogParser parser = new RequestLogParser();
        List<String> logs = Arrays.asList(
                "timestamp=2024-11-30T12:00:00Z request_method=GET response_time_ms=100", // Missing request_url and response_status
                "timestamp=2024-11-30T12:01:00Z request_url=\"/api/status\" response_time_ms=300" // Missing request_method and response_status
        );
        String outputFile = "test_request.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should be empty for invalid logs", result.isEmpty());

        // Clean up
        file.delete();
    }

}
