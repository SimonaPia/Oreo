package it.uniba.dib.sms2324_16;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient implements Serializable {
    private String id;
    private String name;
    private List<Exercise> assignedExercises;
    public Patient() {
        // Vuoto per compatibilità con Firestore
    }

    public Patient(String name) {
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
}
