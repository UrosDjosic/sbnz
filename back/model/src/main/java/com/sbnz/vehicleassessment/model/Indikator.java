package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipIndikatora;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indikator {
    private TipIndikatora tip;
    private String deo;
}
