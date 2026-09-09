package com.logisticstudy.api.masterdata.supplier.infra;

import com.logisticstudy.api.masterdata.supplier.infra.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSupplierRepository extends JpaRepository<SupplierEntity, Long> {
}
