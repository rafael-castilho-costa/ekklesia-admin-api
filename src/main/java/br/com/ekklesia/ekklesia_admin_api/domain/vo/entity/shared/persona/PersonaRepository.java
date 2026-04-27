package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    @EntityGraph(attributePaths = {"church"})
    Optional<Persona> findByIdAndChurchId(Integer id, Long churchId);

    @EntityGraph(attributePaths = {"church"})
    List<Persona> findAllByChurchId(Long churchId);
}
