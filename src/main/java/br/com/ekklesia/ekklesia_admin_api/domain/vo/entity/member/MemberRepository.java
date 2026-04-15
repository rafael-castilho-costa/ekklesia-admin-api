package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    @Override
    @EntityGraph(attributePaths = {"persona", "persona.church"})
    java.util.Optional<Member> findById(Integer integer);

    @Override
    @EntityGraph(attributePaths = {"persona", "persona.church"})
    java.util.List<Member> findAll();
}
