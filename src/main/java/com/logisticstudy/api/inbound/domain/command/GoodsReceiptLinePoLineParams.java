package com.logisticstudy.api.inbound.domain.command;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptLineId;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import lombok.Builder;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import java.math.BigDecimal;

@Builder
public record GoodsReceiptLinePoLineParams(
        GoodsReceiptLineId id,
        ItemId itemId,
        POLineId poLineId,
        Quantity quantity,
        BigDecimal orderedQty,
        LocationId toLocationId,
        LotInfo lotInfo
) {
}
