package com.logisticstudy.api.masterdata.supplier.web;

import com.logisticstudy.api.masterdata.supplier.app.SupplierAppService;
import com.logisticstudy.api.masterdata.supplier.web.dto.CreateSupplierRequest;
import com.logisticstudy.api.masterdata.supplier.web.dto.SupplierResponse;
import com.logisticstudy.api.masterdata.supplier.web.dto.UpdateSupplierRequest;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierAppService supplierAppService;

    // TODO: 실제 사용자 인증 정보에서 가져오도록 수정
    private static final String CURRENT_USER = "system";

    /**
     * 새로운 공급사를 생성합니다.
     * @param request 생성 요청 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        supplierAppService.createSupplier(request, CURRENT_USER);
    }

    /**
     * ID로 공급사 정보를 조회합니다.
     * @param id 공급사 ID
     * @return 조회된 공급사 응답 DTO
     */
    @GetMapping("/{id}")
    public SupplierResponse getSupplier(@PathVariable Long id) {
        return supplierAppService.getSupplier(id);
    }

    /**
     * 공급사 정보를 업데이트합니다.
     * @param id 공급사 ID
     * @param request 업데이트 요청 DTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSupplier(@PathVariable Long id, @Valid @RequestBody UpdateSupplierRequest request) {
        if (!id.equals(request.id())) {
            // ID 불일치 시 예외 처리 (GlobalExceptionHandler에서 처리하도록 유도)
            throw new IllegalArgumentException("경로 변수 ID와 요청 본문의 ID가 일치하지 않습니다.");
        }
        supplierAppService.updateSupplier(request, CURRENT_USER);
    }

    /**
     * 페이징 처리된 공급사 목록을 조회합니다.
     * @param page 조회할 페이지 번호 (기본값 0)
     * @param size 페이지당 공급사 수 (기본값 10)
     * @return 페이징 처리된 공급사 응답 DTO 목록
     */
    @GetMapping
    public PageResponse<SupplierResponse> getSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return supplierAppService.getSuppliers(page, size);
    }
}