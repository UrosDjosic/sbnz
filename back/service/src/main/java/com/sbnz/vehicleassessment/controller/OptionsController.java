package com.sbnz.vehicleassessment.controller;

import com.sbnz.vehicleassessment.repository.BrandCoefficientEntity;
import com.sbnz.vehicleassessment.repository.BrandCoefficientRepository;
import com.sbnz.vehicleassessment.repository.RepairPolicyEntity;
import com.sbnz.vehicleassessment.repository.RepairPolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/options")
@CrossOrigin(origins = "*")
public class OptionsController {

    private final BrandCoefficientRepository brandRepository;
    private final RepairPolicyRepository policyRepository;

    public OptionsController(BrandCoefficientRepository brandRepository, RepairPolicyRepository policyRepository) {
        this.brandRepository = brandRepository;
        this.policyRepository = policyRepository;
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getBrands() {
        List<String> brands = brandRepository.findAll()
            .stream()
            .map(BrandCoefficientEntity::getMarka)
            .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/car-parts")
    public ResponseEntity<List<String>> getCarParts() {
        List<String> parts = policyRepository.findAll()
            .stream()
            .map(RepairPolicyEntity::getDeo)
            .collect(Collectors.toList());
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/brands-full")
    public ResponseEntity<List<BrandCoefficientEntity>> getBrandsFull() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    @GetMapping("/car-parts-full")
    public ResponseEntity<List<RepairPolicyEntity>> getCarPartsFull() {
        return ResponseEntity.ok(policyRepository.findAll());
    }
}
