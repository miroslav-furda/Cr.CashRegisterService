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
}
