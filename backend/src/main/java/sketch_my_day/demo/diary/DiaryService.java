package sketch_my_day.demo.diary;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    // Constructor injection
    // Spring automatically gives this class the repository it needs
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public List<Diary> getAllDiaries(String userId){
        return diaryRepository.findByUserIdOrderByEntryDateDesc(userId);
    }

    public Diary getDiaryByDate(String userId, LocalDate entryDate) {
        return diaryRepository.findByUserIdAndEntryDate(userId, entryDate)
                .orElse(null);
    }

    // if the user already has a diary for that date -> update it
    // otherwise -> create a new one
    public Diary saveDiary(Diary diary){
        Diary existingDiary = diaryRepository
                .findByUserIdAndEntryDate(diary.getUserId(), diary.getEntryDate())
                .orElse(null);

        if (existingDiary != null){
            existingDiary.setMood(diary.getMood());
            existingDiary.setContent(diary.getContent());
            existingDiary.setTodo(diary.getTodo());
            existingDiary.setReflection(diary.getReflection());
            existingDiary.setIllustrationUrl(diary.getIllustrationUrl());
            existingDiary.setSummary(diary.getSummary());

            return diaryRepository.save(existingDiary);
        }
        return diaryRepository.save(diary);
    }
}
