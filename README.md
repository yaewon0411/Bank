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

### @NoArgsConstructor 사용 이유
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

### Spring AOP
  - **공통으로 사용되는 코드를 분리하여 관리** -> 코드 재사용성 관리 편의성 향상
  - @Aspect: : 클래스가 AOP 관점을 정의하고 있음을 나타내는 어노테이션
  - @Pointcut : 특정 조건에 맞는 조인 포인트(프로그램 실행 중 특정 위치)를 식별하는 데 사용
  - @Advice : 특정 조인 포인트에서 Aspect에 의해 취해질 조치. 종류로는 Before, After, AfterReturning, AfterThrowing, Around 등이 있음
    - @Around : 정의된 포인트컷에 대해 실제 메서드 전후로 추가 로직을 실행하도록 지정
  - ex) validationAdvice 메서드에서 컨트롤러 메서드의 매개변수들을 순회하며 BindingResult 타입 인스턴스를 찾으며 유효성 검사 수행

### 컨트롤러는 DTO의 유효성 검사를 수행해야 한다!!
  - 서비스는 유효성 검사를 하지 않기 때문에 유효성 검증이 안된 DTO를 레포지토리에서 엔티티로 바꿔서 DB에 넣으면 DataException이 발생한다
    - ex) User의 email 필드가 20자 제한 걸려 있는데 컨트롤러가 유효 검사를 안하면 DB 에러 발생
  - @Valid, @Validated, 정규 표현식을 사용한 검증 등을 통해 유효성 검사 가능

### DTO 정규 표현식 검증 적용하기
  - @Pattern : 문자열 필드가 정규 표현식에 정의된 패턴과 일치하는 지 검사하는 데 사용. 특정 필드에 대한 입력 형식을 엄격하게 제어 가능
```java
@Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
private String fullName;
```
  - regexp : 검사할 문자열이 일치해야 하는 정규 표현식
  - message : 유효성 검사를 통과하지 못할 경우 반환될 에러 메시지