# **Voeg Basic Authentication toe aan je Spring Boot-applicatie**

In deze opdracht breid je je Spring Boot-project uit met **Basic Authentication** door gebruik te maken van **Spring Security**. Je leert hoe je gebruikers kunt beheren en toegang kunt beveiligen.

**Uitdaging**: Probeer elke stap zelf te implementeren voordat je de uitwerking bekijkt. Gebruik de hints als je vastzit.

---

## **Stap 1: Spring Security toevoegen**
Om beveiliging in je project in te schakelen, moet je eerst de benodigde dependency toevoegen.

1. Open het bestand `pom.xml`.
2. Voeg de volgende dependency toe binnen `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

3. **Herstart je project** om de dependency te laden.

**Hint:** Als je je applicatie opnieuw start en `/cars` opent, zal Spring Security nu om inloggegevens vragen.

---

## **Stap 2: Maak een `User` modelklasse**
Om gebruikers op te slaan, maken we een **`User`**-klasse.

1. Maak in de package `models` een nieuwe klasse `User` aan.
2. Voeg de volgende velden toe:
    - `id` (Long, primary key)
    - `username` (String, uniek en verplicht)
    - `password` (String, verplicht)
    - `role` (String, verplicht)

<details>
<summary>Uitwerking</summary>

```java
package nl.novi.cardemo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    // Getters en setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```
</details>

**Hint:** Zorg ervoor dat je de `jakarta.persistence`-annotaties gebruikt voor Entity-mapping.

---

## **Stap 4: Maak een `UserRepository`**
Om gebruikers op te slaan en op te halen, maken we een repository-interface.

1. Maak in de package `repositories` een nieuwe interface `UserRepository`.
2. Laat de interface `JpaRepository<User, Long>` implementeren.

<details>
<summary>Uitwerking</summary>

```java
package nl.novi.cardemo.repositories;

import nl.novi.cardemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```
</details>

**Hint:** De methode `findByUsername` wordt gebruikt voor authenticatie.

---

Je hebt nu Basic Authentication toegevoegd aan je Spring Boot-project.

## **Stap 4: Beveiligde API configureren**
In Spring Security moeten we aangeven welke endpoints beveiligd zijn en hoe gebruikers worden geverifieerd.

1. Maak een nieuwe klasse `SecurityConfig` in de package `config`.
2. Voeg een configuratie toe voor beveiliging.

<details>
<summary>Uitwerking</summary>

```java
package nl.novi.cardemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/cars").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic();
        return http.build();
    }
}
```
</details>

**Hint:**
- **`/cars`** is nu beveiligd en vereist authenticatie.
- **Andere endpoints** blijven publiek toegankelijk.
- **BCryptPasswordEncoder** wordt gebruikt voor veilige wachtwoordopslag.

---




## **Stap 5: Wachtwoord versleutelen met een helper**

Om wachtwoorden veilig in de database op te slaan, gebruiken we **BCryptPasswordEncoder**. Dit zorgt ervoor dat wachtwoorden gehasht worden en niet als platte tekst in de database staan.

1. Maak een nieuwe package genaamd `utils`.
2. Maak in deze package een klasse `PasswordEncoderUtil` aan.
3. Voeg de volgende code toe:

<details>
<summary>Uitwerking</summary>

```java
package nl.novi.cardemo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "jouw-wachtwoord";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
```
</details>

### **Wat doet deze helper?**
- Je kunt hiermee een wachtwoord invoeren en de gehashte versie genereren.
- Kopieer de gehashte versie en gebruik deze in je database.
- Elke keer dat je een wachtwoord invoert, wordt het anders gehasht (maar blijft hetzelfde te valideren).

**Hint:** Run deze klasse en vervang `jouw-wachtwoord` door een echt wachtwoord dat je wilt gebruiken.

---

## **Stap 6: Voeg versleutelde gebruikers toe aan de database**

Nu we wachtwoorden kunnen versleutelen, voegen we gebruikers toe aan de database met **data.sql**.

1. Open of maak het bestand `src/main/resources/data.sql`.
2. Voeg de volgende regels toe om een admin en een gewone gebruiker aan te maken:

<details>
<summary>Uitwerking</summary>

```sql
INSERT INTO users (username, password, role) VALUES ('admin', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('user', '$2a$10$RTmX/Behh84N9N7yQCRwOuaFbg21V8SlfaWBYFd0g4Ko8I6tFuWfy', 'USER');
```
</details>

### **Wat gebeurt hier?**
- We voegen een **admin** en een **gewone gebruiker** toe.
- De wachtwoorden zijn al gehashed met **BCrypt**.
- Dit zorgt ervoor dat gebruikers meteen beschikbaar zijn bij het opstarten van de applicatie.

**Hint:** Gebruik de `PasswordEncoderUtil` uit stap 5 om je eigen wachtwoorden te versleutelen en toe te voegen.

---

Je hebt nu een helper gemaakt om wachtwoorden te versleutelen en gebruikers veilig in de database gezet. De volgende stap is het implementeren van gebruikersbeheer!




