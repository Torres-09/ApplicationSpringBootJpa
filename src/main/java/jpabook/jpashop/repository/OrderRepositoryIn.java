package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepositoryIn extends JpaRepository<Order, Long> {

    @Query("select o from Order o" + " join fetch o.member m" + " join fetch o.delivery d")
    List<Order> findAllWithMemberAndDelivery(Pageable pageable);

    @Query("select o from Order o join fetch o.member m join fetch o.delivery d")
    List<Order> findAllWithMemberDelivery();
}
