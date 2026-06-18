# Практика 7. Бібліотеки тестування
## Завдання

1. Створити тести з використанням `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `when(...).thenReturn(...)`. Не менше ніж 3 різних бізнес сценаріїв.
2. Створити тести з перевірками void-методів — `verify`, `times`, `never` (щонайменше по 1 тесту на кожну з цих умов).
3. Створити тести з AssertJ SoftAssertions (щонайменше 1).
4. Створити тести з AssertJ перевірки списків (щонайменше 3 різні типи перевірок).
5. Використати PIT-бібліотеку мутаційного тестування. Створити тест, який впаде від мутації та окремо його виправлену версію. (Логіка має містити кілька гілок (if/else), інакше не буде, що мутувати).
   
## Реалізація
**Тема проєкту:** створення завдання в гугл-календарі.

## Запуск тестів
### Команди
- `mvn clean test`
- `mvn jacoco:report`
- `mvn org.pitest:pitest-maven:mutationCoverage`
- `mvn clean verify sonar:sonar "-Dsonar.projectKey=[your_project]" "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=[your_token]"`
  
### JaCoCo report

<img width="945" height="171" alt="image" src="https://github.com/user-attachments/assets/52860d9a-2ecf-4f42-89d7-552cbbfbb094" />

### Sonar report

<img width="1225" height="603" alt="image" src="https://github.com/user-attachments/assets/d2d2f241-41a7-4771-848b-4994d838db19" />

### PIT report
```
>> Line Coverage (for mutated classes only): 89/100 (89%)
>> 1 tests examined
>> Generated 18 mutations Killed 13 (72%)
>> Mutations with no coverage 3. Test strength 87%
>> Ran 45 tests (2.5 tests per mutation)
```
