package com.logopeda.guardian.model;

import com.logopeda.guardian.enums.GuardianRelationship;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** A guardian / family member linked to a patient. */
@Entity
@Table(name = "guardians", indexes = {
        @Index(name = "idx_guardians_clinic", columnList = "clinicId"),
        @Index(name = "idx_guardians_patient", columnList = "patientId"),
        @Index(name = "idx_guardians_user", columnList = "userId")
})
public class Guardian extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    /** Set when this guardian has a FAMILY portal account. */
    @Column(length = 36)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GuardianRelationship relationship;

    private String email;

    @Column(length = 32)
    private String phone;

    @Column(nullable = false)
    private boolean canAccessPortal = false;

    @Column(nullable = false)
    private boolean canReceiveReports = false;

    @Column(nullable = false)
    private boolean canReceiveReminders = false;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public GuardianRelationship getRelationship() {
        return relationship;
    }

    public void setRelationship(GuardianRelationship relationship) {
        this.relationship = relationship;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isCanAccessPortal() {
        return canAccessPortal;
    }

    public void setCanAccessPortal(boolean canAccessPortal) {
        this.canAccessPortal = canAccessPortal;
    }

    public boolean isCanReceiveReports() {
        return canReceiveReports;
    }

    public void setCanReceiveReports(boolean canReceiveReports) {
        this.canReceiveReports = canReceiveReports;
    }

    public boolean isCanReceiveReminders() {
        return canReceiveReminders;
    }

    public void setCanReceiveReminders(boolean canReceiveReminders) {
        this.canReceiveReminders = canReceiveReminders;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
