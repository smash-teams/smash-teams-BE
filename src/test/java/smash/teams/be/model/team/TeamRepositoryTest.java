package smash.teams.be.model.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.exception.Exception400;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class TeamRepositoryTest extends DummyEntity {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE team_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        teamRepository.save(newTeam("개발"));
        teamRepository.save(newTeam("마케팅"));
        em.clear();
    }

    @Test
    public void find_by_team_test() {
        // given
        String team = "마케팅";

        // when
        Optional<Team> teamOP = teamRepository.findByTeam(team);
        if (teamOP.isEmpty()) {
            throw new Exception400(team, "팀을 찾을 수 없습니다.");
        }

        // then
        assertThat(teamOP.get().getTeam()).isEqualTo("마케팅");
    }
}
