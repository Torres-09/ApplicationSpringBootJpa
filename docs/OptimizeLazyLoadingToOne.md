# 지연 로딩과 조회 성능 최적화 ( XtoOne )

- ## 간단한 주문 조회 V1: 엔티티를 직접 노출 (  절대금지 , 실습에서만 한다. )
    - 주문 API , 지연로딩 때문에 발생하는 성능 문제 해결 ( 지연 로딩 해결 문제는 실무에서 매우매우 중요하다. )
    - 양방향 연관관계의 경우 서로 계속 참조하면서 Json을 계속해서 만들어낸다.
    - → 이것을 해결하기 위해서는 `@JsonIgnore`  어노테이션을 이용해서 한 쪽에서는 차단해주어야 한다. ( 엔티티를 직접 노출하면 안 좋다 **결국 entity를 수정**해야 하기 때문에.. )
    - → fetch lazy 를 사용하기 때문에 지연 로딩이 발생한다. 따라서 실제 엔티티를 DB에서 조회하는 것이 아니라 **프록시 객체가 존재**한다. jackson 라이브러리에서는 프록시 객체를 json으로 생성할 수 없기 때문에 예외가 발생한다.
    - → `Hibernate5Module` 라이브러리를 추가해서 스프링 빈으로 등록하면 초기화 된 프록시 객체만 노출하고 초기화 되지 않은 프록시 객체는 노출하지 않는다.
    - → `Hibernate5Module` 빈을 등록할 때 강제 지연 로딩 설정을 추가하면 양방향 연관관계를 계속 로딩하게 한다. ( 이 옵션으로 인해서 API 스펙보다 추가 노출이 발생한다. )
    - **지연로딩을 하지 않으면 되는 게 아닐까? (즉시 로딩EAGER)→ 전혀 아니다.**
        - 즉시 로딩 때문에 연관관계가 필요 없는 경우에도 데이터를 항상 조회해서 성능 문제가 발생한다. → 즉시 로딩은 성능 튜닝이 매우 어려워진다. default를 지연로딩으로 하고 성능 최적화가 필요한 경우에는 **페치 조인(fetch join)을 사용한다.**

            ```java
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
            ```

        - 다음과 같이 이름이나 주소를 직접 조회하게 되면 Lazy가 강제 초기화 되어서 정보가 정상적으로 출력된다.
- ## 간단한 주문 조회 V2 : 엔티티를 DTO로 변환 ( 지연 로딩으로 인한 쿼리 대량 발생 , N+1 문제 )
    - List로 반환하는 것이 아니라 Result 클래스를 별도로 만들어서 반환한다.
    - address 같은 경우는 valueObject라 한다. entity가 아니다.
    - **Lazy Loading으로 인해서 데이터 쿼리가 너무 많이 발생**하는 문제가 있다. order(N개 row) , member(N), delivery(N) 테이블을 조회해야 하는 상황이다. → order를 한 번 조회하고 order의 개수인 N개만큼 멤버와 딜리버리 테이블을 조회한다. → **지연 로딩은 영속성 컨텍스트에서 1차 조회**하므로 , 이미 조회된 경우 쿼리를 생략한다. ( 캐시에 있는 경우 ) ( 만약 주문에 대한 회원이 같다면? 생략한다는 얘기임 )
- ## 간단한 주문 조회 V3 : 엔티티를 DTO로 변환 - 페치 조인 최적화

    ```java
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }
    ```

    - order와 member , delivery 테이블을 fetch join을 통해서 한번에 가져온다. **fetch는 sql에는 없고 jpql에 있는 문법이다. 실무에서 굉장히 많이 쓰는 것이므로 이해를 잘 해야 한다.**
    - 가져오는 데이터의 양은 크게 차이 없으나 DB에 접근하는 코스트 자체가 많이 발생하기 때문에 접근하는 횟수를 줄이는 게 중요하다.
    - v2와 v3는 완벽하게 동일한 값을 return 한다. 그러나 지연로딩이 발생하지 않아서 v3가 성능이 더 좋다.

- ## 간단한 주문 조회 V4 : JPA에서 DTO로 바로 조회 ( JPQL 사용 )
    - 기존에는 엔티티를 조회한 후 DTO로 변환을 했다. 이것을 DTO로 바로 조회하는 것으로 변경한다.
        - 컨트롤러 → 서비스 → 리포지토리 or 컨트롤러 → 리포지토리 와 같은 식으로 한 방향으로 설계를 해야한다. ( 막간 )
    - 원래 엔티티는 Dto에 매핑될 수 없다. entity 나 임베디드 타입을 매핑할 수 있다.

        ```java
        public List<OrderSimpleQueryDto> findOrderDtos() {
            return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                            " from Order o" +
                    " join o.member m" +
                    " join o.delivery d", OrderSimpleQueryDto.class)
                    .getResultList();
        }
        ```

    - 다음과 같은 JPQL을 이용해서 필요한 정보인 dto를 조회할 수 있다. 필요하지 않은 정보를 가져오지 않기 때문에 네트워크 사용량을 줄일 수 있다. ( 생각보다는 미비하다.. )
    - V3 과 V4 는 우열을 가리기 어려운 Trade Off 관계가 있다. V3이 V4 보다 재사용성이 높다. ( 가지고 온 데이터가 전체이기 때문에 다른 api에 캐시에 있는 값들을 사용가능하다. 그러나 V4의 데이터는 전체가 아니기 때문에 다른 api의 스펙과 일치하지 않으면 사용이 불가능하다. )
    - V4는 그리고 쿼리 만드는 게 조금 더 힘들다. 또한 API에 따라 조회하는 dto가 바뀌기 때문에 기존에 api에 따라 출력하는 value가 바뀌는 문제가 발생한다. ( api 가 변경되면 출력 class도 변경해야 함. ) **V4용 repository를 별도로 생성해서 관리한다. → 유지보수 관점에서 순수한 리포지토리는 별도로 관리한다.**
    - 쿼리 방식 선택 권장 순서
        1. 엔티티를 DTO로 변환하는 방법을 기본적으로 선택
        2. 성능 이슈가 있다면 페치 조인으로 성능 최적화를 실시
        3. 그래도 성능 이슈가 남으면 DTO로 직접 조회하는 방식을 실시
        4. 마지막 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용해서 SQL을 작성한다.