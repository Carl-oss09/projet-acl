package com.example.myapi.service;

import com.example.myapi.model.Etudiant;
import com.example.myapi.repository.EtudiantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Etudiant addEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id).orElse(null);
    }

    public void updateEtudiant(Etudiant etudiant) {
        etudiantRepository.save(etudiant);
    }

    public void deleteEtudiant(Long id) {
        etudiantRepository.deleteById(id);
    }
}