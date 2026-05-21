# System rezerwacji sal konferencyjnych

Aplikacja do zarządzania rezerwacjami sal konferencyjnych w firmie.
Użytkownicy mogą przeglądać dostępne sale, tworzyć rezerwacje i zarządzać nimi.
Projekt zrealizowany w technologii Spring Boot z wykorzystaniem JPA i H2.

## Diagram encji

```
+----------------+          +-------------------+
|     Room       |          |   Reservation     |
+----------------+          +-------------------+
| id: Long (PK)  |<-------->| id: Long (PK)     |
| name: String   |    1   * | room: Room (FK)   |
| capacity: int  |          | reservedBy: String|
| floor: int     |          | startTime: LocalDT|
| available: bool|          | endTime: LocalDT  |
+----------------+          +-------------------+
```

## Uruchomienie

```bash
mvn spring-boot:run
```

Aplikacja uruchamia się na porcie 8083.
Konsola H2 dostępna pod: http://localhost:8083/h2-console

## Użyte scopy

| Scope     | Bean              | Uzasadnienie                                                                 |
|-----------|-------------------|------------------------------------------------------------------------------|
| singleton | RoomServiceImpl,  | Domyślny scope Spring. Serwisy nie przechowują stanu, więc jedna instancja   |
|           | ReservationServiceImpl | dla całej aplikacji jest wystarczająca i oszczędna.                        |
| prototype | ConflictChecker   | Każde sprawdzenie konfliktu wymaga osobnej, niezależnej instancji.           |
|           |                   | Dzięki temu unikamy współdzielenia stanu między równoczesnymi żądaniami      |
|           |                   | i zapewniamy bezpieczeństwo wątków bez synchronizacji.                       |
