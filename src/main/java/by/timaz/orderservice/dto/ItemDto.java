package by.timaz.orderservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto implements Serializable {
    private UUID id;

    @Pattern(regexp = "^[\\p{L}]+(?:[ \\p{Pd}’']?[\\p{L}]+)*$",
            flags = Pattern.Flag.UNICODE_CASE,
            message = "Name cannot contain digits and other special characters")
    private String name;

    @DecimalMin(value = "0.01",
            message = "Minimum price is 0.01")
    @Digits(integer = 8,
            fraction = 2,
    message = "Price must have 8 numbers before the decimal point and 2 after")
    private BigDecimal price;

    private List<OrderItemDto> orderItems = new ArrayList<>();
}
