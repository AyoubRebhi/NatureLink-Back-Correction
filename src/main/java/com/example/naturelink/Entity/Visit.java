package Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Visit")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    private String time;
    private float price;
    private String duration;

    @ManyToOne
    @JoinColumn(name = "monument_id")
    private Monument monument;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private TourGuide guide;
}
