package sk.flowy.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;

public interface CashdeskEventRepository extends JpaRepository<CashdeskEvent,Long> {
}
