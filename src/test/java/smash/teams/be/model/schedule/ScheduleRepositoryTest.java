package smash.teams.be.model.schedule;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class ScheduleRepositoryTest extends DummyEntity {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE schedule_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE team_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();

        Team team1 = newTeamForRepoTest("개발팀");
        Team team2 = newTeamForRepoTest("지원팀");
        teamRepository.save(team1);
        teamRepository.save(team2);

        User kimceo = newUserForRepoTest(null,"kimceo");
        User kimmanager = newUserForRepoTest(team1,"kimmanager");
        User kimdayoff = newUserForRepoTest(team2,"kimdayoff");
        userRepository.save(kimceo);
        userRepository.save(kimmanager);
        userRepository.save(kimdayoff);

        Schedule schedule1 = newScheduleForRepoTest(kimceo);
        Schedule schedule2 = newScheduleForRepoTest(kimmanager);
        Schedule schedule3 = newScheduleForRepoTest(kimdayoff);
        Schedule schedule4 = newScheduleForRepoTest(kimceo);
        Schedule schedule5 = newScheduleForRepoTest(kimdayoff);
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
        scheduleRepository.save(schedule3);
        scheduleRepository.save(schedule4);
        scheduleRepository.save(schedule5);

        em.clear();
    }

    @Test
    public void find_Schedules_By_User_Id_test() {
        // given
        Long userId = 3L;

        // when
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);


        // then
        assertThat(schedules.size()).isEqualTo(2);
        assertThat(schedules.get(0).getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getId()).isEqualTo(5L);
        assertThat(schedules.get(0).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(0).getUser().getName()).isEqualTo("kimdayoff");
        assertThat(schedules.get(1).getUser().getName()).isEqualTo("kimdayoff");
        assertThat(schedules.get(0).getUser().getTeam().getTeamName()).isEqualTo("지원팀");

    }

    @Test
    public void findSchedules_test() {
        // given
        Long userId = 3L;

        // when
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);


        // then
        assertThat(schedules.size()).isEqualTo(2);
        assertThat(schedules.get(0).getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getId()).isEqualTo(5L);
        assertThat(schedules.get(0).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(0).getUser().getName()).isEqualTo("kimdayoff");
        assertThat(schedules.get(1).getUser().getName()).isEqualTo("kimdayoff");
        assertThat(schedules.get(0).getUser().getTeam().getTeamName()).isEqualTo("지원팀");

    }
}
