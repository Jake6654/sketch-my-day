package sketch_my_day.demo.diary;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 *  This file handles HTTP requests from the frontend (Handle HTTP only)
 *  Service - Handles business logic
 *  Repository - Handle DB access
 *  Entity - Represents database data
 */

/**
 * Flow:
 * DiaryController.getAllDiaries() receives the request
 * It calls DiaryService.getAllDiaries(userId)
 * Service calls DiaryRepository.findByUserIdOrderByEntryDateDesc(userId)
 * Repository fetches rows from PostgreSQL
 * JPA converts rows into Diary objects
 * Controller returns them as JSON
 */
@RestController // tell this class handles REST API requests and return JSON data
@RequestMapping("/api/diaries") // Sets the base path for all endpoints in this controller
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping
    // RequestParam reads a query parameter from the url
    // ex: GET /api/diaries?userId=abc123
    public List<Diary> getAllDiaries(@RequestParam String userId){
        return diaryService.getAllDiaries(userId);
    }

    @GetMapping("/{date}")
    // PathVariable reads a value from the URL path
    // ex: GET /api/diaries/2026-03-06?userId=abc123 then receives 2026-03-06
    public Diary getDiaryByDae(
            @PathVariable
            // This helps Spring correctly convert the path string into a LocalDate
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @RequestParam String userId
            ){
        return diaryService.getDiaryByDate(userId, date);
    }

    // RequestBody take the JSON body from the request and convert it into a java object
    @PostMapping
    public Diary saveDiary(@RequestBody Diary diary){
        return diaryService.saveDiary(diary);
    }

}

