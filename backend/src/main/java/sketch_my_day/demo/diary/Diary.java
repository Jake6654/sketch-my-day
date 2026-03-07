package sketch_my_day.demo.diary;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity // this make this class be recognized as a database entity
@Table(name = "diaries", // diaries table 과 map
uniqueConstraints = {
        // One user can have only one diary per date, this match my database rule
        @UniqueConstraint(columnNames = {"user_id", "entry_date"})
})
@Getter
@Setter
@NoArgsConstructor
public class Diary {

    @Id // primary key
    @GeneratedValue // ID value is generated automatically
    private UUID id;

    // We use name mapping if a java field and DB colum are different
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(nullable = false, length=16)
    private String mood;

    // columnDefinition = text tells JPA that the database colum is a long text column
    // if you Varchar -> length = ? it may cause an error as long as it is a very long text
    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String todo;

    @Column(columnDefinition = "text")
    private String reflection;

    @Column(name = "illustration_url", columnDefinition = "text")
    private String illustrationUrl;

    // use length when the length of content can be predictable
    @Column(length = 300)
    private String summary;


    // insertable and updateble = false
    // means that read these values from the database, but do not try to manually write them from JPA
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

}
