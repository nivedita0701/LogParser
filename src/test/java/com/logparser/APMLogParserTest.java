package com.logparser;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class APMLogParserTest {

    @Test
    public void testAggregateAndWrite_validLogs() throws IOException {
        APMLogParser parser = new APMLogParser();
        List<String> logs = Arrays.asList(
                "timestamp=2024-11-30T10:00:00Z metric=cpu_usage_percent host=webserver1 value=85",
                "timestamp=2024-11-30T10:01:00Z metric=cpu_usage_percent host=webserver1 value=75",
                "timestamp=2024-11-30T10:02:00Z metric=memory_usage_percent host=webserver1 value=50"
        );
        String outputFile = "test_apm.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should contain cpu_usage_percent", result.has("cpu_usage_percent"));
        assertEquals(75.0, result.getJSONObject("cpu_usage_percent").getDouble("minimum"), 0.001);
        assertEquals(85.0, result.getJSONObject("cpu_usage_percent").getDouble("max"), 0.001);
        assertTrue("Result should contain memory_usage_percent", result.has("memory_usage_percent"));

        // Clean up
        file.delete();
    }

    @Test
    public void testAggregateAndWrite_malformedLogs() throws IOException {
        APMLogParser parser = new APMLogParser();
        List<String> logs = Arrays.asList(
                "timestamp=2024-11-30T10:00:00Z metric=cpu_usage_percent value=" // Malformed log
        );
        String outputFile = "test_apm.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should be empty for malformed logs", result.isEmpty());

        // Clean up
        file.delete();
    }
}
