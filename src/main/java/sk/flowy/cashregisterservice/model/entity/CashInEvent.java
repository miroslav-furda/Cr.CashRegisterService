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
@Table(name = "pokladna_vklad")
@NoArgsConstructor
@Getter
@Setter
public class CashInEvent implements Serializable {

    private static final long serialVersionUID = -6801247041497273305L;

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_smena")
    @JsonIgnore
    private CashDeskEvent cashDeskEvent;

    @Column(name = "hotovost_suma")
    private int balance;

    @Column(name = "created_at")
    private Date createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashInEvent that = (CashInEvent) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
