package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    @Override
    @EntityGraph(attributePaths = {"church"})
    java.util.Optional<Persona> findById(Integer integer);

    @Override
    @EntityGraph(attributePaths = {"church"})
    java.util.List<Persona> findAll();
}
