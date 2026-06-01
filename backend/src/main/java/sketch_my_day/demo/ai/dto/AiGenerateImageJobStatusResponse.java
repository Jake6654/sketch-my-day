package sketch_my_day.demo.ai.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Fast API 의 GET /generate-image/{jobid} 응답을 받는다
 * completed 상태면 illustrationUrl, summary, prompt, negativePrompt can be filled
 */
public record AiGenerateImageJobStatusResponse(
        @JsonProperty("job_id")
        String jobId,

        @JsonProperty("status")
        String status,

        @JsonProperty("illustration_url")
        String illustrationUrl,

        @JsonProperty("summary")
        String summary,

        @JsonProperty("prompt")
        String prompt,

        @JsonProperty("negative_prompt")
        String negativePrompt,

        @JsonProperty("error")
        String error
) {
}