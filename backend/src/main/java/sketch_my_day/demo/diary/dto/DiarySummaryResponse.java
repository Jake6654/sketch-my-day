package sketch_my_day.demo.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 목록 화면에서는 보통 모든 정보가 필요하지 않으니 중요한 데이터만 조회용으로 만들었다
 */
@Getter
@AllArgsConstructor
public class DiarySummaryResponse {

    private UUID id;
    private LocalDate entryDate;
    private String mood;
    // 리스트 카드에 노출할 요약 텍스트(있으면 사용)
    private String summary;
    // summary가 없을 때 본문(content) 기반으로 내려주는 미리보기 텍스트
    private String contentPreview;
    private String illustrationUrl;
}
