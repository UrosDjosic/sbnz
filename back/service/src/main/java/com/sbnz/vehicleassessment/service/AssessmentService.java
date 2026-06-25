package com.sbnz.vehicleassessment.service;

import com.sbnz.vehicleassessment.dto.AssessmentRequest;
import com.sbnz.vehicleassessment.dto.AssessmentResponse;
import com.sbnz.vehicleassessment.model.Eskalacija;
import com.sbnz.vehicleassessment.model.Faktor;
import com.sbnz.vehicleassessment.model.Indikator;
import com.sbnz.vehicleassessment.model.Odluka;
import com.sbnz.vehicleassessment.model.Ostaci;
import com.sbnz.vehicleassessment.model.OstecenoVozilo;
import com.sbnz.vehicleassessment.model.PromenaIntervencije;
import com.sbnz.vehicleassessment.model.Trosak;
import com.sbnz.vehicleassessment.model.Vozilo;
import com.sbnz.vehicleassessment.model.VrednostVozila;
import com.sbnz.vehicleassessment.model.bc.DeoHijerarhija;
import com.sbnz.vehicleassessment.model.bc.KompatibilanSa;
import com.sbnz.vehicleassessment.model.bc.KriticniSklop;
import com.sbnz.vehicleassessment.model.bc.RezervniDeo;
import com.sbnz.vehicleassessment.model.enums.TipIndikatora;
import com.sbnz.vehicleassessment.model.event.ProcenaSteteEvent;
import com.sbnz.vehicleassessment.repository.AssessmentRecord;
import com.sbnz.vehicleassessment.repository.AssessmentRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentService {

    private final KieContainer assessmentKieContainer;
    private final AssessmentRepository repository;
    private final FraudDetectionService fraudDetectionService;

    public AssessmentService(@Qualifier("assessmentKieContainer") KieContainer assessmentKieContainer,
                             AssessmentRepository repository,
                             FraudDetectionService fraudDetectionService) {
        this.assessmentKieContainer = assessmentKieContainer;
        this.repository = repository;
        this.fraudDetectionService = fraudDetectionService;
    }

    public AssessmentResponse evaluate(AssessmentRequest request) {
        KieSession session = assessmentKieContainer.newKieSession("assessmentKSession");
        try {
            seedBackwardChainingFacts(session);

            session.insert(request.getVozilo());
            if (request.getOstecenja() != null) {
                for (OstecenoVozilo o : request.getOstecenja()) {
                    session.insert(o);
                }
            }

            session.fireAllRules();

            AssessmentResponse response = collectResponse(session);
            ProcenaSteteEvent event = buildFraudEvent(request, response);
            response.setFraudAlerti(fraudDetectionService.addEvent(event).getAlerti());
            persist(request, response);
            return response;
        } finally {
            session.dispose();
        }
    }

    private void seedBackwardChainingFacts(KieSession session) {
        // hijerarhija sklopova
        session.insert(new DeoHijerarhija("branik_prednji", "prednji_kraj"));
        session.insert(new DeoHijerarhija("hauba",          "prednji_kraj"));
        session.insert(new DeoHijerarhija("vetrobran",      "karoserija"));
        session.insert(new DeoHijerarhija("prednji_kraj",   "karoserija"));
        session.insert(new DeoHijerarhija("karoserija",     "VOZILO"));
        session.insert(new DeoHijerarhija("motor",          "pogonski_sklop"));
        session.insert(new DeoHijerarhija("turbina",        "motor"));
        session.insert(new DeoHijerarhija("pogonski_sklop", "VOZILO"));
        session.insert(new DeoHijerarhija("airbag",         "bezbednosni_sistem"));
        session.insert(new DeoHijerarhija("bezbednosni_sistem", "VOZILO"));
        session.insert(new DeoHijerarhija("sasija",         "VOZILO"));

        // kriticni sklopovi
        session.insert(new KriticniSklop("karoserija"));
        session.insert(new KriticniSklop("pogonski_sklop"));
        session.insert(new KriticniSklop("bezbednosni_sistem"));

        // rezervni delovi (primer)
        session.insert(new RezervniDeo("branik_kia_ceed_2015", false));
        session.insert(new RezervniDeo("branik_kia_ceed_2016", false));
        session.insert(new RezervniDeo("branik_aftermarket_X", true));
        session.insert(new KompatibilanSa("branik_kia_ceed_2015", "branik_kia_ceed_2016"));
        session.insert(new KompatibilanSa("branik_kia_ceed_2016", "branik_aftermarket_X"));
    }

    private AssessmentResponse collectResponse(KieSession session) {
        List<Faktor> faktori = new ArrayList<>();
        List<Trosak> troskovi = new ArrayList<>();
        List<Indikator> indikatori = new ArrayList<>();
        List<Eskalacija> eskalacije = new ArrayList<>();
        List<PromenaIntervencije> promene = new ArrayList<>();
        double vrednost = 0, ostaci = 0;
        Odluka odluka = null;

        for (Object f : session.getObjects()) {
            if (f instanceof Faktor)              faktori.add((Faktor) f);
            else if (f instanceof Trosak)         troskovi.add((Trosak) f);
            else if (f instanceof Indikator)      indikatori.add((Indikator) f);
            else if (f instanceof Eskalacija)     eskalacije.add((Eskalacija) f);
            else if (f instanceof PromenaIntervencije) promene.add((PromenaIntervencije) f);
            else if (f instanceof VrednostVozila) vrednost = ((VrednostVozila) f).getVrednost();
            else if (f instanceof Ostaci)         ostaci = ((Ostaci) f).getVrednost();
            else if (f instanceof Odluka)         odluka = (Odluka) f;
        }

        double ukupno = troskovi.stream().mapToDouble(Trosak::getIznos).sum();
        return new AssessmentResponse(vrednost, ostaci, ukupno, odluka,
                faktori, troskovi, indikatori, eskalacije, promene, new ArrayList<>());
    }

    private ProcenaSteteEvent buildFraudEvent(AssessmentRequest request, AssessmentResponse response) {
        Vozilo v = request.getVozilo();
        double procenatStete = response.getVrednostVozila() > 0
                ? (response.getUkupniTroskovi() / response.getVrednostVozila()) * 100.0
                : 0.0;
        boolean imaKriticniSklop = response.getIndikatori().stream()
                .anyMatch(i -> i.getTip() == TipIndikatora.KRITICNA_KOMPONENTA);
        double naknada = response.getOdluka() != null ? response.getOdluka().getNaknada() : 0.0;

        return new ProcenaSteteEvent(
                normalize(request.getBrojSasije(), v.getMarka() + "-" + v.getModel()),
                v.getMarka(),
                v.getModel(),
                new Date(),
                response.getVrednostVozila(),
                response.getUkupniTroskovi(),
                procenatStete,
                response.getOdluka() != null ? response.getOdluka().getTip() : null,
                request.getOstecenja() != null ? request.getOstecenja().size() : 0,
                imaKriticniSklop,
                naknada
        );
    }

    private String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private void persist(AssessmentRequest request, AssessmentResponse r) {
        Vozilo v = request.getVozilo();
        AssessmentRecord rec = new AssessmentRecord();
        rec.setMarka(v.getMarka());
        rec.setModel(v.getModel());
        rec.setBrojSasije(normalize(request.getBrojSasije(), v.getMarka() + "-" + v.getModel()));
        rec.setNabavnaCena(v.getNabavnaCena());
        rec.setVrednostVozila(r.getVrednostVozila());
        rec.setVrednostOstataka(r.getVrednostOstataka());
        rec.setUkupniTroskovi(r.getUkupniTroskovi());
        rec.setProcenatStete(r.getVrednostVozila() > 0
                ? (r.getUkupniTroskovi() / r.getVrednostVozila()) * 100.0
                : 0.0);
        rec.setBrojOstecenihDelova(request.getOstecenja() != null ? request.getOstecenja().size() : 0);
        rec.setImaKriticniSklop(r.getIndikatori().stream()
                .anyMatch(i -> i.getTip() == TipIndikatora.KRITICNA_KOMPONENTA));
        if (r.getOdluka() != null) {
            rec.setOdluka(r.getOdluka().getTip());
            rec.setNaknada(r.getOdluka().getNaknada());
            rec.setObrazlozenje(r.getOdluka().getObrazlozenje());
        }
        rec.setKreirano(Instant.now());
        repository.save(rec);
    }
}
