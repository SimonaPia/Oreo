package it.uniba.dib.sms2324_16;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patient implements Serializable {
    private String id;
    private String name;
    private List<Exercise> assignedExercises;
    public Patient() {
        // Vuoto per compatibilità con Firestore
    }

    public Patient(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAssignedExercise(Exercise exercise) {
        if (assignedExercises == null) {
            assignedExercises = new ArrayList<>();
        }

        if (!assignedExercises.contains(exercise)) {
            assignedExercises.add(exercise);
        }
    }


    public List<Exercise> getAssignedExercises() {
        return assignedExercises;
    }

    public void setAssignedExercises(List<Exercise> assignedExercises) {
        this.assignedExercises = assignedExercises;
    }

    public String getName() {
        return name;
    }

    public boolean hasSameName(Patient other) {
        return (this.id.equals(other.id) && this.name.equals(other.name));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Patient patient = (Patient) obj;
        return id.equals(patient.id) && name.equals(patient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
