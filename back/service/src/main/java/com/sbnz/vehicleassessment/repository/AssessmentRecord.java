package com.sbnz.vehicleassessment.repository;

import com.sbnz.vehicleassessment.model.enums.TipOdluke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "assessment_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marka;

    @Column(nullable = false)
    private String model;

    private double nabavnaCena;
    private double vrednostVozila;
    private double vrednostOstataka;
    private double ukupniTroskovi;
    private double naknada;

    @Enumerated(EnumType.STRING)
    private TipOdluke odluka;

    @Lob
    @Column(length = 4000)
    private String obrazlozenje;

    @Column(nullable = false)
    private Instant kreirano;
}
