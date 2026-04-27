package br.com.ekklesia.ekklesia_admin_api.finance;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Integer> {

    @EntityGraph(attributePaths = "church")
    List<FinanceTransaction> findAllByChurchIdOrderByTransactionDateDescIdDesc(Long churchId);

    @EntityGraph(attributePaths = "church")
    Optional<FinanceTransaction> findByIdAndChurchId(Integer id, Long churchId);
}
