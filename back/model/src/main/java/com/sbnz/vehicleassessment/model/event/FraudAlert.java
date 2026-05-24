package com.sbnz.vehicleassessment.model.event;

import com.sbnz.vehicleassessment.model.enums.TipEskalacije;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {
    private String vlasnikId;
    private String opis;
    private TipEskalacije nivo;
}
