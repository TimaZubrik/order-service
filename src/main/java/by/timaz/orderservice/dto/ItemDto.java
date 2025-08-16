package by.timaz.orderservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto implements Serializable {
    @Null(message = "ID should be empty. It generates automatically ")
    private UUID id;

    @NotBlank(message = "Name cannot be empty")
    @Pattern(
            regexp = "^[\\p{L}]+(?:[ \\p{Pd}'’][\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Name must contain only letters, spaces, hyphens or apostrophes")
    private String name;

    @DecimalMin(value = "0.01",
            message = "Minimum price is 0.01")
    @Digits(integer = 8,
            fraction = 2,
    message = "Price must have 8 numbers before the decimal point and 2 after")
    private BigDecimal price;
}
