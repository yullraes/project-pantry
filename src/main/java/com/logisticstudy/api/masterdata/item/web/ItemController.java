package com.logisticstudy.api.masterdata.item.web;

import com.logisticstudy.api.masterdata.item.app.ItemAppService;
import com.logisticstudy.api.masterdata.item.web.dto.CreateItemRequest;
import com.logisticstudy.api.masterdata.item.web.dto.ItemResponse;
import com.logisticstudy.api.masterdata.item.web.dto.UpdateItemRequest;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemAppService itemAppService;

    // TODO: 실제 사용자 인증 정보에서 가져오도록 수정
    private static final String CURRENT_USER = "system";

    /**
     * 새로운 아이템을 생성합니다.
     * @param request 생성 요청 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createItem(@Valid @RequestBody CreateItemRequest request) {
        itemAppService.createItem(request, CURRENT_USER);
    }

    /**
     * ID로 아이템 정보를 조회합니다.
     * @param id 아이템 ID
     * @return 조회된 아이템 응답 DTO
     */
    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        return itemAppService.getItem(id);
    }

    /**
     * 아이템 정보를 업데이트합니다.
     * @param id 아이템 ID
     * @param request 업데이트 요청 DTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItem(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("경로 변수 ID와 요청 본문의 ID가 일치하지 않습니다.");
        }
        itemAppService.updateItem(request, CURRENT_USER);
    }

    /**
     * 페이징 처리된 아이템 목록을 조회합니다.
     * @param page 조회할 페이지 번호 (기본값 0)
     * @param size 페이지당 아이템 수 (기본값 10)
     * @return 페이징 처리된 아이템 응답 DTO 목록
     */
    @GetMapping
    public PageResponse<ItemResponse> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return itemAppService.getItems(page, size);
    }
}
