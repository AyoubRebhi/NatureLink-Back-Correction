package Service;
import Entity.Monument;
import Repository.MonumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonumentService {
    private final MonumentRepository monumentRepository;

    public MonumentService(MonumentRepository monumentRepository) {
        this.monumentRepository = monumentRepository;
    }


    public Monument addMonument(Monument monument) {
        return monumentRepository.save(monument);
    }


    public List<Monument> getAllMonuments() {
        return monumentRepository.findAll();
    }


    public Optional<Monument> getMonumentById(Integer id) {
        return monumentRepository.findById(id);
    }





    public void deleteMonument(Integer id) {
        monumentRepository.deleteById(id);
    }
}
