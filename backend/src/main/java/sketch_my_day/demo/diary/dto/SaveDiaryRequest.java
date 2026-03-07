package sketch_my_day.demo.diary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank // value must not be empty, null, whitespace
    private String userId;

    @NotNull
    private LocalDate entryDate;

    @NotBlank
    @Size(max = 16) // length size
    private String mood;

    @Size(max = 5000)
    private String content;

    @Size(max = 2000)
    private String todo;

    @Size(max = 2000)
    private String reflection;

    private String illustrationUrl;

    @Size(max = 300)
    private String summary;
}