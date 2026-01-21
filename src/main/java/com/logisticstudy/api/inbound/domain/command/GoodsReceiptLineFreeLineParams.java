package com.logisticstudy.api.inbound.domain.command;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import lombok.Builder;

@Builder
public record GoodsReceiptLineFreeLineParams(
        Quantity quantity,
        LocationId toLocationId,
        LotInfo lotInfo
) {
}
