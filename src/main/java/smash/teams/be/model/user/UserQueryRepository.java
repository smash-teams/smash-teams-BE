package smash.teams.be.model.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {
    private final EntityManager em;
    private final int SIZE = 12; // 한 페이지 당 사용자 정보 최대 갯수

    public Page<User> findAll(int page) {
        int startPosition = page * SIZE;

        List<User> userListPS = em.createQuery("select u from User u join fetch u.team order by u.name")
                .setFirstResult(startPosition) // 시작 번호
                .setMaxResults(SIZE) // 개수
                .getResultList();

        Long totalCount = em.createQuery("select count(u) from User u", Long.class).getSingleResult();

        return new PageImpl<>(userListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<User> findAllByKeyword(String keyword, int page) {
        int startPosition = page * SIZE;

        List<User> userListPS = em.createQuery("select u from User u join fetch u.team where u.name like :keyword order by u.name")
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(startPosition)
                .setMaxResults(SIZE)
                .getResultList();

        Long totalCount = em.createQuery("select count(u) from User u where u.name like :keyword", Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getSingleResult();

        return new PageImpl<>(userListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<User> findAllByTeamId(Long id, int page) {
        int startPosition = page * SIZE;

        List<User> userListPS = em.createQuery("select u from User u join fetch u.team where u.team.id = :teamId order by u.name")
                .setParameter("teamId", id)
                .setFirstResult(startPosition)
                .setMaxResults(SIZE)
                .getResultList();

        Long totalCount = em.createQuery("select count(u) from User u where u.team.id = :teamId", Long.class)
                .setParameter("teamId", id)
                .getSingleResult();

        return new PageImpl<>(userListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<User> findAllByKeywordAndTeamId(Long id, String keyword, int page) {
        int startPosition = page * SIZE;

        List<User> userListPS = em.createQuery("select u from User u join fetch u.team where u.name like :keyword and u.team.id = :teamId order by u.name")
                .setParameter("keyword", "%" + keyword + "%")
                .setParameter("teamId", id)
                .setFirstResult(startPosition)
                .setMaxResults(SIZE)
                .getResultList();

        Long totalCount = em.createQuery("select count(u) from User u where u.name like :keyword and u.team.id = :teamId", Long.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setParameter("teamId", id)
                .getSingleResult();

        return new PageImpl<>(userListPS, PageRequest.of(page, SIZE), totalCount);
    }
}
