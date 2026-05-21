package sketch_my_day.demo.logging;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

/**
 * This is the service responsible for sending logs to OpsLens backend
 */
@Service
public class OpsLensClient {

    // This object sends HTTP request to OpsLens
    private final RestClient restClient;

    // These values store the OpsLens connection settings
    private final String opsLensUrl;
    private final String apiKey;
    private final String project;
    private final String environment;

    public OpsLensClient(
            // This reads opslens.url from application.properties, and if it does not exist, it uses http://localhost:8080
            @Value("${opslens.url:http://localhost:8080}") String opsLensUrl,
            @Value("${opslens.api-key:}") String apiKey,
            @Value("${opslens.project:sketch-my-day}") String project,
            @Value("${opslens.environment:dev}") String environment
    ) {
        this.restClient = RestClient.create();
        this.opsLensUrl = opsLensUrl;
        this.apiKey = apiKey;
        this.project = project;
        this.environment = environment;
    }

    public void sendLog(String level, String service, String message) {
        if (apiKey == null || apiKey.isBlank()) {
            return;

        }
        // This creates the JSON body that will be sent to OpsLens
        OpsLensRequest request = new OpsLensRequest(
                LocalDateTime.now(),
                level,
                project,
                environment,
                service,
                message
        );

        try {
            // This starts an HTTP POST request
            restClient.post()
                    // Sends te request to the OpsLens /logs endpoint
                    .uri(opsLensUrl + "/logs")
                    .header("x-api-key", apiKey)
                    .body(request) // attaches the log data as the request body
                    .retrieve() // sends the request
                    .toBodilessEntity(); // ignores the response body
        } catch (Exception e) {
            System.out.println("Failed to send long to OpsLens: " + e.getMessage());
        }

    }
}
