package sketch_my_day.demo.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO는 클라이언트가 저장 요청을 보낼 때 사용하는 객체다
 * 이 클래스의 역할은 클라이언트가 JSON을 보내며면 Spring dl JSON 을 자동으로 SaveDiaryRequest 객체로 바꿔주는 역할을한다
 */
@Getter
@Setter
@NoArgsConstructor
public class SaveDiaryRequest {

    private String userId;
    private LocalDate entryDate;
    private String mood;
    private String content;
    private String todo;
    private String reflection;
    private String illustrationUrl;
    private String summary;
}