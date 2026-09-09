package com.logisticstudy.api.stock.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.shared.Require;
import com.logisticstudy.api.stock.domain.vo.LotId;
import com.logisticstudy.api.stock.domain.vo.StockMoveId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;


@Getter
public class StockMove {
    private StockMoveId id;
    private ItemId itemId;
    private LotId lotId;
    private Quantity quantity;
    private LocationId fromLocationId;
    private LocationId toLocationId;
    private Reason reason;

    public enum Reason {
        RECEIPT,
        ISSUE,
        TRANSFER,
        ADJUST
    }

    @Builder(access = AccessLevel.PRIVATE)
    private StockMove(StockMoveId id,
                      ItemId itemId,
                      LotId lotId,
                      Quantity quantity,
                      LocationId fromLocationId,
                      LocationId toLocationId,
                      Reason reason) {
        this.id = Require.notNull(id, "ID는 필수입니다.");
        this.itemId = Require.notNull(itemId, "품목 ID는 필수입니다.");
        this.lotId = lotId;
        this.quantity = Require.notNull(quantity, "수량은 필수입니다.");
        this.reason = Require.notNull(reason, "이동 사유는 필수입니다.");
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;

        switch (reason) {
            case RECEIPT -> {
                Require.notNull(toLocationId, "입고 이동은 toLocationId가 필수입니다.");
                Require.state(isReceiptWithFromLocation(), "입고 이동은 fromLocationId가 없어야 합니다.");
            }
            case ISSUE -> {
                Require.notNull(fromLocationId, "출고 이동은 fromLocationId가 필수입니다.");
                Require.state(isIssueWithToLocation(), "출고 이동은 toLocationId가 없어야 합니다.");
            }
            case TRANSFER -> {
                Require.notNull(fromLocationId, "재고 이전은 fromLocationId가 필수입니다.");
                Require.notNull(toLocationId, "재고 이전은 toLocationId가 필수입니다.");
            }
            case ADJUST -> {
                Require.state(isAdjustWithBothLocations(), "재고 조정은 from/to 둘 다 가질 수 없습니다.");
                Require.state(isAdjustWithNoLocation(), "재고 조정은 from/to 중 하나는 필수입니다.");
            }
        }
    }

    private boolean isAdjustWithNoLocation() {
        return fromLocationId == null && toLocationId == null;
    }

    private boolean isAdjustWithBothLocations() {
        return fromLocationId != null && toLocationId != null;
    }

    private boolean isIssueWithToLocation() {
        return toLocationId != null;
    }

    private boolean isReceiptWithFromLocation() {
        return fromLocationId != null;
    }

    public static StockMove receipt(ItemId itemId,
                                    LotId lotId,
                                    Quantity quantity,
                                    LocationId toLocationId) {
        return StockMove.builder()
                .id(StockMoveId.forCreation())
                .itemId(itemId)
                .lotId(lotId)
                .quantity(quantity)
                .toLocationId(toLocationId)
                .reason(Reason.RECEIPT)
                .build();
    }

    public static StockMove issue(ItemId itemId,
                                  LotId lotId,
                                  Quantity quantity,
                                  LocationId fromLocationId) {
        return StockMove.builder()
                .id(StockMoveId.forCreation())
                .itemId(itemId)
                .lotId(lotId)
                .quantity(quantity)
                .fromLocationId(fromLocationId)
                .reason(Reason.ISSUE)
                .build();
    }

    public static StockMove transfer(ItemId itemId,
                                     LotId lotId,
                                     Quantity quantity,
                                     LocationId fromLocationId,
                                     LocationId toLocationId) {
        return StockMove.builder()
                .id(StockMoveId.forCreation())
                .itemId(itemId)
                .lotId(lotId)
                .quantity(quantity)
                .fromLocationId(fromLocationId)
                .toLocationId(toLocationId)
                .reason(Reason.TRANSFER)
                .build();
    }

    public static StockMove adjust(ItemId itemId,
                                   LotId lotId,
                                   Quantity quantity,
                                   LocationId fromLocationId,
                                   LocationId toLocationId) {
        return StockMove.builder()
                .id(StockMoveId.forCreation())
                .itemId(itemId)
                .lotId(lotId)
                .quantity(quantity)
                .fromLocationId(fromLocationId)
                .toLocationId(toLocationId)
                .reason(Reason.ADJUST)
                .build();
    }
}
