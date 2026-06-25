package com.sbnz.vehicleassessment.dto;

import com.sbnz.vehicleassessment.model.OstecenoVozilo;
import com.sbnz.vehicleassessment.model.Vozilo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequest {
    private String brojSasije;
    private Vozilo vozilo;
    private List<OstecenoVozilo> ostecenja;
}
