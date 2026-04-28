package br.com.ekklesia.ekklesia_admin_api.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByPersonaId(Integer personaId);

    boolean existsByChurchId(Long churchId);

    @EntityGraph(attributePaths = {"church", "persona", "persona.church", "roles"})
    Optional<User> findAuthByEmail(String email);

    @EntityGraph(attributePaths = {"church", "persona", "persona.church", "roles"})
    Optional<User> findDetailedById(Integer id);

    @EntityGraph(attributePaths = {"church", "persona", "persona.church", "roles"})
    List<User> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"church", "persona", "persona.church", "roles"})
    List<User> findAllByChurchIdOrderByIdAsc(Long churchId);
}
