package com.sbnz.vehicleassessment.dto;

import com.sbnz.vehicleassessment.model.event.FraudAlert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckResponse {
    private List<FraudAlert> alerti;
}
