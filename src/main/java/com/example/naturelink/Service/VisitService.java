package Service;
import Entity.Visit;
import Repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitService {
    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit addVisit(Visit visit) {
        return visitRepository.save(visit);
    }


    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }


    public Optional<Visit> getVisitById(Integer id) {
        return visitRepository.findById(id);
    }





    public void deleteVisit(Integer id) {
        visitRepository.deleteById(id);
    }
}
