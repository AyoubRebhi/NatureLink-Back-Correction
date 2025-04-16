package Controller;
import Entity.Visit;
import Service.VisitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/visits")
public class VisitController {
    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }


    @PostMapping
    public ResponseEntity<Visit> addVisit(@RequestBody Visit visit) {
        Visit createdVisit = visitService.addVisit(visit);
        return ResponseEntity.ok(createdVisit);
    }


    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        return ResponseEntity.ok(visitService.getAllVisits());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Integer id) {
        Optional<Visit> visit = visitService.getVisitById(id);
        return visit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Integer id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}
