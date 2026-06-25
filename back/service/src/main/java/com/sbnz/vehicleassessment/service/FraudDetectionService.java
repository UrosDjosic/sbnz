package com.sbnz.vehicleassessment.service;

import com.sbnz.vehicleassessment.dto.FraudCheckRequest;
import com.sbnz.vehicleassessment.dto.FraudCheckResponse;
import com.sbnz.vehicleassessment.model.event.FraudAlert;
import com.sbnz.vehicleassessment.model.event.ProcenaSteteEvent;
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
        KieSession session = cepKieContainer.newKieSession("fraudKSession");
        try {
            if (req.getProcene() != null) {
                for (ProcenaSteteEvent p : req.getProcene()) {
                    session.insert(p);
                    session.fireAllRules();
                }
            }
            return new FraudCheckResponse(collectAlerts(session));
        } finally {
            session.dispose();
        }
    }

    public synchronized FraudCheckResponse addEvent(ProcenaSteteEvent z) {
        cepSession.insert(z);
        cepSession.fireAllRules();
        return new FraudCheckResponse(collectAlerts(cepSession));
    }

    private List<FraudAlert> collectAlerts(KieSession session) {
        List<FraudAlert> alerts = new ArrayList<>();
        for (Object o : session.getObjects()) {
            if (o instanceof FraudAlert) alerts.add((FraudAlert) o);
        }
        return alerts;
    }
}
