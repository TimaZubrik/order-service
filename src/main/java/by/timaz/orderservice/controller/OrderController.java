package by.timaz.orderservice.controller;

import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dto.OrderDto;
import by.timaz.orderservice.dto.OrderProduct;
import by.timaz.orderservice.dto.OrderUpdateDto;
import by.timaz.orderservice.dto.ResponseDto;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order/")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("all")
    public ResponseEntity<ResponseDto> getOrders(@RequestParam String email) {
        return new ResponseEntity<>(orderService.findAll(email), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getOrder(@RequestParam UUID id,
                                                @RequestParam String email) {
        return new ResponseEntity<>(orderService.findById(id,email), HttpStatus.OK);
    }

    @GetMapping("by-ids")
    public ResponseEntity<ResponseDto> getOrdersByIdIn(@RequestParam Collection<UUID> ids,
                                             @RequestParam String email) {
        return new ResponseEntity<>(orderService.findByIdsIn(ids, email), HttpStatus.OK);
    }

    @GetMapping("users")
    public ResponseEntity<List<OrderDto>> getOrdersByUsersIds(@RequestParam Collection<UUID> ids) {
        return new ResponseEntity<>(orderService.findByUserIds(ids), HttpStatus.OK);
    }

    @GetMapping("status")
    public ResponseEntity<ResponseDto> getOrderStatus(@RequestParam Collection<String> statusList,
                                                      @RequestParam String email) {
        return new ResponseEntity<>(orderService.findByStatusIn(statusList, email), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createOrder(@RequestBody @Valid List<@Valid OrderProduct> orderRequest,
                                                    @RequestParam String email) {
        return new ResponseEntity<>(orderService.save(orderRequest, email), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<ResponseDto> updateOrder(@RequestBody @Valid OrderUpdateDto orderDto,
                                         @RequestParam UUID id,
                                         @RequestParam String email) {
        return new ResponseEntity<>(orderService.update(orderDto,id,email), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteOrder(@RequestParam UUID id,
                                              @RequestParam String email) {
        orderService.delete(id,email);
        return new ResponseEntity<>("Order with id= "+id.toString()+" deleted",HttpStatus.OK);
    }

}
