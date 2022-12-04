# 지연 로딩과 조회 성능 최적화 ( 컬렉션 조회 )

- ## 주문 조회 V1 : 엔티티 직접 노출 ( 마찬가지로 금지, 실습에서만 사용 )
    - 한 주문에 여러 아이템을 가지고 있기 때문에 일대다 매핑이 된다. 이전 간단 조회에서는 orderItem에 대한 정보가 없었지만 여기서는 일대다 정보에 대한 내용도 다룬다.
    - 한 주문에서 여러 아이템을 주문 할 수 있기 때문에 주문에서 아이템들을 리스트 형태로 받은 후에 람다식으로 각 아이템을 참조해서 LAZY 초기화를 해준다.

        ```java
        @GetMapping("/api/v1/orders")
        public List<Order> ordersV1() {
            List<Order> all = orderRepository.findAllByString(new OrderSearch());
            for (Order order : all) {
                order.getMember().getName(); // LAZY 초기화
                order.getDelivery().getAddress(); // LAZY 초기화
        
                List<OrderItem> orderItems = order.getOrderItems();
                orderItems.stream().forEach(o->o.getItem().getName()); // LAZY 초기화
            }
            return all;
        }
        ```

- ## 주문 조회 V2 : 엔티티를 DTO로 변환 ( 지연 로딩으로 인한 쿼리 대량 발생 , N + 1 문제 )
    - Dto에 Getter 가 없으면 오류가 발생한다.
    - OrderItem 같은 경우에는 컬렉션 조회에 해당하기 때문에 LAZY 초기화를 해줘야 한다.
    - Dto 안에 엔티티가 있으면 안된다. 래핑 하는 것도 안된다. 엔티티에 대한 의존을 끊어내야 한다.
    - 컬렉션을 조회하기 때문에 주문 하나를 조회할 때 주문에 포함되는 아이템을 각각 조회한다. → 쿼리가 굉장히 많이 발생함 → 최적화가 필요해진다. ( 만약 같은 아이템을 주문하여 영속성 컨텍스트에 이미 포함되어 있는 경우에는 쿼리를 발생하지 않는다. )
- 주문 조회 V3 : 엔티티를 DTO로 변환 + 페치 조인 최적화 ( 지연 로딩 문제 해결, 페이징 불가능 )
    - 일대다 매핑이 있어서 조인하게 되면 DB에서 데이터의 수가 증가하게 된다. 결과로 동일한 데이터의 조회수가 증가하게 된다. 이를 `distinct` 키워드를 사용하여 중복을 제거한다. → **단점으로는 페이징이 불가능하다. ( 페이징을 실시하면 메모리에서 페이징을 해버린다.. 매우 큰 일 발생 ) , `실무에서는 무조건 절대 주의해야 한다.`**
        - DB에서 `distinct` 를 실행해보면 실제로 중복데이터를 제거하지는 않는 모습이다. 왜냐하면 자세히 살펴보면 모든 값이 정확히 같지는 않기 때문이다. 그러나 주문의 번호가 같은 경우에 애플리케이션에 중복을 제거해서 가져온다.
        - **컬렉션 페치 조인은 1개만 사용할 수 있다.** 컬렉션 둘 이상에 페치 조인을 하게 되면 데이터가 부정확하게 조회될 수 있다.
- ## **주문 조회 V3.1: 엔티티를 DTO로 변환 - 페이징 한계 돌파 ( 컬렉션에 대한 지연로딩은 그래도 유지한다. 대신 지연 로딩 성능 최적화를 실시한다. )**
    - 컬렉션을 페치조인 하면 일대다 조인이 발생하여 **데이터가 예측할 수 없이 증가**하기 때문에 페이징이 불가능하다. 일대다 매핑에서 적은 것을 기준으로 페이징해야 하는데 데이터에는 많은 것을 기준으로 row를 만들어내기 때문이다. ( 주문을 기준으로 하고 싶은데 , 주문 아이템이 기준이 되어버림 )
    - 해결방법
        1. ToOne 관계를 모두 페치조인 한다. → ToOne 관계는 row를 증가시키지 않는다.
        2. 컬렉션은 지연 로딩으로 조회한다.
        3. 지연 로딩 성능 최적화를 위해 `**hibernate.default_batch_fetch_size**` , `@BatchSize` 를 적용한다. `hibernate.default_batch_fetch_size` → 글로벌 설정 , `@BatchSize` → 개별 최적화 → 이 옵션들을 사용하면  컬렉션이나 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회한다. → 쿼리 호출의 양이 효율적으로 줄어든다.
            1. `@BatchSize` 는 컬렉션인 경우에는 필드에 , ToOne인 경우에 class에 어노테이션을 추가하면 된다.
        4. 이것보다 더 효율적이고 빠르게 처리해야 한다? → 비 관계형 데이터베이스인 Redis 혹은 MongoDB를 사용해야 한다.
        - V3과 V3.1은 페이징의 가능 여부에 차이가 있다. 추가로 컬렉션에 페치 조인을 적용하지 않기 때문에 가져오는 데이터의 양에도 차이가 있다. 환경에 따라 두 개를 적절하게 선택하여 사용하면 된다. ( V3 은 DB에 접근을 적게 함 + 데이터 많음 , V3.1은 DB 에 접근을 조금 더 많이 함 + 데이터 적음 )
    - 정리
        - ToOne 매핑 관계는 페치조인으로 쿼리 수를 줄인다.
        - ToMany ( 컬렉션 ) 매핑 관계는 하이버네이트 배치 페치 사이즈 옵션을 사용하여 최적화한다. ( size는 100~1000 개 사이로 권장한다. 사이즈가 커질수록 애플리케이션에 순간 부하가 커진다. DB와 애플리케이션의 부하를 고려하여 결정하면 된다. )
- ## 주문 조회 V4: JPA에서 DTO 직접 조회 ( N + 1 문제 )
    - JQPL을 만들더라도 컬렉션을 select로 가져와서 dto로 만들 때 생성자로 넘겨줄 수 가 없다.
    - 따라서 먼저 ToOne 관계인 주문에 대한 정보를 모두 가져온 후에 ToMany 관계인 주문 아이템에 대해서 반복문을 실행해서 각각 정보를 가져온다. → 쿼리가 N +1 번 발생한다. 그러나 row가 증가하지는 않는다.
    - 별도의 메서드가 필요하고 , 쿼리가 많이 발생하는 단점이 있다.

        ```java
        public List<OrderQueryDto> findOrderQueryDto() {
            List<OrderQueryDto> result = findOrders();
        
            result.stream().forEach(o ->{
                List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
                o.setOrderItems(orderItems);
            });
        
            return result;
        }
        ```

- ## 주문 조회 V5: JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화 ( 쿼리 문제 해결 )
    - Order 조회 한 번 , OrderItem과 Item은 join 해서 한 번 쿼리 실행
    - ToOne인 Order를 먼저 조회하고 여기서 얻은 식별자인 OrderId를 이용하여 ToMany 관계인 OrderItem을 in 쿼리를 이용해서 한 꺼번에 조회한다.
    - 이를 MAP으로 메모리에 정리하면 매칭 성능이 O(1)이기 때문에 굉장히 좋다.

    ```java
    public List<OrderQueryDto> findAllByDto_optimaiztion() {
        List<OrderQueryDto> result = findOrders();
    
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    
        // 쿼리 한 번으로 모두 가져온다. 이후에 메모리에서 foreach 를 통해서 세팅한다.
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select  new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
    
        // OrderId 가 key 가 되는 map이 된다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDto -> OrderItemDto.getOrderId()));
    
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }
    ```

- ## 주문 조회 V6: JPA에서 DTO로 직접 조회, 플랫 데이터 최적화 ( 쿼리 단 한 번.. , 페이징 불가능 )
    - DB에서 모두 join해서 한 번에 가져온다. 이를 미리 맞춰서 Dto로 만든 후에 해당 Dto에 담아서 반환한다.
    - 페이징이 가능한가? → 가능하다. 그러나 클라이언트가 원하는 페이징이 아니라 DB에서 뱉어내는 대로 ( OrderId 기준 페이징이 아니라 각 row 마다 데이터가 반환된다. )

        ```java
        public List<OrderFlatDto> findAllByDto_flat() {
            return em.createQuery(
                            "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id , m.name , o.orderDate , o.status , d.address , i.name , oi.orderPrice , oi.count)" +
                                    " from Order o" +
                                    " join o.member m" +
                                    " join o.delivery d" +
                                    " join o.orderItems oi" +
                                    " join oi.item i", OrderFlatDto.class
                    )
                    .getResultList();
        }
        ```

    - 만약 기존 API 스펙에 맞춰서 만들고 싶다? 해당 dto를 메모리에서 중복을 제거해서 반환하면 된다. OrderFlatDto → OrderQueryDto로 변환 , 이전처럼 OrderItemQueryDto를 매핑하고 다시 최종적으로 OrderQueryDto로 만들어서 반환한다.. → 작업량이 상당~
    - 결과적으로 애플리케이션에 전달하는 데이터에는 중복데이터가 상당히 추가되기 때문에 V5보다 더 성능이 안 좋을 수 있다. ( 쿼리는 적게 생성되지만.. )
        - `@EqualsAndHashCode of("orderid")` 를 OrderQueryDto에 추가하여 equals와 hash 함수를 오버라이딩 하여 매핑한 후 그룹화 할 때 , 별도로 생성되지 않고 그룹화된다.