package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipIntervencije;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trosak {
    private String deo;
    private double iznos;
    private TipIntervencije tip;
}
