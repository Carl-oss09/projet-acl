package com.example.myapi.service;

import com.example.myapi.model.Formateur;
import com.example.myapi.repository.FormateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormateurService {

    private final FormateurRepository formateurRepository;

    public FormateurService(FormateurRepository formateurRepository) {
        this.formateurRepository = formateurRepository;
    }

    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }

    public Formateur addFormateur(Formateur formateur) {
        return formateurRepository.save(formateur);
    }

    public Formateur getFormateurById(Long id) {
        return formateurRepository.findById(id).orElse(null);
    }

    public void updateFormateur(Formateur formateur) {
        formateurRepository.save(formateur);
    }

    public void deleteFormateur(Long id) {
        formateurRepository.deleteById(id);
    }
}