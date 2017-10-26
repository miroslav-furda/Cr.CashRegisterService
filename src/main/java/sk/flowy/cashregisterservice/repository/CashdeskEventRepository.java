package sk.flowy.cashregisterservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;

public interface CashdeskEventRepository extends JpaRepository<CashdeskEvent,Long> {
}
