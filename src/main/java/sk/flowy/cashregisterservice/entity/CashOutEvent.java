package sk.flowy.cashregisterservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "pokladna_vyber")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CashOutEvent {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_smena")
    @JsonIgnore
    private CashdeskEvent cashdeskEvent;

    @Column(name="hotovost_suma")
    private int cashBalance;

    @Column(name="stravne_listky_suma")
    private int gastroTicketsBalance;

    @Column(name="terminal_suma")
    private int terminalBalance;

    @Column(name="denna_uzavierka")
    private boolean dailyBalance;

    @Column(name="created_at")
    private Date createdAt;
}