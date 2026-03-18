package sketch_my_day.demo.ai.dto;


/**
 * This DTO 는 프론트에서 받는 API 계약
 */
public record AiGenerateImageRequest(
        String userId,
        String entryDate,
        String mood,
        String content,
        String todo,
        String reflection
) {
}