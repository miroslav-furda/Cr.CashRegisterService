package sk.flowy.cashregisterservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Wrapper for data received from client about daily and interval balance.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceWrapper implements Serializable {
    private static final long serialVersionUID = 161995340013282537L;

    private Long userId;
    private int cashBalance;
    private int gastroTicketsBalance;
    private int terminalBalance;
    private boolean endOfShift;
}
