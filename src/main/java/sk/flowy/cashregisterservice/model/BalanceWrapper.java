package sk.flowy.cashregisterservice.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Wrapper for data received from client about daily and interval balance.
 */
@Data
public class BalanceWrapper implements Serializable {
    private static final long serialVersionUID = 161995340013282537L;

    private Long userId;
    private int cashBalance;
    private int gastroTicketsBalance;
    private int terminalBalance;
    private boolean endOfShift;
}
