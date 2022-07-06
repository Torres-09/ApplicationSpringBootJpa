package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Joined 는 가장 정규화된 스타일, SingleTable 은 한 테이블에 모두 모으는 것, TablePerClass 는 클래스마다 테이블로 나누는 전략
@DiscriminatorColumn(name = "dtype")
// 상품별로 구분.
@Getter
@Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

}
