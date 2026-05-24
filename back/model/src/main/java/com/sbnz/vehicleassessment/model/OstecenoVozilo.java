package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipIntervencije;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OstecenoVozilo {

    private String naziv;
    private TipIntervencije tipIntervencije;
    private double cenaDela;
    private int normaSati;
    private double cenaRadaSat;
    private double procenatOstecenja;
}
