package com.sbnz.vehicleassessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairPolicy {
    private String deo;
    private double maxPopravkaProcenat;
    private boolean dozvoliPopravku;
    private boolean kritican;
}
