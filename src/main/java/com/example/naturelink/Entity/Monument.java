package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "monument")
public class Monument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    @Getter
    private String nom;
    private String description;
    private String localisation;
    private String horairesOuverture;
    private float prixEntree;
    private String image;

    @OneToMany(mappedBy = "monument", cascade = CascadeType.ALL)
    private List<Visit> visite;

}
