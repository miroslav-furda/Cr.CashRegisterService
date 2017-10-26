package sk.flowy.cashregisterservice.model;

import lombok.Data;

@Data
public class CashInWrapper {

    private Long userId;
    private Integer balance;
    private Long cashdeskEventId;
}
