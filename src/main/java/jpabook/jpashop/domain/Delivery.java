package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    // 상태를 숫자로 주는 경우, 나중에 상태가 추가되었을 때 크게 문제가 된다.
    /**
     * ready or comp
     */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // ready, comp
}
