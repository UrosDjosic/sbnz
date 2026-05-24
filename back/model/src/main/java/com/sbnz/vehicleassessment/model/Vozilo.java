package com.sbnz.vehicleassessment.model;

import com.sbnz.vehicleassessment.model.enums.KvalitetPopravki;
import com.sbnz.vehicleassessment.model.enums.TipKoriscenja;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vozilo {

    private String marka;
    private String model;
    private int godinaPrveRegistracije;
    private int predjenoKm;
    private int prosecnoKmKategorije;
    private double nabavnaCena;
    private int stanje;
    private TipKoriscenja tipKoriscenja;
    private boolean ranijeStete;
    private KvalitetPopravki kvalitetPrethodnih;
    private boolean airbagAktiviran;

    public int getGodinaStarosti() {
        return Year.now().getValue() - godinaPrveRegistracije;
    }

    public int getKmGodisnje() {
        int g = Math.max(1, getGodinaStarosti());
        return predjenoKm / g;
    }
}
