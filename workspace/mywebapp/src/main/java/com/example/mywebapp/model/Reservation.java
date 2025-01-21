package com.example.mywebapp.model;


public class Reservation {

    private Long id;

    private Long id_cours;
    private Long idEleve;

    // Constructeurs
    public Reservation() {}

    public Reservation(Long id_cours, Long id_eleve) {
        this.id_cours = id_cours;
        this.idEleve = id_eleve;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_cours() {
        return id_cours;
    }

    public void setId_cours(Long id_cours) {
        this.id_cours = id_cours;
    }

    public Long getIdEleve() {
        return idEleve;
    }

    public void setIdEleve(Long idEleve) {
        this.idEleve = idEleve;
    }
}