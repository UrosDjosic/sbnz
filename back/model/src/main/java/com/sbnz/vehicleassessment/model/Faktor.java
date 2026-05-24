package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipFaktora;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faktor {
    private TipFaktora tip;
    private double koeficijent;
}
