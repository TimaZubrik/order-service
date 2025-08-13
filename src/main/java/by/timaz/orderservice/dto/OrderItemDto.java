package by.timaz.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto implements Serializable {

    private UUID id;
    @NotBlank(message = "quantity cannot be empty")
    @Pattern(regexp = "\\d+", message = "quantity must be integer")
    private int quantity;

}
