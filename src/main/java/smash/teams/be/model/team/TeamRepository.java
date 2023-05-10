package smash.teams.be.model.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select t from Team t where t.teamName = :teamName")
    Optional<Team> findByTeamName(@Param("teamName") String teamName);

    @Query("select t from Team t where t.teamName = :teamName")
    Team findTeamByTeamName(@Param("teamName") String teamName);
}
