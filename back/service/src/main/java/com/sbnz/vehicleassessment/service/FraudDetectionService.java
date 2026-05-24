package com.sbnz.vehicleassessment.service;

import com.sbnz.vehicleassessment.dto.FraudCheckRequest;
import com.sbnz.vehicleassessment.dto.FraudCheckResponse;
import com.sbnz.vehicleassessment.model.event.FraudAlert;
import com.sbnz.vehicleassessment.model.event.ZahtevEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private final KieContainer cepKieContainer;
    private KieSession cepSession;

    public FraudDetectionService(@Qualifier("cepKieContainer") KieContainer cepKieContainer) {
        this.cepKieContainer = cepKieContainer;
    }

    @PostConstruct
    public void init() {
        cepSession = cepKieContainer.newKieSession("fraudKSession");
    }

    @PreDestroy
    public void destroy() {
        if (cepSession != null) cepSession.dispose();
    }

    public synchronized FraudCheckResponse processEvents(FraudCheckRequest req) {
        if (req.getZahtevi() != null) {
            for (ZahtevEvent z : req.getZahtevi()) {
                cepSession.insert(z);
                cepSession.fireAllRules();
            }
        }
        List<FraudAlert> alerts = new ArrayList<>();
        for (Object o : cepSession.getObjects()) {
            if (o instanceof FraudAlert) alerts.add((FraudAlert) o);
        }
        return new FraudCheckResponse(alerts);
    }

    public synchronized FraudCheckResponse addEvent(ZahtevEvent z) {
        cepSession.insert(z);
        cepSession.fireAllRules();
        List<FraudAlert> alerts = new ArrayList<>();
        for (Object o : cepSession.getObjects()) {
            if (o instanceof FraudAlert) alerts.add((FraudAlert) o);
        }
        return new FraudCheckResponse(alerts);
    }
}
