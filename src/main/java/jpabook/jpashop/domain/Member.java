package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // 여기에 값을 넣는 다고 해서 반대편이 변경되지는 않는다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
