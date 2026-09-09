package com.logisticstudy.api.masterdata.supplier.infra;

import com.logisticstudy.api.masterdata.supplier.domain.Supplier;
import com.logisticstudy.api.masterdata.supplier.infra.entity.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierEntityMapper {

    public Supplier toDomain(SupplierEntity entity) {
        return Supplier.builder()
                .id(entity.getId())
                .name(entity.getName())
                .leadTimeDays(entity.getLeadTimeDays())
                .status(Supplier.Status.valueOf(entity.getStatus().toUpperCase()))
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .contactPersonName(entity.getContactPersonName())
                .businessRegistrationNumber(entity.getBusinessRegistrationNumber())
                .paymentTerms(entity.getPaymentTerms())
                .build();
    }

    public SupplierEntity toEntity(Supplier domain) {
        return SupplierEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .leadTimeDays(domain.getLeadTimeDays())
                .status(domain.getStatus().name())
                .address(domain.getAddress())
                .phoneNumber(domain.getPhoneNumber())
                .email(domain.getEmail())
                .contactPersonName(domain.getContactPersonName())
                .businessRegistrationNumber(domain.getBusinessRegistrationNumber())
                .paymentTerms(domain.getPaymentTerms())
                .build();
    }
}