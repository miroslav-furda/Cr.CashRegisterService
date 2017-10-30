package sk.flowy.cashregisterservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Wrapper for data received from client about daily and interval balance.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceWrapper {

    private Long userId;
    private int cashBalance;
    private int gastroTicketsBalance;
    private int terminalBalance;
    private boolean endOfShift;
}
