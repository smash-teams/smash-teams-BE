package smash.teams.be.model.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t where t.team = :team")
    Optional<Team> findByTeam(@Param("team") String team);
}
