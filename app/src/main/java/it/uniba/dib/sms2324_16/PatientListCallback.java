package it.uniba.dib.sms2324_16;

import java.util.List;

public interface PatientListCallback {
    void onPatientListReceived(List<Patient> patientList);
    void onFailure(String errorMessage);
}
