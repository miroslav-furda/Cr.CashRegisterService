package sk.flowy.cashregisterservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pokladna_vyber")
@NoArgsConstructor
@Getter
@Setter
public class CashOutEvent implements Serializable {

    private static final long serialVersionUID = -4142646286123281468L;

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_smena")
    @JsonIgnore
    private CashDeskEvent cashDeskEvent;

    @Column(name = "hotovost_suma")
    private int cashBalance;

    @Column(name = "stravne_listky_suma")
    private int gastroTicketsBalance;

    @Column(name = "terminal_suma")
    private int terminalBalance;

    @Column(name = "denna_uzavierka")
    private boolean dailyBalance;

    @Column(name = "created_at")
    private Date createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashOutEvent that = (CashOutEvent) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}