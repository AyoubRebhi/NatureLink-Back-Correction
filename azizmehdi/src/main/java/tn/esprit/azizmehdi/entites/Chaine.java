package tn.esprit.azizmehdi.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chaine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long chId ;
   private String chNom ;
   @Enumerated(EnumType.STRING)
  private   Thematique chTheme ;
   @OneToMany(mappedBy = "chaine")
    private List<Programme> programmes = new ArrayList<>();

    public List<Programme> getProgrammes() {
        return programmes;
    }

    public String getChNom() {
        return chNom;

    }

    public Thematique getChTheme() {
        return chTheme;
    }
}
