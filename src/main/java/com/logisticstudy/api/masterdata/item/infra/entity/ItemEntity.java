package com.logisticstudy.api.masterdata.item.infra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    private String barcode;

    @Column(name = "base_uom", nullable = false)
    private String baseUom;

    @Column(name = "item_category", nullable = false)
    private String itemCategory;

    @Column(name = "temperature_zone", nullable = false)
    private String temperatureZone;

    @Column(name = "requires_expiry", nullable = false)
    private boolean requiresExpiry;

    @Column(name = "safety_stock")
    private BigDecimal safetyStock;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "ea_to_gram")
    private BigDecimal eaToGram;

    @Column(name = "ea_to_ml")
    private BigDecimal eaToMl;

    @Column(name = "ea_to_box")
    private BigDecimal eaToBox;

    @Column(name = "ea_to_plt")
    private BigDecimal eaToPlt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;
}
