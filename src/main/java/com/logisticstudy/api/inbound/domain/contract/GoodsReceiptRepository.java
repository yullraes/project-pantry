package com.logisticstudy.api.inbound.domain.contract;

import com.logisticstudy.api.inbound.domain.GoodsReceipt;

import java.util.Optional;

/**
 * 입고(GoodsReceipt) 애그리거트의 데이터 영속성을 처리하는 리포지토리 인터페이스입니다.
 */
public interface GoodsReceiptRepository {
    /**
     * 입고 애그리거트를 저장(생성 또는 수정)합니다.
     *
     * @param goodsReceipt 저장할 입고 애그리거트
     * @return 저장된 입고 애그리거트
     */
    GoodsReceipt save(GoodsReceipt goodsReceipt);

    /**
     * ID를 기준으로 입고 애그리거트를 조회합니다.
     *
     * @param id 조회할 입고 ID
     * @return 조회된 입고 애그리거트 (Optional)
     */
    Optional<GoodsReceipt> findById(Long id);
}
