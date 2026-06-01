package sketch_my_day.demo.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** FastAPI POST /generate-image 응답을 받는 DTO
 * {
 *    "job_id": "123",
 *   "status": "pending"
 * }
 */
public record AiGenerateImageJobCreateResponse(
        @JsonProperty("job_id")
        String jobId,

        @JsonProperty("status")
        String status
) {
}