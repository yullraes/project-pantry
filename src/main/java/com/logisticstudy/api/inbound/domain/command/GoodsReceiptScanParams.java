package com.logisticstudy.api.inbound.domain.command;

import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptLineId;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record GoodsReceiptScanParams(
        GoodsReceiptLineId newLineId,
        Long itemId,
        BigDecimal incrementQty,
        String uom,
        Long locationId,
        LotInfo lotInfo,
        Long poLineId,
        BigDecimal orderedQty,
        boolean requiresExpiry
) {
}
