package smash.teams.be.model.schedule;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import smash.teams.be.core.dummy.DummyEntity;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


        User kimceo = newUserForRepoTest(null,"kimceo", "CEO");
        User kimmanager = newUserForRepoTest(team1,"kimmanager", "MANAGER");
        User kimdayoff = newUserForRepoTest(team2,"kimday","USER");
        User kimhalfoff = newUserForRepoTest(team2, "kimhalf", "USER");
        User kimshift = newUserForRepoTest(team1, "kimshift", "USER");
        userRepository.save(kimceo);
        userRepository.save(kimmanager);
        userRepository.save(kimdayoff);
        userRepository.save(kimhalfoff);
        userRepository.save(kimshift);

        Schedule schedule1 = newScheduleForRepoTest(kimceo, "DAYOFF","APPROVED","휴가");
        Schedule schedule2 = newScheduleForRepoTest(kimmanager, "HALFOFF","APPROVED","병가");
        Schedule schedule3 = newScheduleForRepoTest(kimdayoff,"DAYOFF","REJECTED","휴가");
        Schedule schedule4 = newScheduleForRepoTest(kimceo, "HALFOFF","APPROVED","개인사정");
        Schedule schedule5 = newScheduleForRepoTest(kimdayoff, "HALFOFF","APPROVED","개인사정");
        Schedule schedule6 = newScheduleForRepoTest(kimdayoff,"SHIFT","APPROVED","당직");
        Schedule schedule7 = newScheduleForRepoTest(kimhalfoff, "SHIFT","LAST","당직");
        Schedule schedule8 = newScheduleForRepoTest(kimhalfoff, "DAYOFF","LAST","휴가");
        Schedule schedule9 = newScheduleForRepoTest(kimmanager,"DAYOFF", "LAST","휴가");
        Schedule schedule10 = newScheduleForRepoTest(kimshift,"SHIFT", "LAST","당직");
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
        scheduleRepository.save(schedule3);
        scheduleRepository.save(schedule4);
        scheduleRepository.save(schedule5);
        scheduleRepository.save(schedule6);
        scheduleRepository.save(schedule7);
        scheduleRepository.save(schedule8);
        scheduleRepository.save(schedule9);
        scheduleRepository.save(schedule10);

        em.clear();
    }

    @Test
    public void find_Schedules_By_User_Id_test() {
        // given
        Long userId = 3L;

        // when
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserId(userId);


        // then
        assertThat(schedules.size()).isEqualTo(3);
        assertThat(schedules.get(0).getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getType()).isEqualTo("HALFOFF");
        assertThat(schedules.get(2).getId()).isEqualTo(6L);
        assertThat(schedules.get(0).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(1).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(2).getUser().getId()).isEqualTo(3L);
        assertThat(schedules.get(0).getReason()).isEqualTo("휴가");
        assertThat(schedules.get(1).getUser().getName()).isEqualTo("kimday");
        assertThat(schedules.get(2).getUser().getEmail()).isEqualTo("kimday@gmail.com");
        assertThat(schedules.get(0).getStatus()).isEqualTo("REJECTED");

    }

    @Test
    public void findSchedules_test() {
        // given

        // when
        List<Schedule> schedules = scheduleRepository.findSchedulesWithName();

        // then
        assertThat(schedules.size()).isEqualTo(10);
        assertThat(schedules.get(0).getId()).isEqualTo(1L);
        assertThat(schedules.get(1).getUser().getId()).isEqualTo(2L);
        assertThat(schedules.get(2).getStatus()).isEqualTo("REJECTED");
        assertThat(schedules.get(3).getType()).isEqualTo("HALFOFF");
        assertThat(schedules.get(4).getUser().getName()).isEqualTo("kimday");
        assertThat(schedules.get(5).getUser().getRole()).isEqualTo("USER");
        assertThat(schedules.get(6).getUser().getTeam().getTeamName()).isEqualTo("지원팀");
        assertThat(schedules.get(7).getReason()).isEqualTo("휴가");
        assertThat(schedules.get(8).getUser().getTeam().getTeamName()).isEqualTo("개발팀");
        assertThat(schedules.get(9).getUser().getEmail()).isEqualTo("kimshift@gmail.com");

    }

    @Test
    public void findSchedulesByTeamName_test(){
        // given

        // when
        List<Schedule> schedules = scheduleRepository.findSchedulesByTeamId(1L);

        // then
        assertThat(schedules.size()).isEqualTo(3);
        assertThat(schedules.get(0).getId()).isEqualTo(2L);
        assertThat(schedules.get(1).getId()).isEqualTo(9L);
        assertThat(schedules.get(2).getId()).isEqualTo(10L);
        assertThat(schedules.get(0).getUser().getId()).isEqualTo(2L);
        assertThat(schedules.get(1).getUser().getId()).isEqualTo(2L);
        assertThat(schedules.get(2).getUser().getId()).isEqualTo(5L);
        assertThat(schedules.get(0).getUser().getName()).isEqualTo("kimmanager");
        assertThat(schedules.get(1).getUser().getName()).isEqualTo("kimmanager");
        assertThat(schedules.get(2).getUser().getName()).isEqualTo("kimshift");
        assertThat(schedules.get(0).getUser().getTeam().getTeamName()).isEqualTo("개발팀");
        assertThat(schedules.get(1).getUser().getTeam().getTeamName()).isEqualTo("개발팀");
        assertThat(schedules.get(2).getUser().getTeam().getTeamName()).isEqualTo("개발팀");
        assertThat(schedules.get(2).getUser().getTeam().getId()).isEqualTo(1L);
        assertThat(schedules.get(2).getUser().getTeam().getId()).isEqualTo(1L);
        assertThat(schedules.get(2).getUser().getTeam().getId()).isEqualTo(1L);
    }

    @Test
    public void findSchedulesWithName_test() throws Exception {
        // given

        // when
        List<Schedule> schedulesListPS = scheduleRepository.findSchedulesWithName();

        // then
        assertEquals(10, schedulesListPS.size());
        assertTrue(schedulesListPS.stream().allMatch(schedule -> schedule.getUser() != null));
    }

    @Test
    public void findScheduleByScheduleId_test(){
        // given
//        Schedule schedule8 = newScheduleForRepoTest(kimhalfoff, "DAYOFF","LAST","휴가");
        // when
        Schedule schedule = scheduleRepository.findScheduleByScheduleId(8L);
        // then
        assertThat(schedule.getId()).isEqualTo(8L);
        assertThat(schedule.getType()).isEqualTo(Type.DAYOFF.getType());
        assertThat(schedule.getStatus()).isEqualTo(Status.LAST.getStatus());
        assertThat(schedule.getUser().getName()).isEqualTo("kimhalf");
        assertThat(schedule.getUser().getRemain()).isEqualTo(20);
    }
}
