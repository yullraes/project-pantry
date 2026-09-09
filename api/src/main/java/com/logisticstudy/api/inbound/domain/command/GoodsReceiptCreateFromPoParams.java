package com.logisticstudy.api.inbound.domain.command;

import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.purchasing.domain.vo.POId;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptId;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptNumber;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GoodsReceiptCreateFromPoParams(
        GoodsReceiptId id,
        GoodsReceiptNumber grnNumber,
        SupplierId supplierId,
        POId poId,
        LocalDateTime receivedAt
) {
}
