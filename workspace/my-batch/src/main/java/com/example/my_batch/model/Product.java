package com.example.my_batch.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;

    private Date date;

    // Constructeurs
    public Product() {}

    public Product(String name, double price, Date date) {
        this.name = name;
        this.price = price;
        this.date = date;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Date getdate(){return date;}

    public void setDate (Date d) {
        this.date = d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
