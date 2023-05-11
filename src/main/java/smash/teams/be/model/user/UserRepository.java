package smash.teams.be.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u join fetch u.team where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select count(*) from User u where u.team.id = :id")
    int calculateCountByTeamId(@Param("id") Long id);
}
