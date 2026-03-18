package sketch_my_day.demo.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobCreateResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobStatusResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageRequest;
import sketch_my_day.demo.ai.dto.AiGenerateImageUpstreamRequest;

@Service
public class AiImageService {

    private final WebClient aiWebClient;

    public AiImageService(WebClient aiWebClient) {
        this.aiWebClient = aiWebClient;
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

        return aiWebClient.post()
                .uri("/generate-image")// baseUrl 뒤에 /generate-image 을 붙힘
                .bodyValue(upstreamRequest)
                .retrieve()
                .bodyToMono(AiGenerateImageJobCreateResponse.class)// 응답 body 을 해당 DTO 로 변환
                .block(); // 응답이 올때까지 기달
    }

    public AiGenerateImageJobStatusResponse getImageJobStatus(String jobId) {
        return aiWebClient.get()
                .uri("/generate-image/{jobId}", jobId)
                .retrieve()
                .bodyToMono(AiGenerateImageJobStatusResponse.class)
                .block();
    }
}
