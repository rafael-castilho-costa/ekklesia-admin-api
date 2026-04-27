package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    @EntityGraph(attributePaths = {"persona", "persona.church"})
    Optional<Member> findByIdAndPersonaChurchId(Integer id, Long churchId);

    @EntityGraph(attributePaths = {"persona", "persona.church"})
    List<Member> findAllByPersonaChurchId(Long churchId);

    Optional<Member> findByPersonaIdAndPersonaChurchId(Integer personaId, Long churchId);
}
