package com.sbnz.vehicleassessment.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "brand_coefficient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandCoefficientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String marka;

    private double startKoef;
    private double godisnjiPad;
    private double minVrednost;
    private double kmKorekcija;

    public BrandCoefficientEntity(String marka, double startKoef, double godisnjiPad, double minVrednost, double kmKorekcija) {
        this.marka = marka;
        this.startKoef = startKoef;
        this.godisnjiPad = godisnjiPad;
        this.minVrednost = minVrednost;
        this.kmKorekcija = kmKorekcija;
    }
}
