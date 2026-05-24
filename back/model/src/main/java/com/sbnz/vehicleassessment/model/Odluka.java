package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipOdluke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Odluka {
    private TipOdluke tip;
    private double naknada;
    private String obrazlozenje;
}
