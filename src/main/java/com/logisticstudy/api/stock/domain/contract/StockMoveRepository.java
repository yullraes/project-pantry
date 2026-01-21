package com.logisticstudy.api.stock.domain.contract;

import com.logisticstudy.api.stock.domain.StockMove;

import java.util.Optional;

public interface StockMoveRepository {
    StockMove save(StockMove move);

    Optional<StockMove> findByIdempotencyKey(String idempotencyKey);
}
