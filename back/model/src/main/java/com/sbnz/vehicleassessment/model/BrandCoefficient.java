package com.sbnz.vehicleassessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandCoefficient {
    private String marka;
    private double startKoef;
    private double godisnjiPad;
    private double minVrednost;
    private double kmKorekcija;
}
