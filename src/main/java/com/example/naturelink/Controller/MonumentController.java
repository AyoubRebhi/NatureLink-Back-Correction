package Controller;
import Entity.Monument;
import Service.MonumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/monuments")
public class MonumentController {
    private final MonumentService monumentService;

    public MonumentController(MonumentService monumentService) {
        this.monumentService = monumentService;
    }


    @PostMapping
    public ResponseEntity<Monument> addMonument(@RequestBody Monument monument) {
        Monument createdMonument = monumentService.addMonument(monument);
        return ResponseEntity.ok(createdMonument);
    }


    @GetMapping
    public ResponseEntity<List<Monument>> getAllMonuments() {
        return ResponseEntity.ok(monumentService.getAllMonuments());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Monument> getMonumentById(@PathVariable Integer id) {
        Optional<Monument> monument = monumentService.getMonumentById(id);
        return monument.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonument(@PathVariable Integer id) {
        monumentService.deleteMonument(id);
        return ResponseEntity.noContent().build();
    }
}
