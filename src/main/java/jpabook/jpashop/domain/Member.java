package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    /**
     * 엔티티는 순수하게 유지하는 것이 가장 좋다고 배웠고 나 역시 그렇다고 생각한다.
     * validation을 위한 어노테이션을 이곳에 추가하기 보다는 회원가입 등의 api를 위해 사용되는 Dto에 validation 과 관련된 어노테이션을
     * 추가하는 것이 적절해보인다.
     */
    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    // 여기에 값을 넣는 다고 해서 반대편이 변경되지는 않는다.
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
