package com.logisticstudy.api.inbound.domain;

import com.logisticstudy.api.inbound.domain.command.GoodsReceiptLineFreeLineParams;
import com.logisticstudy.api.inbound.domain.command.GoodsReceiptLinePoLineParams;
import com.logisticstudy.api.inbound.domain.command.GoodsReceiptLineReconstitutionParams;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptLineId;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;



import java.math.BigDecimal;

@Getter
public class GoodsReceiptLine {
    private GoodsReceiptLineId id;
    private ItemId itemId; // 얘도 꼭 있어야할까? poLineId 가 있는데? TODO: 있어야 함 po 아닌 경우로도 만들어져서
    private POLineId poLineId; // nullable 하겠네?
    private Quantity quantity;

    private LocationId toLocationId;
    private LotInfo lotInfo;
    private boolean freeLine;
    private BigDecimal orderedQty;

    @Builder(access = AccessLevel.PRIVATE)
    private GoodsReceiptLine(GoodsReceiptLineId id,
                             ItemId itemId,
                             Quantity quantity,
                             LocationId toLocationId,
                             LotInfo lotInfo,
                             boolean freeLine,
                             POLineId poLineId,
                             BigDecimal orderedQty) {
        this.id = Require.notNull(id, "");
        this.itemId = Require.notNull(itemId, "");
        this.quantity = Require.notNull(quantity, "");
        Require.positive(this.quantity.value(), "");
        this.toLocationId = toLocationId;
        this.lotInfo = lotInfo;
        this.freeLine = freeLine;
        this.poLineId = poLineId;
        this.orderedQty = orderedQty;
    }

    public static GoodsReceiptLine reconstitute(GoodsReceiptLineId id,
                                                ItemId itemId,
                                                Quantity quantity,
                                                LocationId toLocationId,
                                                LotInfo lotInfo,
                                                boolean requiresExpiry,
                                                boolean freeLine,
                                                POLineId poLineId,
                                                BigDecimal orderedQty) {
        return new GoodsReceiptLine(id, itemId, quantity, toLocationId, lotInfo, freeLine, poLineId, orderedQty);
    }

    public static GoodsReceiptLine reconstitute(GoodsReceiptLineReconstitutionParams params) {
        Require.notNull(params, "");
        return reconstitute(
                params.id(),
                params.itemId(),
                params.quantity(),
                params.toLocationId(),
                params.lotInfo(),
                params.requiresExpiry(),
                params.freeLine(),
                params.poLineId(),
                params.orderedQty()
        );
    }

    public static GoodsReceiptLine freeLine(GoodsReceiptLineFreeLineParams params) {
        Require.notNull(params, "");
        return GoodsReceiptLine.builder()
                .id(GoodsReceiptLineId.forCreation())
                .quantity(params.quantity())
                .toLocationId(params.toLocationId())
                .lotInfo(params.lotInfo())
                .freeLine(true)
                .build();
    }

    public static GoodsReceiptLine fromPo(GoodsReceiptLinePoLineParams params) {
        Require.notNull(params, "");
        Require.notNull(params.poLineId(), "");
        return GoodsReceiptLine.builder()
                .id(params.id())
                .itemId(params.itemId())
                .poLineId(params.poLineId())
                .quantity(params.quantity())
                .orderedQty(params.orderedQty())
                .toLocationId(params.toLocationId())
                .lotInfo(params.lotInfo())
                .freeLine(false)
                .build();
    }


    public void changeQuantity(Quantity newQty) {
        Quantity qtyToSet = Require.notNull(newQty, "");
        Require.positive(qtyToSet.value(), "");
        this.quantity = qtyToSet;
    }

    public void changeLocation(LocationId newLocationId) {
        this.toLocationId = Require.notNull(newLocationId, "");
    }


    public void linkToPo(POLineId poLineId) {
        this.poLineId = Require.notNull(poLineId, "");
    }

    public void unlinkFromPo(Long poLineId) {
        Require.notNull(poLineId, "");
        Require.state(this.poLineId.equals(poLineId), "");
        this.poLineId = null;
    }

    public void changeLotInfo(LotInfo lotInfo) {
        this.lotInfo = Require.notNull(lotInfo, "");
    }
}
