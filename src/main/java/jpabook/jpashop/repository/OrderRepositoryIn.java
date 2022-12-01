package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepositoryIn extends JpaRepository<Order, Long> {

    List<Order> findAllByDeliveryAndMember(Pageable pageable);
}
