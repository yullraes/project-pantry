package com.logisticstudy.api.purchasing.domain.contract;

import com.logisticstudy.api.purchasing.domain.PO;
import com.logisticstudy.api.purchasing.domain.vo.POId;

import java.util.Optional;

public interface PORepository {
    void save(PO po);

    Optional<PO> findById(POId poId);

    void deleteById(POId poId);
}
