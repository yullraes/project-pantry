package com.logisticstudy.api.purchasing.infra.repo;

import com.logisticstudy.api.purchasing.web.dto.POListResponse;
import com.logisticstudy.api.purchasing.web.dto.POResponse;
import com.logisticstudy.api.shared.domain.page.PageResult;

import java.util.Optional;

public interface POQueryRepository {
    PageResult<POListResponse> findOpenPage(int page, int size);

    PageResult<POListResponse> findOverduePage(int page, int size);

    Optional<POResponse> findDetailById(Long id);
}
