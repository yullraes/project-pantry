package com.logisticstudy.api.inbound.domain.vo;

import com.logisticstudy.api.shared.Require;

import java.time.LocalDate;

/**
 * 로트 번호와 유통기한의 쌍을 나타내는 값 객체(Value Object)입니다.
 * 이 정보는 입고 라인과 함께 이동합니다.
 *
 * @param lotCode    로트 번호
 * @param expiryDate 유통기한
 */
public record LotInfo(
        // 시스템 내부에서 쓰는건데 LotId 면 되지않나? 그렇다면 또 expiryDate는 필요없는거 아닌가? TODO: 필요함 입고 당시에 Lot 엔티티가 만들어져 있지 않음 따라서 LotInfo 로 만들어야 할 수 있음
        String lotCode,
        LocalDate expiryDate
) {

    public LotInfo {
        Require.notBlank(lotCode, "로트 번호는 비어 있을 수 없습니다.");
        Require.notNull(expiryDate, "유통기한은 비어 있을 수 없습니다.");
    }
}
    
