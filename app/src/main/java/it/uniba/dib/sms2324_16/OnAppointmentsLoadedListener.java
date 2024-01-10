package it.uniba.dib.sms2324_16;

import java.util.List;

public interface OnAppointmentsLoadedListener {
    void onAppointmentsLoaded(List<Appointment> appointments);
    void onAppointmentsLoadError(Exception e);
}
