package com.sbnz.vehicleassessment.config;

import com.sbnz.vehicleassessment.repository.BrandCoefficientEntity;
import com.sbnz.vehicleassessment.repository.BrandCoefficientRepository;
import com.sbnz.vehicleassessment.repository.RepairPolicyEntity;
import com.sbnz.vehicleassessment.repository.RepairPolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initBrandCoefficients(BrandCoefficientRepository brandRepository) {
        return args -> {
            if (brandRepository.count() == 0) {
                List<BrandCoefficientEntity> brands = loadBrandCoefficientsFromCSV();
                brandRepository.saveAll(brands);
            }
        };
    }

    @Bean
    public CommandLineRunner initRepairPolicies(RepairPolicyRepository policyRepository) {
        return args -> {
            if (policyRepository.count() == 0) {
                List<RepairPolicyEntity> policies = loadRepairPoliciesFromCSV();
                policyRepository.saveAll(policies);
            }
        };
    }

    private List<BrandCoefficientEntity> loadBrandCoefficientsFromCSV() throws IOException {
        List<BrandCoefficientEntity> brands = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("rules/brand-coefficients.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    BrandCoefficientEntity entity = new BrandCoefficientEntity(
                        parts[0].trim(),
                        Double.parseDouble(parts[1].trim()),
                        Double.parseDouble(parts[2].trim()),
                        Double.parseDouble(parts[3].trim()),
                        Double.parseDouble(parts[4].trim())
                    );
                    brands.add(entity);
                }
            }
        }
        
        return brands;
    }

    private List<RepairPolicyEntity> loadRepairPoliciesFromCSV() throws IOException {
        List<RepairPolicyEntity> policies = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("rules/repair-policy.csv");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    RepairPolicyEntity entity = new RepairPolicyEntity(
                        parts[0].trim(),
                        Double.parseDouble(parts[1].trim()),
                        Boolean.parseBoolean(parts[2].trim()),
                        Boolean.parseBoolean(parts[3].trim())
                    );
                    policies.add(entity);
                }
            }
        }
        
        return policies;
    }
}
