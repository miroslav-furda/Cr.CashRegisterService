package sk.flowy.cashregisterservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "uzivatel")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CashdeskUser {

    @GeneratedValue
    @Id
    private Long id;

    @OneToMany(mappedBy = "cashdeskUser")
    private List<CashdeskEvent> cashdeskEvents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashdeskUser that = (CashdeskUser) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
