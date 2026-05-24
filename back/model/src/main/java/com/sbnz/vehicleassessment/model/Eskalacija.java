package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipEskalacije;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Eskalacija {
    private TipEskalacije tip;
    private String opis;
}
