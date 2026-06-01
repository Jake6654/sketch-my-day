package sketch_my_day.demo.logging;

import java.time.LocalDateTime;

public class OpsLensRequest {
    private LocalDateTime timestamp;
    private String level;
    private String project;
    private String environment;
    private String service;
    private String message;


    // default constructor is mainly added because frameworks like Spring
    // and Jackson often need it when creating objects internally
    // So adding a no-args constructor is a common framework-friendly convention in Java DTOs
    public OpsLensRequest() {
    }

    public OpsLensRequest(
            LocalDateTime timestamp,
            String level,
            String project,
            String environment,
            String service,
            String message
    ) {
        this.timestamp = timestamp;
        this.level = level;
        this.project = project;
        this.environment = environment;
        this.service = service;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getProject() {
        return project;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getService() {
        return service;
    }

    public String getMessage() {
        return message;
    }
}