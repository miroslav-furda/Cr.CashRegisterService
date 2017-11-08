package sk.codxa.cr.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskUser;

public interface CashDeskUserRepository extends JpaRepository<CashDeskUser, Long> {
}
