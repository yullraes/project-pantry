package com.logisticstudy.api.masterdata.supplier.domain;

import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Supplier {
    public enum Status {ACTIVE, INACTIVE}
    private SupplierId id;
    private String name;
    private Integer leadTimeDays;
    private Status status;
    private String address;
    private String phoneNumber;
    private String email;
    private String contactPersonName;
    private String businessRegistrationNumber;
    private String paymentTerms;

    @Builder(access = AccessLevel.PACKAGE)
    Supplier(SupplierId id, String name, Integer leadTimeDays, Status status,
             String address, String phoneNumber, String email, String contactPersonName,
             String businessRegistrationNumber, String paymentTerms) {

        this.id = Require.notNull(id, "공급업체 ID는 필수입니다.");
        this.name = Require.notBlank(name, "공급사 이름은 필수입니다.");
        this.leadTimeDays = Require.notNull(leadTimeDays, "리드 타임은 필수입니다.");
        Require.state(this.leadTimeDays >= 0, "리드 타임은 0 이상이어야 합니다.");
        this.status = Require.notNull(status, "공급사 상태는 필수입니다.");
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.contactPersonName = contactPersonName;
        this.businessRegistrationNumber = Require.notBlank(businessRegistrationNumber, "사업자 등록 번호는 필수입니다.");
        this.paymentTerms = paymentTerms;
    }

    public static Supplier reconcile(SupplierId id, String name, Integer leadTimeDays, Status status,
                                     String address, String phoneNumber, String email, String contactPersonName,
                                     String businessRegistrationNumber, String paymentTerms) {
        return Supplier.builder()
                .id(id)
                .name(name)
                .leadTimeDays(leadTimeDays)
                .status(status)
                .address(address)
                .phoneNumber(phoneNumber)
                .email(email)
                .contactPersonName(contactPersonName)
                .businessRegistrationNumber(businessRegistrationNumber)
                .paymentTerms(paymentTerms)
                .build();
    }

    public static Supplier create(String name, Integer leadTimeDays,
                                  String address, String phoneNumber, String email, String contactPersonName,
                                  String businessRegistrationNumber, String paymentTerms) {
        return Supplier.builder()
                .id(SupplierId.forCreation())
                .name(name)
                .leadTimeDays(leadTimeDays)
                .status(Status.ACTIVE)
                .address(address)
                .phoneNumber(phoneNumber)
                .email(email)
                .contactPersonName(contactPersonName)
                .businessRegistrationNumber(businessRegistrationNumber)
                .paymentTerms(paymentTerms)
                .build();
    }

    /**
     * 이 공급사가 주어진 리드 타임 내에 주문을 이행할 수 있는지 확인합니다.
     * 공급사의 상태가 ACTIVE여야 하며, 공급사의 리드 타임이 요구된 리드 타임보다 짧거나 같아야 합니다.
     * @param requiredLeadTimeDays 요구되는 최대 리드 타임 (일)
     * @return 주문 이행이 가능하면 true, 불가능하면 false
     */
    public boolean canFulfillOrder(int requiredLeadTimeDays) {
        if (this.status != Status.ACTIVE) {
            return false;
        }

        if (this.leadTimeDays > requiredLeadTimeDays) {
            return false;
        }

        return true;
    }
}
