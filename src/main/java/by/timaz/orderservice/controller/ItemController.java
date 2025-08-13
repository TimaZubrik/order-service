package by.timaz.orderservice.controller;

import by.timaz.orderservice.dto.ItemDto;
import by.timaz.orderservice.dto.ItemUpdateDto;
import by.timaz.orderservice.service.ItemService;
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
@RequestMapping("/items/")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("all")
    public ResponseEntity<?> getItems() {
        return new ResponseEntity<>(itemService.findAllItems(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getItem(@RequestParam UUID id) {
        return new ResponseEntity<>(itemService.findItemById(id), HttpStatus.OK);
    }

    @GetMapping("by-name")
    public ResponseEntity<?> getItems(@RequestParam Collection<String> names) {
        return new ResponseEntity<>(itemService.findItemsByNames(names), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody @Valid ItemDto itemDto) {
        return new ResponseEntity<>(itemService.saveItem(itemDto), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<?> updateItem(@RequestBody @Valid ItemUpdateDto itemDto,
                                        @RequestParam UUID id) {
        return new ResponseEntity<>(itemService.updateItem(itemDto, id), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestParam UUID id) {
        itemService.deleteItemById(id);
        return new ResponseEntity<>("Item with id= "+id.toString()+" deleted", HttpStatus.OK);
    }
}
