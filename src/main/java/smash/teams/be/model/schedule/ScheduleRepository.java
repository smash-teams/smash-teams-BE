package smash.teams.be.model.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smash.teams.be.model.team.Team;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user u JOIN FETCH u.team t")
    List<Schedule> findSchedules();

    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId")
    List<Schedule> findSchedulesByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.user u JOIN FETCH u.team t WHERE s.user.team.id = :teamId ORDER BY s.id DESC")
    List<Schedule> findSchedulesByTeamId(@Param("teamId")Long teamId);

    @Query("SELECT s FROM Schedule s Join FETCH s.user u")
    List<Schedule> findSchedulesWithName();

    @Query("SELECT s FROM Schedule s WHERE s.id = :scheduleId")
    Schedule findScheduleByScheduleId(@Param("scheduleId") Long scheduleId);
}
