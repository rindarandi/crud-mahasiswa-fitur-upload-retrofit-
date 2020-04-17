package com.example.crudapplication;

import java.util.ArrayList;

public class Response {
    private boolean error;
    private String message;
    private Mahasiswa errors;

    public Mahasiswa getErrors() {
        return errors;
    }

    public void setErrors(Mahasiswa errors) {
        this.errors = errors;
    }

    private ArrayList<Mahasiswa> mahasiswa;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Mahasiswa> getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(ArrayList<Mahasiswa> mahasiswa) {
        this.mahasiswa = mahasiswa;
    }
}
