package sketch_my_day.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sketch_my_day.demo.logging.OpsLensClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// This class can handle exceptions thrown during controller request processing
@ControllerAdvice
public class GlobalExceptionHandler {

    private final OpsLensClient opsLensClient;


    public GlobalExceptionHandler(OpsLensClient opsLensClient) {
        this.opsLensClient = opsLensClient;
    }

    // Handle Validation/ Bad Request Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(
            MethodArgumentNotValidException error,
            HttpServletRequest request
    ) {
        opsLensClient.sendLog(
                "ERROR",
                "GlobalExceptionHandler",
                "Validation failed at " + request.getRequestURI() + ": " + error.getMessage()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Request validation failed");
        body.put("path", request.getRequestURI());

        // returns HTTP 400 with a JSON response body
        return ResponseEntity.badRequest().body(body);
    }

    // Handle JSON Parse / Request Body Errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(
            HttpMessageNotReadableException error,
            HttpServletRequest request
    ) {
        opsLensClient.sendLog(
                "ERROR",
                "GlobalExceptionHandler",
                "Invalid request body at " + request.getRequestURI() + ": " + error.getMessage()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Invalid request body");
        body.put("path", request.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    // Handle Missing Params/ Type Mismatch
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException error,
            HttpServletRequest request
    ) {
        opsLensClient.sendLog(
                "ERROR",
                "GlobalExceptionHandler",
                "Missing request parameter at " + request.getRequestURI() + ": " + error.getParameterName()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Missing request parameter: " + error.getParameterName());
        body.put("path", request.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    // Generic Fallback Handler
    // This catches unexpected controller-level/runtime errors
    // Be careful not to include secrets or full request body in the log
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(
            Exception error,
            HttpServletRequest request
    ) {
        opsLensClient.sendLog(
                "ERROR",
                "GlobalExceptionHandler",
                "Unhandled backend error at "
                        + request.getRequestURI()
                        + ": "
                        + error.getClass().getSimpleName()
                        + " - "
                        + error.getMessage()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", "Unexpected backend error");
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(500).body(body);
    }

}
