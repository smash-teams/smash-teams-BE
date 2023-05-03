package smash.teams.be.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(BCryptPasswordEncoder.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest extends DummyEntity {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE team_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        userRepository.save(newUser("이승민"));
        userRepository.save(newUser("송재근"));
        em.clear();
    }

    @Test
    public void find_by_email_test() {
        // given
        String email = "이승민@gmail.com";

        // when
        Optional<User> userOP = userRepository.findByEmail(email);
        if (userOP.isEmpty()) {
            throw new Exception400(email, "사용자를 찾을 수 없습니다.");
        }
        User userPS = userOP.get();

        // then
        assertThat(userPS.getEmail()).isEqualTo("이승민@gmail.com");
    }

    @Test
    public void calculate_count_by_team_id_test() {
        // given
        Long teamId = 1L;

        // given2
        Team team = teamRepository.save(Team.builder()
                .teamName("개발팀")
                .build());
        userRepository.save(newUserWithTeam("이승민", team));
        userRepository.save(newUserWithTeam("최민식", team));

        // when
        int i = userRepository.calculateCountByTeamId(teamId);

        // then
        assertThat(i).isEqualTo(2);
    }
}
