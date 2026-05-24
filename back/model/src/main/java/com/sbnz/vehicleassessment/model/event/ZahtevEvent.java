package com.sbnz.vehicleassessment.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import java.util.Date;

@Role(Role.Type.EVENT)
@Timestamp("vremeZahteva")
@Expires("90d")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZahtevEvent {
    private String vlasnikId;
    private String voziloBroj;
    private double iznosZahteva;
    private Date vremeZahteva;
}
