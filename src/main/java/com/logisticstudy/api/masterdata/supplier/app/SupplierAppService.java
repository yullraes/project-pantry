package com.logisticstudy.api.masterdata.supplier.app;

import com.logisticstudy.api.masterdata.supplier.domain.Supplier;
import com.logisticstudy.api.masterdata.supplier.domain.contract.SupplierRepository;
import com.logisticstudy.api.masterdata.supplier.web.dto.CreateSupplierRequest;
import com.logisticstudy.api.masterdata.supplier.web.dto.SupplierResponse;
import com.logisticstudy.api.masterdata.supplier.web.dto.UpdateSupplierRequest;
import com.logisticstudy.api.masterdata.supplier.web.SupplierDtoMapper;
import com.logisticstudy.api.shared.domain.page.PageResult;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierAppService {
    private final SupplierRepository supplierRepository;
    private final SupplierDtoMapper supplierDtoMapper;

    /**
     * 새로운 공급사를 생성합니다.
     * @param request 생성 요청 DTO
     * @param creator 생성자 ID
     */
    public void createSupplier(CreateSupplierRequest request, String creator) {

        Supplier supplier = supplierDtoMapper.toDomain(request, creator);
        supplierRepository.save(supplier);
    }

    /**
     * ID로 공급사 정보를 조회합니다.
     * @param id 공급사 ID
     * @return 조회된 공급사 응답 DTO
     * @throws NoSuchElementException 해당 ID의 공급사를 찾을 수 없을 경우
     */
    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + id + "인 공급사를 찾을 수 없습니다."));
        return supplierDtoMapper.toResponse(supplier);
    }

    /**
     * 공급사 정보를 업데이트합니다.
     * @param request 업데이트 요청 DTO
     * @param updater 수정자 ID
     * @throws NoSuchElementException 해당 ID의 공급사를 찾을 수 없을 경우
     */
    public void updateSupplier(UpdateSupplierRequest request, String updater) {
        Supplier existingSupplier = supplierRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("ID가 " + request.id() + "인 공급사를 찾을 수 없습니다."));

        Supplier updatedSupplier = supplierDtoMapper.toDomain(request, existingSupplier, updater);
        supplierRepository.save(updatedSupplier);
    }

    /**
     * 페이징 처리된 공급사 목록을 조회합니다.
     * @param page 조회할 페이지 번호
     * @param size 페이지당 공급사 수
     * @return 페이징 처리된 공급사 응답 DTO 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> getSuppliers(int page, int size) {
        PageResult<Supplier> suppliers = supplierRepository.findPage(page, size);
        var response = suppliers.content().stream().map(supplierDtoMapper::toResponse).toList();
        return new PageResponse<>(response, suppliers.page(), suppliers.size(), suppliers.totalElements(), suppliers.totalPages(), suppliers.hasNext());
    }
}