package it.uniba.dib.sms2324_16;

public class ExerciseItem {
    private String childName;
    private String exerciseType;
    private boolean exerciseCompleted;
    private String risposta;
    private String idBambino;
    private boolean exerciseCorrected;

    public ExerciseItem(String childName, String exerciseType, boolean exerciseCompleted, String risposta, String idBambino, boolean exerciseCorrected) {
        this.childName = childName;
        this.exerciseType = exerciseType;
        this.exerciseCompleted = exerciseCompleted;
        this.risposta = risposta;
        this.idBambino = idBambino;
        this.exerciseCorrected = exerciseCorrected;
    }

    public String getChildName() {
        return childName;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public boolean isExerciseCompleted() {
        return exerciseCompleted;
    }

    public String getRisposta() {
        return risposta;
    }

    public String getIdBambino() {
        return idBambino;
    }
    public boolean isExerciseCorrected() {
        return exerciseCorrected;
    }
    public void setCorrection(boolean exerciseCorrected) {
        this.exerciseCorrected = exerciseCorrected;
    }
}

