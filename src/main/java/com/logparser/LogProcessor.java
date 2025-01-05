package com.logparser;

import java.io.*;
import java.util.*;

public class LogProcessor {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("--file")) {
            System.out.println("Usage: --file <filename>");
            return;
        }

        String filename = args[1];
        System.out.println("Processing file: " + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> apmLogs = new ArrayList<>();
            List<String> applicationLogs = new ArrayList<>();
            List<String> requestLogs = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("metric=")) {
                    apmLogs.add(line);
                } else if (line.contains("level=")) {
                    applicationLogs.add(line);
                } else if (line.contains("request_method=")) {
                    requestLogs.add(line);
                }
            }

             //Process and write JSON files
            APMLogParser apmParser = new APMLogParser();
            apmParser.aggregateAndWrite(apmLogs, "apm.json");

            ApplicationLogParser applicationParser = new ApplicationLogParser();
            applicationParser.aggregateAndWrite(applicationLogs, "application.json");

            RequestLogParser requestParser = new RequestLogParser();
            requestParser.aggregateAndWrite(requestLogs, "request.json");

            System.out.println("Log processing completed successfully.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
