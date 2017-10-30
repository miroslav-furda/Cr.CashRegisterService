package sk.flowy.cashregisterservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "smena")
@NoArgsConstructor
@Getter
@Setter
public class CashDeskEvent implements Serializable {

    private static final long serialVersionUID = 2247811483967735395L;

    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "zaciatok")
    private Date startOfShift;

    @Column(name = "koniec")
    private Date endOfShift;

    @OneToMany(mappedBy = "cashDeskEvent", cascade = ALL)
    private List<CashInEvent> cashInEvents;

    @OneToMany(mappedBy = "cashDeskEvent", cascade = ALL)
    private List<CashOutEvent> cashOutEvents;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    @JsonIgnore
    private CashDeskUser cashDeskUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDeskEvent that = (CashDeskEvent) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
