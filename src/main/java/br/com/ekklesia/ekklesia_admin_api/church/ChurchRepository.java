package br.com.ekklesia.ekklesia_admin_api.church;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurchRepository extends JpaRepository<Church, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    java.util.Optional<Church> findByCnpj(String cnpj);
}
