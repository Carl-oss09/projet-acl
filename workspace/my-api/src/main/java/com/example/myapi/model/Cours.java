package com.example.myapi.model;

import jakarta.persistence.*;

@Entity
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matiere;
    private String titre;
    private String description;
    private String date;

    private boolean aprem_matin;
    private Long formateurId;
    private int nb_eleves_max;

    // Constructeurs
    public Cours() {}

    public Cours(String matiere, String titre, String description, String date, boolean aprem_matin, Long id_prof, int nb_eleves_max) {
        this.matiere = matiere;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.aprem_matin = aprem_matin;
        this.formateurId = id_prof;
        this.nb_eleves_max = nb_eleves_max;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAprem_matin() {
        return aprem_matin;
    }

    public void setAprem_matin(boolean aprem_matin) {
        this.aprem_matin = aprem_matin;
    }

    public Long getFormateurId() {
        return formateurId;
    }

    public void setFormateurId(Long id_prof) {
        this.formateurId = id_prof;
    }

    public int getNb_eleves_max() {
        return nb_eleves_max;
    }

    public void setNb_eleves_max(int nb_eleves_max) {
        this.nb_eleves_max = nb_eleves_max;
    }
}