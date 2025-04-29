package tn.esprit.azizmehdi.entites;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Programme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prId;
    private String prNom ;

    @ManyToMany
    private Set<Utilisateur> utilisateurs;

    @ManyToOne(cascade = CascadeType.ALL)
    private Chaine chaine;

    public void setChaine(Chaine chaine) {
        this.chaine = chaine;
    }

    public Chaine getChaine() {
        return chaine;
    }

    public String getPrNom() {
        return prNom;
    }
}
