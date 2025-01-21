package com.example.myapi.repository;

import com.example.myapi.model.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
}
