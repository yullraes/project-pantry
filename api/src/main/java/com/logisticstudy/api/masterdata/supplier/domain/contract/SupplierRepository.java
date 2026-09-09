package com.logisticstudy.api.masterdata.supplier.domain.contract;

import com.logisticstudy.api.masterdata.supplier.domain.Supplier;
import com.logisticstudy.api.shared.domain.page.PageResult;

import java.util.Optional;

public interface SupplierRepository {
    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Long id);

    PageResult<Supplier> findPage(int page, int size);
}
