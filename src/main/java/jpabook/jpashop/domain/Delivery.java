package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    // 상태를 숫자로 주는 경우, 나중에 상태가 추가되었을 때 크게 문제가 된다.
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // ready, comp
}
