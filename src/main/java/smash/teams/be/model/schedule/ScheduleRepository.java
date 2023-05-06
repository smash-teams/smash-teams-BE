package smash.teams.be.model.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId")
    List<Schedule> findSchedulesByUserId(Long userId);
}
