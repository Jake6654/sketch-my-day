package sketch_my_day.demo.diary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// This file is your database access layer
// Extends JpaRepository to automatically get methods like 
// Save, findById, findAll ...
public interface DiaryRepository extends JpaRepository<Diary, UUID> {

    // Find all diary rows where userId matches, ordered by entryDate descending
    List<Diary> findByUserIdOrderByEntryDateDesc(String userId);

    // Uses Optional because maybe the diary exits, maybe it does not
    Optional<Diary> findByUserIdAndEntryDate(String userId, LocalDate entryDate);

}

