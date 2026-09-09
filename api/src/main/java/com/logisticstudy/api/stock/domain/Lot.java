package com.logisticstudy.api.stock.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.shared.Require;
import com.logisticstudy.api.stock.domain.vo.LotId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Lot {
    private final LotId id;
    private final ItemId itemId;
    private final String lotCode;
    private final LocalDate expiryDate;

    @Builder(access = AccessLevel.PRIVATE)
    private Lot(LotId id,
                ItemId itemId,
                String lotCode,
                LocalDate expiryDate
) {
        this.id = id;
        this.itemId = Require.notNull(itemId, "품목 ID는 필수입니다.");
        this.lotCode = Require.notBlank(lotCode, "Lot 코드는 필수입니다.");
        this.expiryDate = expiryDate;
    }
}
