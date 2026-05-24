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
@Table(name = "repair_policy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deo;

    private double maxPopravkaProcenat;
    private boolean dozvoliPopravku;
    private boolean kritican;

    public RepairPolicyEntity(String deo, double maxPopravkaProcenat, boolean dozvoliPopravku, boolean kritican) {
        this.deo = deo;
        this.maxPopravkaProcenat = maxPopravkaProcenat;
        this.dozvoliPopravku = dozvoliPopravku;
        this.kritican = kritican;
    }
}
