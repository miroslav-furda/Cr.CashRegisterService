package sk.flowy.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.flowy.cashregisterservice.model.entity.CashdeskUser;

public interface CashdeskUserRepository extends JpaRepository<CashdeskUser, Long> {
}
