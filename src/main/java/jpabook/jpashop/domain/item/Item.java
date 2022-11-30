package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * manyToMany 관계는 실무에서는 절대 사용하지 않는다. 중간에 각각을 매핑해주는 테이블이 있어야 하는데 여기서는
     * manyToMany 매핑을 보여주기 위해서 구현한 내용이다.
     */
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 데이터를 가지고 있는 쪽에서 비즈니스 로직을 작성하는 것이 객체 지향 적이다. 관리하기가 용이하다.
    // setter 를 가지고 변경하는 것보다 더 좋다.

    //재고 수량 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        // check
        int resultStock = this.stockQuantity - quantity;
        if (resultStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = resultStock;
    }
}
