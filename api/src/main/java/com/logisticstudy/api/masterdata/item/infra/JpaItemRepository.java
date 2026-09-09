package com.logisticstudy.api.masterdata.item.infra;

import com.logisticstudy.api.masterdata.item.infra.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItemRepository extends JpaRepository<ItemEntity, Long> {
}
