package com.logopeda.therapy.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.therapy.dto.TherapySessionRequest;
import com.logopeda.therapy.enums.SessionType;
import com.logopeda.therapy.model.TherapySession;
import com.logopeda.therapy.repository.TherapySessionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Therapy session CRUD, scoped to the clinic. */
@Service
@Transactional
public class TherapySessionService {

    private final TherapySessionRepository sessionRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final com.logopeda.center.service.CenterService centerService;

    public TherapySessionService(TherapySessionRepository sessionRepository,
                                 PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                                 com.logopeda.center.service.CenterService centerService) {
        this.sessionRepository = sessionRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.centerService = centerService;
    }

    @Transactional(readOnly = true)
    public List<TherapySession> search(String clinicId, String patientId, String therapistId,
                                       LocalDate fromDate, LocalDate toDate, SessionType sessionType) {
        return sessionRepository.search(clinicId,
                blankToNull(patientId), blankToNull(therapistId), fromDate, toDate, sessionType);
    }

    @Transactional(readOnly = true)
    public TherapySession getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public TherapySession create(String clinicId, TherapySessionRequest request) {
        patientAccessGuard.requireAccessiblePatient(request.patientId());
        TherapySession session = new TherapySession();
        session.setClinicId(clinicId);
        apply(session, request);
        return sessionRepository.save(session);
    }

    public TherapySession update(String clinicId, String id, TherapySessionRequest request) {
        TherapySession session = findOwned(clinicId, id);
        apply(session, request);
        return sessionRepository.save(session);
    }

    public void delete(String clinicId, String id) {
        TherapySession session = findOwned(clinicId, id);
        sessionRepository.delete(session);
    }

    private void apply(TherapySession session, TherapySessionRequest request) {
        session.setPatientId(request.patientId());
        session.setTherapistId(request.therapistId());
        session.setAppointmentId(blankToNull(request.appointmentId()));
        session.setSessionDate(request.sessionDate());
        session.setDurationMinutes(request.durationMinutes());
        session.setSessionType(request.sessionType());
        session.setSummary(request.summary());
        session.setActivitiesPerformed(request.activitiesPerformed());
        session.setPatientResponse(request.patientResponse());
        session.setObservations(request.observations());
        session.setHomework(request.homework());
        session.setNextSteps(request.nextSteps());
        String centerId = blankToNull(request.centerId());
        centerService.requireCenterInClinic(centerId);
        session.setCenterId(centerId);
    }

    private TherapySession findOwned(String clinicId, String id) {
        return sessionRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("TherapySession", id));
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
