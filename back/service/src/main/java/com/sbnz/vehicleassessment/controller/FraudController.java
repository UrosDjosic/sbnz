package com.sbnz.vehicleassessment.controller;

import com.sbnz.vehicleassessment.dto.FraudCheckRequest;
import com.sbnz.vehicleassessment.dto.FraudCheckResponse;
import com.sbnz.vehicleassessment.model.event.ZahtevEvent;
import com.sbnz.vehicleassessment.service.FraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@CrossOrigin(origins = "*")
public class FraudController {

    private final FraudDetectionService service;

    public FraudController(FraudDetectionService service) {
        this.service = service;
    }

    @PostMapping("/check")
    public ResponseEntity<FraudCheckResponse> check(@RequestBody FraudCheckRequest req) {
        return ResponseEntity.ok(service.processEvents(req));
    }

    @PostMapping("/event")
    public ResponseEntity<FraudCheckResponse> event(@RequestBody ZahtevEvent z) {
        return ResponseEntity.ok(service.addEvent(z));
    }
}
