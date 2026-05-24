package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.TipIntervencije;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromenaIntervencije {
    private String deo;
    private TipIntervencije od;
    private TipIntervencije ka;
    private String razlog;
}
