package com.logisticstudy.api.purchasing.domain.dto;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.purchasing.domain.vo.OverReceivePolicy;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;

import java.math.BigDecimal;

public record POLineState(
        POLineId id,
        ItemId itemId,
        Quantity targetQty,
        BigDecimal price,
        OverReceivePolicy overReceivePolicy,
        Quantity receivedQty
) {
}
