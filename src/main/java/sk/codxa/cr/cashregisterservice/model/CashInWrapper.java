package sk.codxa.cr.cashregisterservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashInWrapper {

    private Long userId;
    private Integer balance;
}
