package sketch_my_day.demo.ai;


import org.springframework.web.bind.annotation.*;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobCreateResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageJobStatusResponse;
import sketch_my_day.demo.ai.dto.AiGenerateImageRequest;

@RestController
@RequestMapping("/api/ai") //common prefix
public class AiImageController {

    private final AiImageService aiImageService;

    public AiImageController(AiImageService aiImageService) {
        this.aiImageService = aiImageService;
    }

    /**
     * Frontend -> Spring -> FastAPI
     */
    @PostMapping("/generate-image") // frontend 가 이 endpoint 을 호출하면
    public AiGenerateImageJobCreateResponse createImageJob(
            @RequestBody AiGenerateImageRequest request
    ) {
        // backend 가 다시 FastAPI 을 호출한다
        return aiImageService.createImageJob(request);
    }

    @GetMapping("/generate-image/{jobId}")
    public AiGenerateImageJobStatusResponse getImageJobStatus(
            @PathVariable String jobId
    ) {
        return aiImageService.getImageJobStatus(jobId);
    }
}