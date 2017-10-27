package sk.flowy.cashregisterservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "smena")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CashdeskEvent {

    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "zaciatok")
    private Date startOfShift;

    @Column(name = "koniec")
    private Date endOfShift;

    @OneToMany(mappedBy = "cashdeskEvent", cascade = ALL)
    private List<CashInEvent> cashInEvents;

    @OneToMany(mappedBy = "cashdeskEvent", cascade = ALL)
    private List<CashOutEvent> cashOutEvents;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    @JsonIgnore
    private CashdeskUser cashdeskUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashdeskEvent that = (CashdeskEvent) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
