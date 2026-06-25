package com.sbnz.vehicleassessment.model.event;

import com.sbnz.vehicleassessment.model.enums.TipOdluke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("vremeProcene")
@Expires("370d")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcenaSteteEvent {
    private String brojSasije;
    private String marka;
    private String model;
    private Date vremeProcene;
    private double vrednostVozila;
    private double ukupniTroskovi;
    private double procenatStete;
    private TipOdluke tipOdluke;
    private int brojOstecenihDelova;
    private boolean imaKriticniSklop;
    private double naknada;
}
