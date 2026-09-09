package com.logisticstudy.api.masterdata.item.infra;

import com.logisticstudy.api.masterdata.item.domain.Item;
import com.logisticstudy.api.masterdata.item.domain.contract.ItemRepository;
import com.logisticstudy.api.masterdata.item.infra.entity.ItemEntity;
import com.logisticstudy.api.shared.domain.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final JpaItemRepository jpaRepository;
    private final ItemEntityMapper mapper;

    @Override
    public Item save(Item item) {
        ItemEntity entity = mapper.toEntity(item);
        ItemEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Item> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemEntity> pages = jpaRepository.findAll(pageable);
        List<Item> list = pages.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(list, page, size, pages.getTotalElements(), pages.getTotalPages(), pages.hasNext());
    }

    @Override
    public Optional<Item> findBySku(String sku) {
        return Optional.empty();
    }
}
