package com.logisticstudy.api.stock.domain.contract;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.stock.domain.Lot;
import com.logisticstudy.api.stock.domain.vo.LotId;

import java.util.List;
import java.util.Optional;

public interface LotRepository {
    Lot save(Lot lot);

    Optional<Lot> findById(LotId id);

    Optional<Lot> findByItemIdAndCode(ItemId itemId, String lotCode);

    List<Lot> findAllById(List<LotId> lotIds);
}
