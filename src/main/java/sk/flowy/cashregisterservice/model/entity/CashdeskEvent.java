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

@Entity
@Table(name = "smena")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CashdeskEvent implements Serializable {

    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "zaciatok")
    private Date startOfShift;

    @Column(name = "koniec")
    private Date endOfShift;

    @OneToMany(mappedBy = "cashdeskEvent", cascade = CascadeType.PERSIST)
    private List<CashInEvent> cashInEvents;

    @OneToMany(mappedBy = "cashdeskEvent", cascade = CascadeType.PERSIST)
    private List<CashOutEvent> cashOutEvents;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    @JsonIgnore
    private CashdeskUser cashdeskUser;
}
