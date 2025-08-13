package by.timaz.orderservice.controller;

import by.timaz.orderservice.dto.OrderDto;
import by.timaz.orderservice.dto.OrderUpdateDto;
import by.timaz.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/order/")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("all")
    public ResponseEntity<?> getOrders() {
        return new ResponseEntity<>(orderService.findAll(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getOrder(@RequestParam UUID id) {
        return new ResponseEntity<>(orderService.findById(id), HttpStatus.OK);
    }

    @GetMapping("by-ids")
    public ResponseEntity<?> getOrdersByIdIn(@RequestParam Collection<UUID> ids) {
        return new ResponseEntity<>(orderService.findByIdsIn(ids), HttpStatus.OK);
    }

    @GetMapping("users")
    public ResponseEntity<?> getOrdersByUsersIds(@RequestParam Collection<UUID> ids) {
        return new ResponseEntity<>(orderService.findByUserIds(ids), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDto orderDto) {
        return new ResponseEntity<>(orderService.save(orderDto), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<?> updateOrder(@RequestBody @Valid OrderUpdateDto orderDto,
                                         @RequestParam UUID id) {
        return new ResponseEntity<>(orderService.update(orderDto,id), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteOrder(@RequestParam UUID id) {
        orderService.delete(id);
        return new ResponseEntity<>("Order with id= "+id.toString()+" deleted",HttpStatus.OK);
    }
}
