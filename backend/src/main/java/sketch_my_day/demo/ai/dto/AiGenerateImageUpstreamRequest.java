package sketch_my_day.demo.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FastAPI /generate-image 요청 바디 전용 DTO (snake_case).
 * ai-service 로 보낸ㄴ 외부 API 계약
 */
public record AiGenerateImageUpstreamRequest(
        @JsonProperty("user_id")
        String userId,

        @JsonProperty("entry_date")
        String entryDate,

        String mood,
        String content,
        String todo,
        String reflection
) {
}
