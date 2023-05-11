package smash.teams.be.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u join fetch u.team where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select count(*) from User u where u.team.id = :id")
    int calculateCountByTeamId(@Param("id") Long id);

    @Query("select u from User u where u.id = :id")
    User findUserById(@Param("id") Long id);

    @Query("select u from User u where u.name = :name")
    Optional<User> findByName(@Param("name") String name);

    @Query("select u from User u where u.email = :email")
    User findUserByEmail(@Param("email") String email);
}
