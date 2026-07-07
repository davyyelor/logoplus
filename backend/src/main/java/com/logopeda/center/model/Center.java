package com.logopeda.center.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A physical or logical center within a clinic (e.g. a branch or consulting
 * room location). Multi-center support is additive: {@code clinicId} remains the
 * tenant boundary, and the center is an optional grouping within that clinic.
 */
@Entity
@Table(name = "centers", indexes = {
        @Index(name = "idx_center_clinic", columnList = "clinicId")
})
public class Center extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false)
    private String name;

    private String address;

    private String city;

    private String phone;

    private String email;

    @Column(nullable = false)
    private boolean active = true;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
