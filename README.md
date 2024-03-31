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