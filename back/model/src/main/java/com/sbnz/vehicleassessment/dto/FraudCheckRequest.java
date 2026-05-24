package com.sbnz.vehicleassessment.dto;

import com.sbnz.vehicleassessment.model.event.ZahtevEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckRequest {
    private List<ZahtevEvent> zahtevi;
}
