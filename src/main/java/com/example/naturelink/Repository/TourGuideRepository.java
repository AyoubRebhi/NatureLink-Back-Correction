package Repository;

import Entity.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourGuideRepository extends JpaRepository<TourGuide, Integer> {
}
