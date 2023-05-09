package smash.teams.be.model.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s")
    List<Schedule> findSchedules();

    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId")
    List<Schedule> findSchedulesByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Schedule s WHERE s.user.team.teamName = :teamName")
    List<Schedule> findSchedulesByTeamName(@Param("teamName") String teamName);


    @Query("SELECT s FROM Schedule s Join FETCH s.user u")
    List<Schedule> findSchedulesWithName();

    @Query("SELECT s FROM Schedule s WHERE s.id = :scheduleId")
    Schedule findScheduleByScheduleId(@Param("scheduleId") Long scheduleId);

}
