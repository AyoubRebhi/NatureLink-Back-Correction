package tn.esprit.azizmehdi.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usrId ;
    private String usrNom ;
    private Date usrDateInscription;
    @Enumerated(EnumType.STRING)
    private Profession profession ;
    @ManyToMany
    private List<Programme> programmesFavoris = new ArrayList<>();

    public List<Programme> getProgrammesFavoris() {
        return programmesFavoris;
    }

    public String getUsrNom() {
        return usrNom;
    }

    public Date getUsrDateInscription() {
        return usrDateInscription;
    }

    public Profession getProfession() {
        return profession;
    }
}
