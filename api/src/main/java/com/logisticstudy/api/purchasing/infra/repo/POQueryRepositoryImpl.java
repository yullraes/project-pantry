package com.logisticstudy.api.purchasing.infra.repo;

import com.logisticstudy.api.purchasing.web.dto.QPOListResponse;
import com.logisticstudy.api.purchasing.web.dto.QPOResponse_Line;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.logisticstudy.api.masterdata.item.infra.entity.QItemEntity;
import com.logisticstudy.api.masterdata.supplier.infra.entity.QSupplierEntity;
import com.logisticstudy.api.purchasing.infra.entity.QPOEntity;
import com.logisticstudy.api.purchasing.infra.entity.QPOLineEntity;
import com.logisticstudy.api.purchasing.web.dto.POListResponse;
import com.logisticstudy.api.purchasing.web.dto.POResponse;
import com.logisticstudy.api.shared.domain.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class POQueryRepositoryImpl implements POQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QPOEntity po = QPOEntity.pOEntity;
    private static final QPOLineEntity line = QPOLineEntity.pOLineEntity;
    private static final QSupplierEntity supplier = QSupplierEntity.supplierEntity;
    private static final QItemEntity item = QItemEntity.itemEntity;

    @Override
    public PageResult<POListResponse> findOpenPage(int page, int size) {
        BooleanExpression openCondition = po.status.notIn("CLOSED", "CANCELLED", "REJECTED");
        return findPageByCondition(openCondition, page, size);
    }

    @Override
    public PageResult<POListResponse> findOverduePage(int page, int size) {
        LocalDate today = LocalDate.now();
        BooleanExpression overdueCondition = po.expectedDate.before(today)
                .and(po.status.ne("CLOSED"))
                .and(po.status.ne("CANCELLED"))
                .and(po.status.ne("REJECTED"));

        return findPageByCondition(overdueCondition, page, size);
    }

    /**
     * 공통 페이징 조회 로직
     */
    private PageResult<POListResponse> findPageByCondition(BooleanExpression condition, int page, int size) {
        Long total = queryFactory
                .select(po.count())
                .from(po)
                .where(condition)
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<POListResponse> content = queryFactory
                .select(new QPOListResponse(
                        po.id,
                        supplier.name,
                        po.status,
                        po.expectedDate,
                        // 라인이 없을 때 sum()이 null 될 수 있으면 coalesce 써도 됨
                        // line.orderedQuantity.multiply(line.price).sum().coalesce(BigDecimal.ZERO)
                        line.orderedQuantity.multiply(line.price).sum()
                ))
                .from(po)
                .leftJoin(supplier).on(supplier.id.eq(po.supplierId))
                .leftJoin(po.purchaseOrderLines, line)
                .where(condition)
                .groupBy(po.id, po.status, po.expectedDate, supplier.name)
                .orderBy(po.expectedDate.asc(), po.id.asc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        int totalPages = totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / size);
        boolean hasNext = (long) (page + 1) * size < totalCount;

        return new PageResult<>(content, page, size, totalCount, totalPages, hasNext);
    }

    @Override
    public Optional<POResponse> findDetailById(Long id) {
        Tuple header = queryFactory
                .select(
                        po.id,
                        supplier.name,
                        po.createdBy,
                        po.status,
                        po.orderDate,
                        po.expectedDate
                )
                .from(po)
                .leftJoin(supplier).on(supplier.id.eq(po.supplierId))
                .where(po.id.eq(id))
                .fetchOne();

        if (header == null) {
            return Optional.empty();
        }

        // 2) 라인 목록 조회
        List<POResponse.Line> lines = queryFactory
                .select(new QPOResponse_Line(
                        item.name,
                        item.skuCode,
                        line.orderedQuantity,
                        line.receivedQuantity,
                        line.orderedQuantity.subtract(line.receivedQuantity),
                        line.price,
                        line.orderedQuantity.multiply(line.price)
                ))
                .from(po)
                .join(po.purchaseOrderLines, line)
                .leftJoin(item).on(item.id.eq(line.itemId))
                .where(po.id.eq(id))
                .fetch();


        BigDecimal totalAmount = lines.stream()
                .map(POResponse.Line::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        POResponse result = POResponse.builder()
                .id(header.get(po.id))
                .supplierName(header.get(supplier.name))
                .createdBy(header.get(po.createdBy))
                .status(header.get(po.status))
                .orderDate(header.get(po.orderDate))
                .expectedDeliveryDate(header.get(po.expectedDate))
                .totalAmount(totalAmount)
                .lines(lines)
                .build();

        return Optional.of(result);
    }
}