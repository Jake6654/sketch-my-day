package sketch_my_day.demo.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobCreateResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobStatusResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageRequest;
import sketch_my_day.demo.ai.dto.AiGenerateImageUpstreamRequest;
import sketch_my_day.demo.logging.OpsLensClient;

@Service
public class AiImageService {

    private final WebClient aiWebClient;
    private final OpsLensClient opsLensClient;

    public AiImageService(WebClient aiWebClient, OpsLensClient opsLensClient) {
        this.aiWebClient = aiWebClient;
        this.opsLensClient = opsLensClient;
    }

    // FastAPI 의 POST /generate-image 을 호출
    public AiGenerateImageJobCreateResponse createImageJob(AiGenerateImageRequest request) {
        AiGenerateImageUpstreamRequest upstreamRequest = new AiGenerateImageUpstreamRequest(
                request.userId(),
                request.entryDate(),
                request.mood(),
                request.content(),
                request.todo(),
                request.reflection()
        );

        try {
            AiGenerateImageJobCreateResponse response = aiWebClient.post()
                    .uri("/generate-image") // baseUrl 뒤에 /generate-image 을 붙힘
                    .bodyValue(upstreamRequest)
                    .retrieve()
                    .bodyToMono(AiGenerateImageJobCreateResponse.class) // 응답 body 을 해당 DTO 로 변환
                    .block();// 응답이 올때까지 기달

            opsLensClient.sendLog(
                    "INFO",
                    "AiImageService",
                    "AI image job created successfully for userId=" + request.userId()
            );

            return response;

        } catch (Exception error) {
            opsLensClient.sendLog(
                    "ERROR",
                    "AiImageService",
                    "AI image job creation failed: "
                            + error.getClass().getSimpleName()
                            + " - "
                            + error.getMessage()
            );

            throw new RuntimeException(error);
        }
        }



    public AiGenerateImageJobStatusResponse getImageJobStatus(String jobId) {
        return aiWebClient.get()
                .uri("/generate-image/{jobId}", jobId)
                .retrieve()
                .bodyToMono(AiGenerateImageJobStatusResponse.class)
                .block();
    }
}
