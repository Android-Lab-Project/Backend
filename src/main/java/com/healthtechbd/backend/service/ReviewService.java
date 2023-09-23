package com.healthtechbd.backend.service;

import com.healthtechbd.backend.repo.AmbulanceTripRepository;
import com.healthtechbd.backend.repo.DiagnosisOrderRepository;
import com.healthtechbd.backend.repo.DoctorSerialRepository;
import com.healthtechbd.backend.repo.MedicineOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private DoctorSerialRepository doctorSerialRepository;

    @Autowired
    private DiagnosisOrderRepository diagnosisOrderRepository;

    @Autowired
    private AmbulanceTripRepository ambulanceTripRepository;
    @Autowired
    private MedicineOrderRepository medicineOrderRepository;

    @Transactional
    public void updateReviewCheckedForDoctorSerial(Long o_id, Long user_id, Long subject_id) {
        doctorSerialRepository.updateReviewChecked(o_id, user_id, subject_id);
    }

    @Transactional
    public void updateReviewCheckedForDiagnosisOrder(Long o_id, Long user_id, Long subject_id) {
        diagnosisOrderRepository.updateReviewChecked(o_id, user_id, subject_id);
    }

    @Transactional
    public void updateReviewCheckedForAmbulanceTrip(Long o_id, Long user_id, Long subject_id) {
        ambulanceTripRepository.updateReviewChecked(o_id, user_id, subject_id);
    }

    @Transactional
    public void updateReviewCheckedForMedicineOrder(Long o_id, Long user_id, Long subject_id) {
        medicineOrderRepository.updateReviewChecked(o_id, user_id, subject_id);
    }
}
