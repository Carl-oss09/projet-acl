package com.example.my_batch.model;

import jakarta.persistence.*;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCours;
    private Long idEleve;

    // Constructeurs
    public Reservation() {}

    public Reservation(Long id_cours, Long id_eleve) {
        this.idCours = id_cours;
        this.idEleve = id_eleve;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCours() {
        return idCours;
    }

    public void setIdCours(Long id_cours) {
        this.idCours = id_cours;
    }

    public Long getIdEleve() {
        return idEleve;
    }

    public void setIdEleve(Long idEleve) {
        this.idEleve = idEleve;
    }
}