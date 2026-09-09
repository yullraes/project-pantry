package com.logisticstudy.api.masterdata.item.app;

import com.logisticstudy.api.masterdata.item.domain.Item;
import com.logisticstudy.api.masterdata.item.domain.contract.ItemRepository;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.item.web.dto.CreateItemRequest;
import com.logisticstudy.api.masterdata.item.web.dto.ItemResponse;
import com.logisticstudy.api.masterdata.item.web.dto.UpdateItemRequest;
import com.logisticstudy.api.masterdata.item.web.ItemDtoMapper;
import com.logisticstudy.api.shared.domain.page.PageResult;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemAppService {
    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;

    /**
     * 새로운 아이템을 생성합니다.
     * @param request 생성 요청 DTO
     * @param creator 생성자 ID
     */
    public void createItem(CreateItemRequest request, String creator) {
        Item item = itemDtoMapper.toDomain(request, ItemId.forCreation() ,creator);
        itemRepository.save(item);
    }

    /**
     * ID로 아이템 정보를 조회합니다.
     * @param id 아이템 ID
     * @return 조회된 아이템 응답 DTO
     * @throws NoSuchElementException 해당 ID의 아이템을 찾을 수 없을 경우
     */
    @Transactional(readOnly = true)
    public ItemResponse getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + id + "인 아이템을 찾을 수 없습니다."));
        return itemDtoMapper.toResponse(item);
    }

    /**
     * 아이템 정보를 업데이트합니다.
     * @param request 업데이트 요청 DTO
     * @param updater 수정자 ID
     * @throws NoSuchElementException 해당 ID의 아이템을 찾을 수 없을 경우
     */
    public void updateItem(UpdateItemRequest request, String updater) {
        Item existingItem = itemRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("ID가 " + request.id() + "인 아이템을 찾을 수 없습니다."));

        Item updatedItem = itemDtoMapper.toDomain(request, existingItem, updater);
        itemRepository.save(updatedItem);
    }

    /**
     * 페이징 처리된 아이템 목록을 조회합니다.
     * @param page 조회할 페이지 번호
     * @param size 페이지당 아이템 수
     * @return 페이징 처리된 아이템 응답 DTO 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getItems(int page, int size) {
        PageResult<Item> items = itemRepository.findPage(page, size);
        var response = items.content().stream().map(itemDtoMapper::toResponse).toList();
        return new PageResponse<>(response, items.page(), items.size(), items.totalElements(), items.totalPages(), items.hasNext());
    }
}