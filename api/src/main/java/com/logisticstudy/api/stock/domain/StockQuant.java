package com.logisticstudy.api.stock.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.shared.Require;
import com.logisticstudy.api.stock.domain.vo.LotId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StockQuant {
    private final ItemId itemId;
    private final LocationId locationId;
    private final LotId lotId;
    private Quantity onHandQty;
    private Quantity reservedQty;
    private long version;

    @Builder(access = AccessLevel.PRIVATE)
    private StockQuant(
            ItemId itemId,
            LocationId locationId,
            LotId lotId,
            Quantity onHandQty,
            Quantity reservedQty,
            long version) {
        this.itemId = Require.notNull(itemId, "품목 ID는 필수입니다.");
        this.locationId = Require.notNull(locationId, "로케이션 ID는 필수입니다.");
        this.lotId = lotId;
        this.onHandQty = onHandQty == null ? Quantity.Zero() : onHandQty;
        this.reservedQty = reservedQty == null ? Quantity.Zero() : reservedQty;
        this.version = version;
        validateQuantities();
    }

    public Quantity getAvailableQty() {
        return onHandQty.subtract(reservedQty);
    }

    public void receive(Quantity qty) {
        increaseOnHand(qty);
    }

    public void consume(Quantity qty) {
        decreaseOnHand(qty);
    }

    public void reserve(Quantity qty) {
        Require.notNull(qty, "예약 수량이 null일 수 없습니다.");
        this.reservedQty = this.reservedQty.add(qty);
        validateQuantities();
    }

    public void releaseReservation(Quantity qty) {
        Require.notNull(qty, "예약 해제 수량이 null일 수 없습니다.");
        this.reservedQty = this.reservedQty.subtract(qty);
        validateQuantities();
    }

    public void commitReserved(Quantity qty) {
        releaseReservation(qty);
        decreaseOnHand(qty);
    }

    public void increaseVersion() {
        this.version = this.version + 1;
    }

    private void increaseOnHand(Quantity qty) {
        Require.notNull(qty, "증가 수량이 null일 수 없습니다.");
        this.onHandQty = this.onHandQty.add(qty);
    }

    private void decreaseOnHand(Quantity qty) {
        Require.notNull(qty, "감소 수량이 null일 수 없습니다.");
        this.onHandQty = this.onHandQty.subtract(qty);
        validateQuantities();
    }

    private void validateQuantities() {
        if (isOnHandLessThanReserved()) {
            throw new IllegalArgumentException("보유 수량은 예약 수량보다 크거나 같아야 합니다.");
        }
    }

    private boolean isOnHandLessThanReserved() {
        return this.onHandQty.compareTo(this.reservedQty) < 0;
    }
}
