package com.logisticstudy.api.purchasing.infra.repo;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.item.domain.vo.Uom;
import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.purchasing.domain.PO;
import com.logisticstudy.api.purchasing.domain.contract.PORepository;
import com.logisticstudy.api.purchasing.domain.dto.POLineState;
import com.logisticstudy.api.purchasing.domain.vo.OverReceivePolicy;
import com.logisticstudy.api.purchasing.domain.vo.POId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.purchasing.infra.entity.POEntity;
import com.logisticstudy.api.purchasing.infra.entity.POLineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PORepository의 JPA 기반 구현체.
 */
@Repository
public class PORepositoryImpl implements PORepository {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;

    public PORepositoryImpl(PurchaseOrderJpaRepository purchaseOrderJpaRepository) {
        this.purchaseOrderJpaRepository = purchaseOrderJpaRepository;
    }

    @Override
    public void save(PO po) {
        purchaseOrderJpaRepository.save(toEntity(po));
    }

    @Override
    public Optional<PO> findById(POId poId) {
        return purchaseOrderJpaRepository.findById(poId.value())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(POId poId) {
        purchaseOrderJpaRepository.deleteById(poId.value());
    }

    private PO toDomain(POEntity entity) {
        List<POLineState> lineStates = entity.getPurchaseOrderLines().stream()
                .map(this::toLineState)
                .toList();

        return PO.reconcile(
                new POId(entity.getId()),
                new SupplierId(entity.getSupplierId()),
                PO.Status.valueOf(entity.getStatus()),
                entity.getDescription(),
                entity.getOrderDate(),
                entity.getExpectedDate(),
                lineStates
        );
    }

    private POLineState toLineState(POLineEntity entity) {
        return new POLineState(
                new POLineId(entity.getId()),
                new ItemId(entity.getItemId()),
                Quantity.of(entity.getOrderedQuantity(), Uom.of(entity.getUom())),
                entity.getPrice(),
                entity.isAllowOverReceive() ? OverReceivePolicy.allowedTo(entity.getMaxOverRatio()) : OverReceivePolicy.notAllowed(),
                Quantity.of(entity.getReceivedQuantity(), Uom.of(entity.getUom()))
        );
    }

    private POEntity toEntity(PO po) {
        POEntity entity = POEntity.builder()
                .id(po.getId().value())
                .supplierId(po.getSupplierId().value())
                .description(po.getDescription())
                .status(po.getStatus().name())
                .orderDate(po.getOrderDate())
                .expectedDate(po.getExpectedDeliveryCompleteDate())
                .build();

        po.getLines().forEach(line -> {
            POLineEntity lineEntity = POLineEntity.builder()
                    .id(line.getId().value())
                    .itemId(line.getItemId().value())
                    .orderedQuantity(line.getTargetQty().value())
                    .uom(line.getTargetQty().uom().symbol())
                    .receivedQuantity(line.getReceivedQty().value())
                    .price(line.getPrice())
                    .allowOverReceive(line.getOverReceivePolicy().isAllowed())
                    .maxOverRatio(line.getOverReceivePolicy().getMaxOverRatio())
                    .build();
            entity.getPurchaseOrderLines().add(lineEntity);
        });

        return entity;
    }
}
