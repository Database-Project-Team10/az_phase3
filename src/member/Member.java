package src.member;

import java.time.LocalDate;

public class Member {

    Long id;
    String email;
    String password;
    String name;
    LocalDate birthDate;
    LocalDate createdAt;

    public Member() {}

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Member(String email, String password, String name, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.createdAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
