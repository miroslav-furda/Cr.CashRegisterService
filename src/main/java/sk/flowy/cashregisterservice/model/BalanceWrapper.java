package sk.flowy.cashregisterservice.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BalanceWrapper implements Serializable {

    private Long userId;
    private int cashBalance;
    private int gastroTicketsBalance;
    private int terminalBalance;
}
