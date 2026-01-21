package com.logisticstudy.api.purchasing.app;

import com.logisticstudy.api.masterdata.item.domain.Item;
import com.logisticstudy.api.masterdata.item.domain.contract.ItemRepository;
import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.item.domain.vo.Uom;
import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.purchasing.domain.PO;
import com.logisticstudy.api.purchasing.domain.contract.PORepository;
import com.logisticstudy.api.purchasing.infra.repo.POQueryRepository;
import com.logisticstudy.api.purchasing.domain.vo.POId;
import com.logisticstudy.api.purchasing.web.dto.CreatePORequest;
import com.logisticstudy.api.purchasing.web.dto.POListResponse;
import com.logisticstudy.api.purchasing.web.dto.POResponse;
import com.logisticstudy.api.shared.domain.page.PageResult;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class POAppService {

    private final PORepository poRepository;
    private final POQueryRepository poQueryRepository;
    private final ItemRepository itemRepository;

    /**
     * 발주 생성 유스케이스.
     */
    public void createPO(CreatePORequest request) {
        SupplierId supplierId = new SupplierId(request.supplierId());
        PO po = PO.draft(supplierId, request.description(), request.orderDate(), request.expectedDeliveryDate());

        request.items().forEach(itemRequest -> {
            Item item = itemRepository.findBySku(itemRequest.sku())
                    .orElseThrow(() -> new NoSuchElementException("해당 SKU의 품목을 찾을 수 없습니다: " + itemRequest.sku()));
            ItemId itemId = item.getId();
            Quantity orderedQty = Quantity.of(itemRequest.quantity(), Uom.of(itemRequest.unit()));
            BigDecimal price = itemRequest.price();
            po.addPOLine(itemId, orderedQty, price, itemRequest.maxOverRatio());
        });

        poRepository.save(po);
    }

    /**
     * 단일 발주 조회 유스케이스.
     *
     * @param id 발주 ID
     * @return 발주 응답 DTO
     */
    public POResponse findPOResponseById(Long id) {
        return poQueryRepository.findDetailById(id)
                .orElseThrow(() -> new NoSuchElementException("발주를 찾을 수 없습니다: " + id));
    }

    /**
     * 발주 목록 조회 유스케이스.
     *
     * @param page 페이지 번호(0-base)
     * @param size 페이지 크기
     * @return 발주 응답 DTO 페이지
     */
    public PageResponse<POListResponse> findPOResponsePage(int page, int size) {
        return toListPageResponse(poQueryRepository.findOpenPage(page, size));
    }

    public void deletePOById(Long id) {
        poRepository.deleteById(new POId(id));
    }

    /**
     * 발주 승인 유스케이스.
     *
     * @param id 발주 ID
     */
    public void approvePO(Long id) {
        PO po = poRepository.findById(new POId(id))
                .orElseThrow(() -> new NoSuchElementException("발주를 찾을 수 없습니다: " + id));
        po.approve();
        poRepository.save(po);
    }

    /**
     * 발주 요청 전달(SENT 전환) 유스케이스.
     *
     * @param id 발주 ID
     */
    public void sendPO(Long id) {
        PO po = poRepository.findById(new POId(id))
                .orElseThrow(() -> new NoSuchElementException("발주를 찾을 수 없습니다: " + id));
        po.send();
        poRepository.save(po);
    }

    /**
     * 미마감(OPEN) 발주 목록 조회 유스케이스.
     *
     * @param page 페이지 번호(0-base)
     * @param size 페이지 크기
     * @return 발주 응답 DTO 페이지
     */
    public PageResponse<POListResponse> findOpenPOResponsePage(int page, int size) {
        return toListPageResponse(poQueryRepository.findOpenPage(page, size));
    }

    /**
     * 예상 납기일이 지났지만 미마감(OVERDUE) 발주 목록 조회 유스케이스.
     *
     * @param page 페이지 번호(0-base)
     * @param size 페이지 크기
     * @return 발주 응답 DTO 페이지
     */
    public PageResponse<POListResponse> findOverduePOResponsePage(int page, int size) {
        return toListPageResponse(poQueryRepository.findOverduePage(page, size));
    }

    private PageResponse<POListResponse> toListPageResponse(PageResult<POListResponse> pageResult) {
        return new PageResponse<>(pageResult.content(), pageResult.page(), pageResult.size(), pageResult.totalElements(),
                pageResult.totalPages(), pageResult.hasNext());
    }
}
