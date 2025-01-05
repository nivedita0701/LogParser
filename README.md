# Log Parser Application

A robust command-line application for parsing and aggregating logs from various sources such as Application Performance Metrics (APM) logs, Application logs, and Request logs. The application classifies log entries, calculates metrics, and generates insightful JSON reports, ensuring extensibility for new log types and file formats.

---

## Features

**✨ Comprehensive Parsing:**
- Handles APM logs, Application logs, and Request logs seamlessly.
- Skips invalid or malformed log lines gracefully.

**📊 Insightful Aggregations:**
- **APM Logs:** Calculates minimum, maximum, median, and average for each metric.
- **Application Logs:** Counts log entries by severity levels (e.g., `INFO`, `ERROR`).
- **Request Logs:** Computes:
  - Response time statistics (min, max, percentiles: 50th, 90th, 95th, 99th).
  - Status code counts by category (e.g., 2XX, 4XX, 5XX).

**🔧 Extensibility:**
- Easily extendable to handle new log types and file formats.

**📂 JSON Output:**
- Generates separate JSON files for each log type (`apm.json`, `application.json`, `request.json`).

---

## **Project Structure**
```plaintext
LogParser/
├── src/
│   ├── main/
│   │   ├── java/com/logparser/
│   │   │   ├── LogProcessor.java           # Entry point of the application
│   │   │   ├── APMLogParser.java           # Handles APM log processing
│   │   │   ├── ApplicationLogParser.java   # Handles Application log processing
│   │   │   ├── RequestLogParser.java       # Handles Request log processing
│   │   │   ├── Utils.java                  # Utility methods for file handling
│   ├── test/
│   │   ├── java/com/logparser/
│   │   │   ├── APMLogParserTest.java       # Unit tests for APMLogParser
│   │   │   ├── ApplicationLogParserTest.java # Unit tests for ApplicationLogParser
│   │   │   ├── RequestLogParserTest.java   # Unit tests for RequestLogParser
├── pom.xml                                 # Maven project configuration
```
---

## Setup and Installation

### Requirements
- Java 8 or higher
- Maven
- Graphviz (for UML diagram generation)

### Steps to Run

1. **Clone the repository:**
   ```
   git clone https://github.com/yourusername/LogParser.git
   cd LogParser
   ```

3. **Build the project:**
   ```
   mvn clean install
   ```

5. **Run the application:**
   ```
   java -cp target/classes com.logparser.LogProcessor --file <path_to_log_file>
   ```

6. **Check the JSON output files** in the project directory:
   - `apm.json`
   - `application.json`
   - `request.json`

---

## Example Usage

### Input Log File

```
timestamp=2024-11-30T12:00:00Z metric=cpu_usage_percent value=75
timestamp=2024-11-30T12:01:00Z level=ERROR message="Application crash"
timestamp=2024-11-30T12:02:00Z request_method=GET request_url="/api/status" response_status=200 response_time_ms=150
```

### Generated Output

**apm.json**

```
{
    "cpu_usage_percent": {
        "minimum": 75,
        "median": 75,
        "average": 75,
        "maximum": 75
    }
}
```

**application.json**

```
{
    "ERROR": 1
}
```

**request.json**

```
{
    "/api/status": {
        "response_times": {
            "min": 150,
            "50_percentile": 150.0,
            "90_percentile": 150.0,
            "95_percentile": 150.0,
            "99_percentile": 150.0,
            "max": 150
        },
        "status_codes": {
            "2XX": 1
        }
    }
}
```

---

## Unit Tests

**Run the unit tests to validate the functionality:**

```
mvn test
```

### Coverage
- Thorough tests for all parsers (`APMLogParser`, `ApplicationLogParser`, `RequestLogParser`).
- Handles edge cases:
  - Empty logs
  - Invalid or malformed log lines
  - Large datasets for percentile calculations

---

## Future Enhancements
- Support for additional log types (e.g., Security logs).
- Compatibility with other file formats (e.g., `.csv`, `.json`).
- Integration with cloud storage for large-scale log processing.

---

## Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository.
2. **Create a new branch**:
   ```
   git checkout -b feature-branch-name
   ```
3. **Commit your changes**:
   ```
   git commit -m "Add feature"
   ```
4. **Push to the branch**:
   ```
   git push origin feature-branch-name
   ```
5. **Open a Pull Request**.

---

## License

This project is licensed under the [MIT License](LICENSE).
