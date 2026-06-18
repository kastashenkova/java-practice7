# Практика 7. Бібліотеки тестування
## Завдання

1. Створити тести з використанням `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `when(...).thenReturn(...)`. Не менше ніж 3 різних бізнес сценаріїв.
2. Створити тести з перевірками void-методів — `verify`, `times`, `never` (щонайменше по 1 тесту на кожну з цих умов).
3. Створити тести з AssertJ SoftAssertions (щонайменше 1).
4. Створити тести з AssertJ перевірки списків (щонайменше 3 різні типи перевірок).
5. Використати PIT-бібліотеку мутаційного тестування. Створити тест, який впаде від мутації та окремо його виправлену версію. (Логіка має містити кілька гілок (if/else), інакше не буде, що мутувати).
   
## Реалізація
Тема проєкту: створення завдання в гугл-календарі.

## Покриття тестами
### Запуск
- `mvn clean test`
- `mvn jacoco:report`
- `mvn org.pitest:pitest-maven:mutationCoverage`
- `mvn clean verify sonar:sonar "-Dsonar.projectKey=[your_project]" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=[your_token]"`

**JaCoCo report**

**Sonar report**

**PIT report**
