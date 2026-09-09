package com.logisticstudy.api.masterdata.location.infra;

import com.logisticstudy.api.masterdata.location.infra.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLocationRepository extends JpaRepository<LocationEntity, Long> {
}
