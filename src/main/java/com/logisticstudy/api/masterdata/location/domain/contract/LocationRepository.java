package com.logisticstudy.api.masterdata.location.domain.contract;

import com.logisticstudy.api.masterdata.location.domain.Location;
import com.logisticstudy.api.shared.domain.page.PageResult;

import java.util.Optional;

public interface LocationRepository {
    Location save(Location location);

    Optional<Location> findById(Long id);

    PageResult<Location> findPage(int page, int size);
}