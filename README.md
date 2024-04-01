# Junit Bank App

### Jpa LodcalDateTime 자동으로 생성하는 법
- @EnableJpaAuditing (Main 클래스에)
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스에)
```java
    @CreatedDate // insert 할 때
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // insert, update 할 때
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```

### @NoArgaConstructor 사용 이유
- 모든 엔티티는 기본 생성자를 가져야 함!
  - JPA 구현체가 객체를 프록시로 생성하거나, DB로부터 조회하여 객체를 복구할 때 필요
- access level은 protected로 설정하자
  - private으로 하면 지연 로딩 시 JPA 구현체가 프록시 생성 못함

### Jnuit 테스트 
- Mock 환경에서 테스트 할 시 WebEnvironment.MOCK으로 설정해주자
``` java
@AutoConfigureMockMvc //Mock(가짜) 환경에 MockMv가 등록됨
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTest {

    //가짜 환경에 등록된 MockMvc를 DI
    @Autowired
    private MockMvc mvc;
```

### 서버는 일관성 있게 에러가 리턴 되어야 한다.
- 내가 모르는 에러가 프론트한테 날라가지 않게 직접 다 제어하자 (CustomResponseUtil로 관리)
  - 인증 실패 예외 처리 커스텀 
  - 권한 실패 예외 처리 커스텀