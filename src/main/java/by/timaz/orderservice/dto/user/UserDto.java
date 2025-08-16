package by.timaz.orderservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {
    private UUID id;
    @NotBlank(message = "Name cannot be empty. ")
    @Pattern(regexp = "^[\\p{L}]+(?:[ \\p{Pd}’']?[\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Name cannot contain digits and other special characters")
    private String name;
    @NotBlank(message = "Surname cannot be empty")
    @Pattern(regexp = "^[\\p{L}]+(?:[ \\p{Pd}’']?[\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Surname cannot contain digits and other special characters")
    private String surname;
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email")
    private String email;
    @NotNull(message = "Birthday cannot be empty")
    @Past(message = "Birthday should be in the past")
    private LocalDate birthday;
    @Builder.Default
    private Set<CardDto> cards = new HashSet<>();

    public void addCard(CardDto card) {
        cards.add(card);
    }
}
