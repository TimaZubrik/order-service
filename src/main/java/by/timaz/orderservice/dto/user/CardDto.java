package by.timaz.orderservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto implements Serializable {
    @NotBlank(message = "card number cannot be empty")
    @Pattern(regexp = "^\\d{13,19}$",
            message = "card number must contain from 13 to 19 digits")
    private String number;
    @Pattern(regexp = "^[A-Z][A-Z]+(?: [A-Z]+)*$",
            message = "holder's name must contain no more than 26 capital letters")
    private String holder;
    @NotBlank(message = "expiration date cannot be empty")
    @Pattern(regexp = "\\d{2}/\\d{2}",
             message = "expiration date must follow the pattern MM/YY")
    private String expiryDate;
}
