package com.logisticstudy.api.purchasing.infra.entity;

import com.logisticstudy.api.shared.infra.jpa.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SoftDelete
public class POLineEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "ordered_qty", nullable = false)
    private BigDecimal orderedQuantity;

    @Column(name = "uom", nullable = false)
    private String uom;

    @Builder.Default
    @Column(name = "received_qty", nullable = false)
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Column(name = "price")
    private BigDecimal price;

    @Builder.Default
    @Column(name = "allow_over_receive", nullable = false)
    private boolean allowOverReceive = false;

    @Column(name = "max_over_ratio")
    private BigDecimal maxOverRatio;

}
