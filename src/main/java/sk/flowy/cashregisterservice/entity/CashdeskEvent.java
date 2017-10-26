package sk.flowy.cashregisterservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name="zaciatok")
    private Date startOfShift;

    @Column(name="koniec")
    private Date endOfShift;

    @OneToOne(mappedBy = "cashdeskEvent", cascade = CascadeType.PERSIST)
    private CashInEvent cashInEvent;

    @OneToOne(mappedBy = "cashdeskEvent", cascade = CascadeType.PERSIST)
    private CashOutEvent cashOutEvent;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    @JsonIgnore
    private CashdeskUser cashdeskUser;
}
