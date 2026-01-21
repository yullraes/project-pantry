package com.logisticstudy.api.stock.domain.contract;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.stock.domain.StockQuant;
import com.logisticstudy.api.stock.domain.vo.LotId;

import java.util.List;
import java.util.Optional;

public interface StockQuantRepository {
    StockQuant save(StockQuant quant);

    Optional<StockQuant> findById(Long id);

    Optional<StockQuant> findByKey(ItemId itemId, LocationId locationId, LotId lotId);

    List<StockQuant> findByItemIdAndLocationId(ItemId itemId, LocationId locationId);

    Optional<StockQuant> findByLotIdAndLocationId(LotId lotId, LocationId locationId);
}
