package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simplified file-based notification service for sample assignment implementation.
 * Writes all notifications to structured log files instead of real notification systems.
 */
@Service
@Slf4j
public class FileNotificationService {
    
    private static final String NOTIFICATION_LOG_DIR = "notification-logs";
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final ConcurrentHashMap<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();
    private final Path logDirectory;
    
    public FileNotificationService() {
        this.logDirectory = Paths.get(NOTIFICATION_LOG_DIR);
        createLogDirectory();
    }
    
    /**
     * Write notification to appropriate log file based on channel
     */
    public boolean writeNotification(NotificationEntity notification) {
        try {
            String fileName = getLogFileName(notification.getChannel());
            String logEntry = formatLogEntry(notification);
            
            writeToFile(fileName, logEntry);
            
            log.info("Notification written to file: {} -> {}", fileName, notification.getId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to write notification to file: {}", notification.getId(), e);
            return false;
        }
    }
    
    /**
     * Write notification summary to daily summary file
     */
    public void writeNotificationSummary(NotificationEntity notification) {
        try {
            String summaryEntry = formatSummaryEntry(notification);
            String fileName = "daily-summary-" + LocalDateTime.now().format(FILE_FORMATTER) + ".log";
            
            writeToFile(fileName, summaryEntry);
            
        } catch (Exception e) {
            log.error("Failed to write notification summary: {}", notification.getId(), e);
        }
    }
    
    /**
     * Get notification statistics from log files
     */
    public NotificationFileStatistics getFileStatistics() {
        try {
            if (!Files.exists(logDirectory)) {
                return new NotificationFileStatistics();
            }
            
            long totalFiles = Files.list(logDirectory).count();
            long totalSize = Files.walk(logDirectory)
                .filter(Files::isRegularFile)
                .mapToLong(file -> {
                    try {
                        return Files.size(file);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
            
            NotificationFileStatistics stats = new NotificationFileStatistics();
            stats.setTotalFiles(totalFiles);
            stats.setTotalSizeBytes(totalSize);
            stats.setLogDirectory(logDirectory.toString());
            stats.setGeneratedAt(LocalDateTime.now());
            
            return stats;
            
        } catch (Exception e) {
            log.error("Failed to get file statistics", e);
            return new NotificationFileStatistics();
        }
    }
    
    private void createLogDirectory() {
        try {
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
                log.info("Created notification log directory: {}", logDirectory);
            }
        } catch (IOException e) {
            log.error("Failed to create log directory: {}", logDirectory, e);
        }
    }
    
    private String getLogFileName(NotificationChannel channel) {
        String date = LocalDateTime.now().format(FILE_FORMATTER);
        return channel.name().toLowerCase() + "-" + date + ".log";
    }
    
    private String formatLogEntry(NotificationEntity notification) {
        StringBuilder entry = new StringBuilder();
        
        // Header with timestamp and metadata
        entry.append("[").append(LocalDateTime.now().format(TIMESTAMP_FORMATTER)).append("]");
        entry.append(" [").append(notification.getChannel()).append("]");
        entry.append(" [").append(notification.getStatus()).append("]");
        entry.append(" ID=").append(notification.getId());
        entry.append("\n");
        
        // Core notification data
        entry.append("  CorrelationID: ").append(notification.getCorrelationId()).append("\n");
        entry.append("  EventType: ").append(notification.getEventType()).append("\n");
        entry.append("  Recipient: ").append(notification.getRecipient()).append("\n");
        
        if (notification.getSubject() != null) {
            entry.append("  Subject: ").append(notification.getSubject()).append("\n");
        }
        
        // Content with proper formatting
        entry.append("  Content: ").append(notification.getContent() != null ? notification.getContent() : "No content").append("\n");
        
        // Additional metadata
        if (notification.getMetadata() != null) {
            entry.append("  Metadata: ").append(notification.getMetadata()).append("\n");
        }
        
        // Error information if present
        if (notification.getErrorMessage() != null) {
            entry.append("  Error: ").append(notification.getErrorMessage()).append("\n");
        }
        
        entry.append("  CreatedAt: ").append(notification.getCreatedAt()).append("\n");
        if (notification.getSentAt() != null) {
            entry.append("  SentAt: ").append(notification.getSentAt()).append("\n");
        }
        
        // Separator
        entry.append("---").append("\n");
        
        return entry.toString();
    }
    
    private String formatSummaryEntry(NotificationEntity notification) {
        StringBuilder entry = new StringBuilder();
        
        entry.append("[").append(LocalDateTime.now().format(TIMESTAMP_FORMATTER)).append("] ");
        entry.append("SUMMARY: ");
        entry.append(notification.getChannel()).append(" | ");
        entry.append(notification.getEventType()).append(" | ");
        entry.append(notification.getStatus()).append(" | ");
        entry.append(notification.getRecipient()).append(" | ");
        entry.append(notification.getCorrelationId());
        entry.append("\n");
        
        return entry.toString();
    }
    
    private void writeToFile(String fileName, String content) throws IOException {
        Path filePath = logDirectory.resolve(fileName);
        ReentrantLock lock = fileLocks.computeIfAbsent(fileName, k -> new ReentrantLock());
        
        lock.lock();
        try {
            // Append to file (create if doesn't exist)
            try (FileWriter fw = new FileWriter(filePath.toFile(), true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.write(content);
                pw.flush();
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Statistics for file-based notifications
     */
    public static class NotificationFileStatistics {
        private LocalDateTime generatedAt;
        private Long totalFiles;
        private Long totalSizeBytes;
        private String logDirectory;
        
        // Getters and setters
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public Long getTotalFiles() { return totalFiles; }
        public void setTotalFiles(Long totalFiles) { this.totalFiles = totalFiles; }
        
        public Long getTotalSizeBytes() { return totalSizeBytes; }
        public void setTotalSizeBytes(Long totalSizeBytes) { this.totalSizeBytes = totalSizeBytes; }
        
        public String getLogDirectory() { return logDirectory; }
        public void setLogDirectory(String logDirectory) { this.logDirectory = logDirectory; }
        
        public String getFormattedSize() {
            if (totalSizeBytes == null) return "0 B";
            
            if (totalSizeBytes < 1024) return totalSizeBytes + " B";
            if (totalSizeBytes < 1024 * 1024) return String.format("%.1f KB", totalSizeBytes / 1024.0);
            return String.format("%.1f MB", totalSizeBytes / (1024.0 * 1024.0));
        }
    }
}