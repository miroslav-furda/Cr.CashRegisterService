package sk.codxa.cr.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskEvent;

public interface CashDeskEventRepository extends JpaRepository<CashDeskEvent,Long> {
}
