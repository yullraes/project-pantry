package com.logisticstudy.api.masterdata.location.infra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "allowed_temperature_zone")
    private String allowedTemperatureZone;

    @Column(name = "allowed_categories") // 콤마로 구분해서 집어넣기
    private String allowedCategories;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;
}