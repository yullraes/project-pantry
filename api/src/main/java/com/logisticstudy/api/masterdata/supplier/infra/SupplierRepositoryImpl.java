package com.logisticstudy.api.masterdata.supplier.infra;

import com.logisticstudy.api.masterdata.supplier.domain.Supplier;
import com.logisticstudy.api.masterdata.supplier.domain.contract.SupplierRepository;
import com.logisticstudy.api.masterdata.supplier.infra.entity.SupplierEntity;
import com.logisticstudy.api.shared.domain.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class SupplierRepositoryImpl implements SupplierRepository {

    private final JpaSupplierRepository jpaRepository;
    private final SupplierEntityMapper mapper;

    @Override
    public Supplier save(Supplier supplier) {
        SupplierEntity entity = mapper.toEntity(supplier);
        SupplierEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Supplier> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierEntity> pages = jpaRepository.findAll(pageable);
        List<Supplier> list = pages.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(list, page, size, pages.getTotalElements(), pages.getTotalPages(), pages.hasNext());
    }
}
