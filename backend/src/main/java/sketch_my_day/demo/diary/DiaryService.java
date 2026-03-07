package sketch_my_day.demo.diary;

import org.springframework.stereotype.Service;
import sketch_my_day.demo.diary.dto.DiaryDetailResponse;
import sketch_my_day.demo.diary.dto.DiarySummaryResponse;
import sketch_my_day.demo.diary.dto.SaveDiaryRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaryService {
    // use final to protect the variable to be reassigned after initialization
    private final DiaryRepository diaryRepository;

    // Constructor injection
    // Spring automatically gives this class the repository it needs
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    // returns a list of diary summaries for a user
    public List<DiarySummaryResponse> getAllDiaries(String userId){
        return diaryRepository.findByUserIdOrderByEntryDateDesc(userId)
                .stream()
                .map(this::toSummaryResponse) // convert each Diary object into a DiarySummaryResponse
                .toList();
    }

    public DiaryDetailResponse getDiaryByDate(String userId, LocalDate entryDate){
        Diary diary = diaryRepository.findByUserIdAndEntryDate(userId, entryDate)
                .orElse(null);

        if (diary == null){
            return null;
        }
        return toDetailResponse(diary);
    }

    // diary exist -> update , not exist -> create a new one
    // The requests is received as a DTO, instead of using the Diary entity directly
    // This is good practice because it separates API input from database models
    public DiaryDetailResponse saveDiary(SaveDiaryRequest request) {
        Diary diary = diaryRepository.findByUserIdAndEntryDate(request.getUserId(), request.getEntryDate())
                .orElse(new Diary());

        // set field, transfer data from the DTO to the Entity
        diary.setUserId(request.getUserId());
        diary.setEntryDate(request.getEntryDate());
        diary.setMood(request.getMood());
        diary.setContent(request.getContent());
        diary.setTodo(request.getTodo());
        diary.setReflection(request.getReflection());
        diary.setIllustrationUrl(request.getIllustrationUrl());
        diary.setSummary(request.getSummary());

        Diary savedDiary = diaryRepository.save(diary);

        return toDetailResponse(savedDiary);
    }

    /**
     * Mapping Methods
     * Diary -> DiarySummaryResponse
     * Diary -> DiaryDetailResponse
     */
    private DiarySummaryResponse toSummaryResponse(Diary diary) {
        return new DiarySummaryResponse(
                diary.getId(),
                diary.getEntryDate(),
                diary.getMood(),
                diary.getSummary(),
                diary.getIllustrationUrl()
        );
    }

    private DiaryDetailResponse toDetailResponse(Diary diary) {
        return new DiaryDetailResponse(
                diary.getId(),
                diary.getUserId(),
                diary.getEntryDate(),
                diary.getMood(),
                diary.getContent(),
                diary.getTodo(),
                diary.getReflection(),
                diary.getIllustrationUrl(),
                diary.getSummary(),
                diary.getCreatedAt(),
                diary.getUpdatedAt()
        );
    }
}
}
