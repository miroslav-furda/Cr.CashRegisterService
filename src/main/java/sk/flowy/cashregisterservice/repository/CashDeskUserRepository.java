package sk.flowy.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;

public interface CashDeskUserRepository extends JpaRepository<CashDeskUser, Long> {
}
