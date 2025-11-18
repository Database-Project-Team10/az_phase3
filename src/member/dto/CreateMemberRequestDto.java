package src.member.dto;

import src.member.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateMemberRequestDto {
    private final String email;
    private final String password;
    private final String confirmPassword;
    private final String name;
    private final LocalDate birthDate;
    private final LocalDateTime createdAt;

    public CreateMemberRequestDto(String email, String password, String confirmPassword, String name, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Member toEntity() {
        return new Member(
                this.email,
                this.password,
                this.name,
                this.birthDate,
                this.createdAt
        );
    }
}
