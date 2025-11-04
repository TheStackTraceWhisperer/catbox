package com.example.catbox.test.listener;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JUnit 5 TestExecutionListener that captures individual test method durations
 * and writes them to docs/test-durations.md.
 * 
 * This listener tracks the execution time of each test method and generates
 * a comprehensive report showing:
 * - Test class and method names
 * - Individual test durations
 * - Total duration per test class
 * - Summary statistics
 */
public class TestDurationListener implements TestExecutionListener {
    
    private final Map<String, Instant> testStartTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> testDurations = new ConcurrentHashMap<>();
    private final Map<String, TestExecutionResult.Status> testStatuses = new ConcurrentHashMap<>();
    private Instant testPlanStartTime;
    
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        testPlanStartTime = Instant.now();
        System.out.println("TestDurationListener: Test execution started");
    }
    
    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            testStartTimes.put(testIdentifier.getUniqueId(), Instant.now());
        }
    }
    
    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            Instant startTime = testStartTimes.get(testIdentifier.getUniqueId());
            if (startTime != null) {
                long durationMs = Duration.between(startTime, Instant.now()).toMillis();
                testDurations.put(testIdentifier.getUniqueId(), durationMs);
                testStatuses.put(testIdentifier.getUniqueId(), testExecutionResult.getStatus());
                testStartTimes.remove(testIdentifier.getUniqueId());
            }
        }
    }
    
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        long totalDurationMs = Duration.between(testPlanStartTime, Instant.now()).toMillis();
        System.out.println("TestDurationListener: Test execution finished, writing report");
        writeTestDurationsReport(testPlan, totalDurationMs);
    }
    
    private void writeTestDurationsReport(TestPlan testPlan, long totalDurationMs) {
        try {
            // Determine the project root directory
            Path projectRoot = findProjectRoot();
            Path docsDir = projectRoot.resolve("docs");
            
            // Create docs directory if it doesn't exist
            if (!Files.exists(docsDir)) {
                Files.createDirectories(docsDir);
            }
            
            Path reportPath = docsDir.resolve("test-durations.md");
            
            // Check if this is the first write (file doesn't exist or is empty)
            boolean isFirstWrite = !Files.exists(reportPath) || Files.size(reportPath) == 0;
            
            // Group tests by class
            Map<String, List<TestInfo>> testsByClass = new TreeMap<>();
            
            for (Map.Entry<String, Long> entry : testDurations.entrySet()) {
                String uniqueId = entry.getKey();
                Long duration = entry.getValue();
                
                TestIdentifier testId = testPlan.getTestIdentifier(uniqueId);
                if (testId != null && testId.isTest()) {
                    String displayName = testId.getDisplayName();
                    String className = extractClassName(testId);
                    String methodName = extractMethodName(displayName);
                    TestExecutionResult.Status status = testStatuses.get(uniqueId);
                    
                    testsByClass.computeIfAbsent(className, k -> new ArrayList<>())
                            .add(new TestInfo(methodName, duration, status));
                }
            }
            
            // Write the report (append mode for multi-module builds)
            StandardOpenOption[] openOptions = isFirstWrite 
                ? new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE}
                : new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND};
                
            try (BufferedWriter writer = Files.newBufferedWriter(reportPath, openOptions)) {
                
                if (isFirstWrite) {
                    writeReportHeader(writer, totalDurationMs);
                }
                writeModuleHeader(writer);
                writeSummaryStatistics(writer, testsByClass, totalDurationMs);
                writeDetailedResults(writer, testsByClass);
            }
            
            System.out.println("TestDurationListener: Report written to " + reportPath);
            
        } catch (IOException e) {
            System.err.println("TestDurationListener: Failed to write test durations report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Path findProjectRoot() {
        // Start from current directory and search upward
        Path current = Paths.get("").toAbsolutePath();
        Path foundParent = null;
        
        while (current != null) {
            Path pomFile = current.resolve("pom.xml");
            if (Files.exists(pomFile)) {
                try {
                    String content = Files.readString(pomFile);
                    // Check if this is the parent pom (catbox-parent)
                    if (content.contains("<artifactId>catbox-parent</artifactId>") && 
                        content.contains("<packaging>pom</packaging>")) {
                        foundParent = current;
                        // Don't break, keep searching upward in case there's a higher parent
                    }
                } catch (IOException e) {
                    // Continue searching
                }
            }
            current = current.getParent();
        }
        
        // Return the found parent, or current directory as fallback
        return foundParent != null ? foundParent : Paths.get("").toAbsolutePath();
    }
    
    private String extractClassName(TestIdentifier testId) {
        // Extract from unique ID which has format like:
        // [engine:junit-jupiter]/[class:com.example.TestClass]/[method:testMethod()]
        String uniqueId = testId.getUniqueId();
        int classStart = uniqueId.indexOf("[class:");
        if (classStart >= 0) {
            int classEnd = uniqueId.indexOf("]", classStart);
            if (classEnd > classStart) {
                String fullClassName = uniqueId.substring(classStart + 7, classEnd);
                // Return just the simple class name
                int lastDot = fullClassName.lastIndexOf('.');
                return lastDot >= 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
            }
        }
        
        return "Unknown";
    }
    
    private String extractMethodName(String displayName) {
        // Remove parentheses and parameters if present
        int parenIndex = displayName.indexOf('(');
        if (parenIndex > 0) {
            return displayName.substring(0, parenIndex);
        }
        return displayName;
    }
    
    private void writeReportHeader(BufferedWriter writer, long totalDurationMs) throws IOException {
        writer.write("# Test Duration Report\n\n");
        writer.write("Generated: " + Instant.now() + "\n\n");
        writer.write("This report aggregates test durations across all modules in the build.\n\n");
        writer.write("---\n\n");
    }
    
    private void writeModuleHeader(BufferedWriter writer) throws IOException {
        // Get module name from current directory
        String moduleName = Paths.get("").toAbsolutePath().getFileName().toString();
        writer.write("## Module: " + moduleName + "\n\n");
    }
    
    private void writeSummaryStatistics(BufferedWriter writer, Map<String, List<TestInfo>> testsByClass, long moduleDurationMs) throws IOException {
        writer.write("### Summary Statistics\n\n");
        
        int totalTests = testsByClass.values().stream().mapToInt(List::size).sum();
        long totalDuration = testsByClass.values().stream()
                .flatMap(List::stream)
                .mapToLong(TestInfo::duration)
                .sum();
        
        long passedTests = testsByClass.values().stream()
                .flatMap(List::stream)
                .filter(t -> t.status == TestExecutionResult.Status.SUCCESSFUL)
                .count();
        
        long failedTests = testsByClass.values().stream()
                .flatMap(List::stream)
                .filter(t -> t.status == TestExecutionResult.Status.FAILED)
                .count();
        
        writer.write("- **Test Classes:** " + testsByClass.size() + "\n");
        writer.write("- **Test Methods:** " + totalTests + "\n");
        writer.write("- **Passed:** " + passedTests + "\n");
        writer.write("- **Failed:** " + failedTests + "\n");
        writer.write("- **Total Test Duration:** " + formatDuration(totalDuration) + "\n");
        writer.write("- **Module Execution Time:** " + formatDuration(moduleDurationMs) + "\n");
        if (totalTests > 0) {
            writer.write("- **Average Test Duration:** " + formatDuration(totalDuration / totalTests) + "\n");
        }
        writer.write("\n");
    }
    
    private void writeDetailedResults(BufferedWriter writer, Map<String, List<TestInfo>> testsByClass) throws IOException {
        writer.write("### Test Details\n\n");
        
        for (Map.Entry<String, List<TestInfo>> entry : testsByClass.entrySet()) {
            String className = entry.getKey();
            List<TestInfo> tests = entry.getValue();
            
            long classDuration = tests.stream().mapToLong(TestInfo::duration).sum();
            
            writer.write("### " + className + "\n\n");
            writer.write("**Class Total Duration:** " + formatDuration(classDuration) + "\n\n");
            
            writer.write("| Test Method | Duration | Status |\n");
            writer.write("|-------------|----------|--------|\n");
            
            // Sort tests by duration (longest first)
            tests.stream()
                    .sorted(Comparator.comparingLong(TestInfo::duration).reversed())
                    .forEach(test -> {
                        try {
                            String statusIcon = test.status == TestExecutionResult.Status.SUCCESSFUL ? "✅" : "❌";
                            writer.write(String.format("| %s | %s | %s |\n", 
                                    test.methodName, 
                                    formatDuration(test.duration),
                                    statusIcon));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            
            writer.write("\n");
        }
        
        writer.write("\n---\n\n");
    }
    
    private String formatDuration(long durationMs) {
        if (durationMs < 1000) {
            return durationMs + "ms";
        } else if (durationMs < 60000) {
            return String.format("%.2fs", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    private record TestInfo(String methodName, long duration, TestExecutionResult.Status status) {}
}
