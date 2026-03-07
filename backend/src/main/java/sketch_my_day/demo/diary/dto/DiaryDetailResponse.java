package sketch_my_day.demo.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 *  일기 하나를 자세히 조회할 때 반환하는 응답 객체
 *  요청 DTO 는 값을 받아야 하니 setter 가 필료하지만 응답 DTO는 만들어서 반환만 하면 되니 없음
 */
@Getter
// 모든 필드를 받는 생성자를 자동 생성해.
@AllArgsConstructor
public class DiaryDetailResponse {

    private UUID id;
    private String userId;
    private LocalDate entryDate;
    private String mood;
    private String content;
    private String todo;
    private String reflection;
    private String illustrationUrl;
    private String summary;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}