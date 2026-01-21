package com.logisticstudy.api.purchasing.infra.repo;

import com.logisticstudy.api.purchasing.infra.entity.POEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderJpaRepository extends JpaRepository<POEntity, Long> {
}
