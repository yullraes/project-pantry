package com.logisticstudy.api.inbound.domain.command;

import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptLineId;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;

import java.math.BigDecimal;

public record GoodsReceiptLineReconstitutionParams(
        GoodsReceiptLineId id,
        ItemId itemId,
        Quantity quantity,
        LocationId toLocationId,
        LotInfo lotInfo,
        boolean requiresExpiry,
        boolean freeLine,
        POLineId poLineId,
        BigDecimal orderedQty
) {
}
