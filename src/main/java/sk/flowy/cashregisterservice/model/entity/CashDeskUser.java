package sk.flowy.cashregisterservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "uzivatel")
@NoArgsConstructor
@Getter
@Setter
public class CashDeskUser implements Serializable {

    private static final long serialVersionUID = -174529975202704202L;

    @GeneratedValue
    @Id
    private Long id;

    @OneToMany(mappedBy = "cashDeskUser")
    private List<CashDeskEvent> cashDeskEvents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDeskUser that = (CashDeskUser) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
