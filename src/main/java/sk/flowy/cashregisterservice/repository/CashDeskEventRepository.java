package sk.flowy.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;

public interface CashDeskEventRepository extends JpaRepository<CashDeskEvent,Long> {
}
