package com.sbnz.vehicleassessment.dto;

import com.sbnz.vehicleassessment.model.Eskalacija;
import com.sbnz.vehicleassessment.model.Faktor;
import com.sbnz.vehicleassessment.model.Indikator;
import com.sbnz.vehicleassessment.model.Odluka;
import com.sbnz.vehicleassessment.model.PromenaIntervencije;
import com.sbnz.vehicleassessment.model.Trosak;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {
    private double vrednostVozila;
    private double vrednostOstataka;
    private double ukupniTroskovi;
    private Odluka odluka;
    private List<Faktor> faktori;
    private List<Trosak> troskovi;
    private List<Indikator> indikatori;
    private List<Eskalacija> eskalacije;
    private List<PromenaIntervencije> promene;
}
