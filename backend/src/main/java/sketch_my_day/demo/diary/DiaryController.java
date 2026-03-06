package sketch_my_day.demo.diary;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping
    public List<Diary> getAllDiaries(@RequestParam String userId){
        return diaryService.getAllDiaries(userId);
    }

    @GetMapping("/{date}")
    public Diary getDiaryByDae(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @RequestParam String userId
            ){
        return diaryService.getDiaryByDate(userId, date);
    }

    @PostMapping
    public Diary saveDiary(@RequestBody Diary diary){
        return diaryService.saveDiary(diary);
    }

}

