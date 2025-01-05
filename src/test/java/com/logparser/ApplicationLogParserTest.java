package com.logparser;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ApplicationLogParserTest {
    @Test
    public void testAggregateAndWrite_validLogs() throws IOException {
        ApplicationLogParser parser = new ApplicationLogParser();
        List<String> logs = Arrays.asList(
                "timestamp=2024-11-30T11:00:00Z level=INFO message=\"System started\"",
                "timestamp=2024-11-30T11:01:00Z level=ERROR message=\"Disk failure detected\"",
                "timestamp=2024-11-30T11:02:00Z level=WARNING message=\"High CPU usage\"",
                "timestamp=2024-11-30T11:03:00Z level=DEBUG message=\"Debugging network issue\""
        );
        String outputFile = "test_application.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertEquals(1, result.getInt("INFO"));
        assertEquals(1, result.getInt("ERROR"));
        assertEquals(1, result.getInt("WARNING"));
        assertEquals(1, result.getInt("DEBUG"));

        // Clean up
        file.delete();
    }

    @Test
    public void testAggregateAndWrite_emptyLogs() throws IOException {
        ApplicationLogParser parser = new ApplicationLogParser();
        List<String> logs = Arrays.asList(); // Empty log list
        String outputFile = "test_application.json";

        parser.aggregateAndWrite(logs, outputFile);

        File file = new File(outputFile);
        assertTrue("Output file should exist", file.exists());

        JSONObject result = new JSONObject(Utils.readFileAsString(outputFile));
        assertTrue("Result should be empty for empty logs", result.isEmpty());

        // Clean up
        file.delete();
    }

}
