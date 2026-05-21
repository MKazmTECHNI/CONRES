# Projekt 1 – System rezerwacji sal konferencyjnych

## Opis projektu
Aplikacja do zarządzania rezerwacjami sal konferencyjnych w firmie.
Użytkownicy mogą przeglądać dostępne sale, tworzyć rezerwacje i zarządzać nimi.
Projekt realizowany w zespole 2–3 osobowym w ciągu 2 tygodni.

---

## Encje

### Room
| Pole       | Typ     | Opis                        |
|------------|---------|-----------------------------|
| id         | Long    | klucz główny, auto          |
| name       | String  | nazwa sali (np. "Sala A")   |
| capacity   | int     | liczba miejsc               |
| floor      | int     | piętro                      |
| available  | boolean | czy sala jest aktywna       |

### Reservation
| Pole        | Typ           | Opis                          |
|-------------|---------------|-------------------------------|
| id          | Long          | klucz główny, auto            |
| room        | Room (FK)     | powiązana sala (@ManyToOne)   |
| reservedBy  | String        | imię i nazwisko osoby         |
| startTime   | LocalDateTime | początek rezerwacji           |
| endTime     | LocalDateTime | koniec rezerwacji             |

---

## Wymagane endpointy REST

### Sale (/api/rooms)
| Metoda | Ścieżka                  | Opis                                     | Kod     |
|--------|--------------------------|------------------------------------------|---------|
| GET    | /api/rooms               | lista wszystkich sal                     | 200     |
| GET    | /api/rooms/{id}          | szczegóły sali                           | 200/404 |
| POST   | /api/rooms               | dodaj salę                               | 201     |
| PUT    | /api/rooms/{id}          | edytuj salę                              | 200/404 |
| DELETE | /api/rooms/{id}          | usuń salę                                | 204/404 |
| GET    | /api/rooms/available     | dostępne sale (param: date, minCapacity) | 200     |

### Rezerwacje (/api/reservations)
| Metoda | Ścieżka                            | Opis                      | Kod     |
|--------|------------------------------------|---------------------------|---------|
| GET    | /api/reservations                  | lista rezerwacji          | 200     |
| GET    | /api/reservations/{id}             | szczegóły rezerwacji      | 200/404 |
| GET    | /api/reservations/room/{roomId}    | rezerwacje dla sali       | 200     |
| POST   | /api/reservations                  | utwórz rezerwację         | 201/409 |
| DELETE | /api/reservations/{id}             | anuluj rezerwację         | 204/404 |

---

## Wymagania techniczne

### Struktura pakietów
```
com.example.roombooking
├── controller     ← tylko REST, zero logiki biznesowej
├── service        ← interfejs + implementacja dla każdego serwisu
├── repository     ← interfejsy Spring Data JPA
├── model          ← encje JPA + klasy żądań/odpowiedzi (bez osobnego dto)
├── event          ← klasy eventów i listenery
├── exception      ← własne wyjątki + GlobalExceptionHandler
└── config         ← klasy @Configuration z @Bean
```

### Interfejsy serwisów (obowiązkowe)
Każdy serwis musi być zdefiniowany przez interfejs:
```java
public interface RoomService {
    List<Room> findAll();
    Room findById(Long id);
    Room save(Room room);
    void delete(Long id);
    List<Room> findAvailable(LocalDateTime start, int minCapacity);
}
```
Controller wstrzykuje interfejs, nie implementację.

### JPA
- Adnotacje: @Entity, @Id, @GeneratedValue, @ManyToOne
- Relacja Room -> Reservation: @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
- Własna metoda w repozytorium, np.:
  List<Reservation> findByRoomIdAndStartTimeBetween(Long roomId, LocalDateTime from, LocalDateTime to);

### Eventy Spring
Przy tworzeniu rezerwacji należy opublikować event:
```java
public class RoomBookedEvent extends ApplicationEvent {
    private final Reservation reservation;
    // konstruktor, getter
}
```
Listener (@Component + @EventListener) wypisuje w konsoli informację o nowej rezerwacji.

### Scopy beanów
Projekt musi używać co najmniej 2 różnych scopów:
- singleton – domyślny dla serwisów
- prototype lub request – np. ConflictChecker tworzony na każde żądanie

Każdy niestandardowy scope musi mieć komentarz w kodzie wyjaśniający wybór.

### Własna konfiguracja @Bean
Klasa AppConfig.java z @Configuration:
```java
@Bean
public ObjectMapper objectMapper() { /* konfiguracja formatu dat */ }

@Bean
@Scope("prototype")
public ConflictChecker conflictChecker() { /* nowa instancja per użycie */ }
```

### Obsługa wyjątków
Klasa GlobalExceptionHandler z @RestControllerAdvice:
- ResourceNotFoundException (404) – brak sali lub rezerwacji
- ConflictException (409) – sala zajęta w danym terminie
- Odpowiedź JSON: { "status": 409, "message": "...", "timestamp": "..." }

Brak try/catch w kontrolerach – obsługa wyłącznie przez handler.

### Testy jednostkowe
Min. 5 testów dla RoomServiceImpl lub ReservationServiceImpl:
- @ExtendWith(MockitoExtension.class)
- Mockowanie repozytorium przez @Mock
- Nazwy: should_throwException_when_roomNotFound

### Plik API.http
Plik API.http w głównym katalogu z min. 10 wywołaniami:
- GET, POST, PUT, DELETE dla każdego zasobu
- Przypadki błędów (404, 409)

---

## Baza danych
- H2/Sqlite in-memory
- Dane inicjalne opcjonalnie przez data.sql lub CommandLineRunner

---

## README.md (wymagany)
1. Opis projektu (2-3 zdania)
2. Diagram encji (ASCII)
3. Instrukcja uruchomienia: mvn spring-boot:run
4. Opis użytych scopów i uzasadnienie

---

## Punktacja
| Element                              | Punkty |
|--------------------------------------|--------|
| Struktura projektu i pakiety         | 10     |
| JPA – encje, relacje, zapytania      | 15     |
| REST API + kody odpowiedzi           | 20     |
| Interfejsy serwisów + DI             | 10     |
| Eventy Spring                        | 10     |
| Scopy beanów                         | 10     |
| Obsługa wyjątków (@ControllerAdvice) | 10     |
| Testy jednostkowe (min. 5)           | 15     |
| Suma                                 | 100    |

### Przelicznik na ocenę
| Punkty | Ocena           |
|--------|-----------------|
| 90–100 | 6 celujący      |
| 80–89  | 5 bardzo dobry  |
| 65–79  | 4 dobry         |
| 50–64  | 3 dostateczny   |
| 30–49  | 2 dopuszczający |
| 0–29   | 1 niedostateczny|

### Typowe błędy odejmujące punkty
- Logika biznesowa w kontrolerze: -5 pkt
- Brak pliku API.http: -5 pkt
- Controller wstrzykuje implementację zamiast interfejsu: -3 pkt
- Brak komentarza przy niestandardowym scopie: -2 pkt
