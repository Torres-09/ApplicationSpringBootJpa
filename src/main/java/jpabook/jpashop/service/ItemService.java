package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name,int price) {
        Item item = itemRepository.findOne(itemId);
        item.setName(name);
        item.setPrice(price);

        // 사실 이것도 아니고 세터가 아니라 변경 로직을 별도로..
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 상품 찾기
     * @param id
     * @return 상품
     */
    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

}
