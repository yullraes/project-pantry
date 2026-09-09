package com.logisticstudy.api.masterdata.supplier.web;

import com.logisticstudy.api.masterdata.supplier.domain.Supplier;
import com.logisticstudy.api.masterdata.supplier.web.dto.CreateSupplierRequest;
import com.logisticstudy.api.masterdata.supplier.web.dto.SupplierResponse;
import com.logisticstudy.api.masterdata.supplier.web.dto.UpdateSupplierRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SupplierDtoMapper {

    @Mappings({
            @Mapping(source = "status", target = "status")
    })
    SupplierResponse toResponse(Supplier supplier);

    default Supplier toDomain(CreateSupplierRequest request, Long newId, String creator) {
        return Supplier.builder()
                .id(newId)
                .name(request.name())
                .leadTimeDays(request.leadTimeDays())
                .status(Supplier.Status.valueOf(request.status().toUpperCase()))
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .contactPersonName(request.contactPersonName())
                .businessRegistrationNumber(request.businessRegistrationNumber())
                .paymentTerms(request.paymentTerms())
                .build();
    }

    default Supplier toDomain(UpdateSupplierRequest request, Supplier existingSupplier, String updater) {
        return Supplier.builder()
                .id(existingSupplier.getId())
                .name(request.name())
                .leadTimeDays(request.leadTimeDays())
                .status(Supplier.Status.valueOf(request.status().toUpperCase()))
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .contactPersonName(request.contactPersonName())
                .businessRegistrationNumber(request.businessRegistrationNumber())
                .paymentTerms(request.paymentTerms())
                .build();
    }
}