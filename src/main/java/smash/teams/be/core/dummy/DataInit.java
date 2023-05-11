package smash.teams.be.core.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserRepository;

@Component
public class DataInit extends DummyEntity {

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, TeamRepository teamRepository) {
        return args -> {

            Team admin = teamRepository.save(newTeam("admin")); // admin 소속팀 db에 추가
            userRepository.save(newAdminWithTeam("admin", admin)); // admin db에 추가

        };
    }
}
