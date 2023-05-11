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
import smash.teams.be.core.exception.Exception404;
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
<<<<<<< HEAD
        Team teamPS = teamRepository.save(newTeam("개발팀"));
        userRepository.save(newUserWithTeam("이승민", teamPS));
        userRepository.save(newUserWithTeam("송재근", teamPS));
=======
        userRepository.save(newUser("이승민"));
        userRepository.save(newUser("송재근"));

>>>>>>> 2ed0c4c (Feat: 이메일 중복확인 및 회원탈퇴 구현)
        em.clear();
    }

    @Test
    public void findByEmail_test() {
        // given
        String email = "이승민@gmail.com";

        // when
        User userPS = userRepository.findByEmail(email).orElseThrow();

        // then
        assertThat(userPS.getEmail()).isEqualTo("이승민@gmail.com");
    }

    @Test
    public void calculateCountByTeamId_test() {
        // given
        Long teamId = 1L;

        // given2
        Team teamPS = teamRepository.findByTeamName("개발팀").orElseThrow();
        userRepository.save(newUserWithTeam("이윤경", teamPS));
        userRepository.save(newUserWithTeam("최민식", teamPS));

        // when
        int i = userRepository.calculateCountByTeamId(teamId);

        // then
        assertThat(i).isEqualTo(4);
    }

}
