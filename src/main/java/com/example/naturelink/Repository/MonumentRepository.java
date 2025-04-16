package Repository;


import Entity.Monument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonumentRepository extends JpaRepository<Monument, Integer> {
}
