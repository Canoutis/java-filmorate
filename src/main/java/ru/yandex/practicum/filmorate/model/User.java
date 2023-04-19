package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @Email
    private String email;
    @NotEmpty
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
