package com.sbnz.vehicleassessment.config;

import com.sbnz.vehicleassessment.model.BrandCoefficient;
import com.sbnz.vehicleassessment.model.RepairPolicy;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DroolsConfig {

    private static final String[] STATIC_DRL = {
        "rules/vehicle-evaluation.drl",
        "rules/damage-assessment.drl",
        "rules/total-loss.drl",
        "rules/backward-chaining.drl"
    };

    private static final String CEP_DRL = "rules/fraud-cep.drl";

    @Bean(name = "assessmentKieContainer")
    public KieContainer assessmentKieContainer() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId rid = ks.newReleaseId("com.sbnz", "assessment-kjar", "1.0.0");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kmm = ks.newKieModuleModel();
        KieBaseModel kbm = kmm.newKieBaseModel("assessmentKBase")
                .setDefault(true)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.CLOUD)
                .addPackage("rules");
        kbm.newKieSessionModel("assessmentKSession").setDefault(true);

        kfs.writeKModuleXML(kmm.toXML());

        for (String drl : STATIC_DRL) {
            kfs.write(ResourceFactory.newClassPathResource(drl).setResourceType(ResourceType.DRL));
        }

        String brandDrl = compileTemplate(
                "rules/brand-depreciation.drt",
                loadBrandData("rules/brand-coefficients.csv"));
        kfs.write("src/main/resources/rules/brand-depreciation.drl", brandDrl);

        String repairDrl = compileTemplate(
                "rules/repair-policy.drt",
                loadRepairData("rules/repair-policy.csv"));
        kfs.write("src/main/resources/rules/repair-policy.drl", repairDrl);

        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Drools assessment build errors:\n" + kb.getResults());
        }
        return ks.newKieContainer(rid);
    }

    @Bean(name = "cepKieContainer")
    public KieContainer cepKieContainer() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId rid = ks.newReleaseId("com.sbnz", "fraud-kjar", "1.0.0");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kmm = ks.newKieModuleModel();
        KieBaseModel kbm = kmm.newKieBaseModel("fraudKBase")
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM)
                .addPackage("rules");
        kbm.newKieSessionModel("fraudKSession")
                .setClockType(ClockTypeOption.get("realtime"));

        kfs.writeKModuleXML(kmm.toXML());
        kfs.write(ResourceFactory.newClassPathResource(CEP_DRL).setResourceType(ResourceType.DRL));

        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Drools CEP build errors:\n" + kb.getResults());
        }
        return ks.newKieContainer(rid);
    }

    private String compileTemplate(String templatePath, List<?> data) throws Exception {
        try (InputStream is = new ClassPathResource(templatePath).getInputStream()) {
            ObjectDataCompiler compiler = new ObjectDataCompiler();
            return compiler.compile(data, is);
        }
    }

    private List<BrandCoefficient> loadBrandData(String csvPath) throws Exception {
        List<BrandCoefficient> list = new ArrayList<>();
        try (InputStream is = new ClassPathResource(csvPath).getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",");
                list.add(new BrandCoefficient(
                        cols[0].trim(),
                        Double.parseDouble(cols[1]),
                        Double.parseDouble(cols[2]),
                        Double.parseDouble(cols[3]),
                        Double.parseDouble(cols[4])
                ));
            }
        }
        return list;
    }

    private List<RepairPolicy> loadRepairData(String csvPath) throws Exception {
        List<RepairPolicy> list = new ArrayList<>();
        try (InputStream is = new ClassPathResource(csvPath).getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            br.readLine(); // header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",");
                list.add(new RepairPolicy(
                        cols[0].trim(),
                        Double.parseDouble(cols[1]),
                        Boolean.parseBoolean(cols[2].trim()),
                        Boolean.parseBoolean(cols[3].trim())
                ));
            }
        }
        return list;
    }
}
