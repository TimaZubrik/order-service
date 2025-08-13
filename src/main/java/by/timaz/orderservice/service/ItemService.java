package by.timaz.orderservice.service;

import by.timaz.orderservice.dao.entity.Item;
import by.timaz.orderservice.dao.repository.ItemRepository;
import by.timaz.orderservice.dto.ItemDto;
import by.timaz.orderservice.dto.ItemUpdateDto;
import by.timaz.orderservice.exceptions.ResourceNotFoundException;
import by.timaz.orderservice.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemDto saveItem(ItemDto itemDto) {
        Item item = itemRepository.save(itemMapper.toItem(itemDto));
        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> findAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    public ItemDto findItemById(UUID id) {
        return itemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Item", "id", id.toString())));
    }

    public List<ItemDto> findItemsByNames(Collection<String> names) {
        List<Item> items = itemRepository.findItemsByNameIn(names);
        return items.stream()
                     .map(itemMapper::toItemDto)
                     .toList();
    }
    @Transactional
    public ItemDto updateItem(ItemUpdateDto itemDto, UUID id) {

        return itemMapper.toItemDto(
                itemMapper.updateItem(
                        itemDto,
                        itemRepository.findById(id)
                                .orElseThrow(()-> new ResourceNotFoundException("Item", "id", id.toString()))
                )
        );
    }

    @Transactional
    public void deleteItemById(UUID id) {
        itemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Item", "id", id.toString())));
        itemRepository.deleteById(id);
    }

}
