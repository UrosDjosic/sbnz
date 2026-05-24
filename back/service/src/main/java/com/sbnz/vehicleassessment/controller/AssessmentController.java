package com.sbnz.vehicleassessment.controller;

import com.sbnz.vehicleassessment.dto.AssessmentRequest;
import com.sbnz.vehicleassessment.dto.AssessmentResponse;
import com.sbnz.vehicleassessment.repository.AssessmentRecord;
import com.sbnz.vehicleassessment.repository.AssessmentRepository;
import com.sbnz.vehicleassessment.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "*")
public class AssessmentController {

    private final AssessmentService service;
    private final AssessmentRepository repository;

    public AssessmentController(AssessmentService service, AssessmentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<AssessmentResponse> evaluate(@RequestBody AssessmentRequest request) {
        return ResponseEntity.ok(service.evaluate(request));
    }

    @GetMapping
    public List<AssessmentRecord> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentRecord> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
