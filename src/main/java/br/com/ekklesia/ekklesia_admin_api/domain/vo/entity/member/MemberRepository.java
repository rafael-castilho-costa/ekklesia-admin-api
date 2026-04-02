package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
