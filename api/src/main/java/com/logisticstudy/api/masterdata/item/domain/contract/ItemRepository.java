package com.logisticstudy.api.masterdata.item.domain.contract;

import com.logisticstudy.api.masterdata.item.domain.Item;
import com.logisticstudy.api.shared.domain.page.PageResult;

import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long id);

    PageResult<Item> findPage(int page, int size);

    Optional<Item> findBySku(String sku);
}
