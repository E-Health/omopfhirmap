package com.canehealth.omopfhirmap.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.canehealth.omopfhirmap.fetchers.*;
import com.canehealth.omopfhirmap.models.*;
import com.canehealth.omopfhirmap.services.CohortService;

import com.canehealth.omopfhirmap.utils.BundleProcessor;
import com.canehealth.omopfhirmap.utils.BundleRunnable;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Component
@Data
public class MainMapper {

    @Autowired
    CohortService cohortService;

    @Autowired
    PersonFetcher personFetcher;

    @Autowired
    ObservationFetcher observationFetcher;

    @Autowired
    MeasurementFetcher measurementFetcher;

    @Autowired
    DrugExposureFetcher drugExposureFetcher;

    @Autowired
    VisitOccurrenceFetcher visitOccurrenceFetcher;

    @Autowired
    ProcedureOccurrenceFetcher procedureOccurrenceFetcher;

    @Autowired
    PatientMapper patientMapper;

    @Autowired
    BundleProcessor bundleProcessor;

    private List<Cohort> cohorts = new ArrayList<>();
    private Cohort cohort;
    private int cohortId = 0;
    private int cohortSize = 0;
    private List<Person> persons = new ArrayList<>();
    private List<Observation> observations = new ArrayList<>();
    private List<Measurement> measurements = new ArrayList<>();
    private List<DrugExposure> drugExposures = new ArrayList<>();
    private List<VisitOccurrence> visitOccurrences = new ArrayList<>();
    private List<ProcedureOccurrence> procedureOccurrences = new ArrayList<>();

    // Create a FHIR context
	FhirContext ctx = FhirContext.forR4();


    public void fetchCohort() {
        if (this.cohortId > 0) {
            this.cohorts = this.cohortService.listByCohort(this.cohortId);
            this.cohortSize = this.cohorts.size();
        }
    }

    public void trimList(int ct){
        this.cohorts = this.cohorts.stream()
                .limit(ct)
                .collect(Collectors.toList());
    }

    public void fetchOmopResources() throws InterruptedException {
        personFetcher.setCohorts(this.cohorts);
        personFetcher.start();
        
        // @TODO uncomment along with implementations

        // observationFetcher.setCohorts(this.cohorts);
        // observationFetcher.start();

        // measurementFetcher.setCohorts(this.cohorts);
        // measurementFetcher.start();

        // drugExposureFetcher.setCohorts(this.cohorts);
        // drugExposureFetcher.start();

        // visitOccurrenceFetcher.setCohorts(this.cohorts);
        // visitOccurrenceFetcher.start();

        // procedureOccurrenceFetcher.setCohorts(this.cohorts);
        // procedureOccurrenceFetcher.start();


        // visitOccurrenceFetcher.join();
        // procedureOccurrenceFetcher.join();
        // drugExposureFetcher.join();
        // measurementFetcher.join();
        // observationFetcher.join();
        personFetcher.join();
        
        this.persons = personFetcher.getOmopResources();
        // this.observations = observationFetcher.getOmopResources();
        // this.measurements = measurementFetcher.getOmopResources();
        // this.drugExposures = drugExposureFetcher.getOmopResources();
        // this.visitOccurrences = visitOccurrenceFetcher.getOmopResources();
        // this.procedureOccurrences = procedureOccurrenceFetcher.getOmopResources();
    }

    public void createBundle() throws InterruptedException {
        fetchCohort();
        //TODO remove
        trimList(5);
        fetchOmopResources();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for(Person person: persons){
            System.out.println("Processing:" + person.getId().toString());
            BundleRunnable<Person, PatientMapper> myRunnable = new BundleRunnable<>(person, patientMapper, bundleProcessor);
            executor.execute(myRunnable);
        }
        executor.shutdown();
    }

    public void writeOmop(){

    }

    public String encodeBundleToJsonString(){
		// Instantiate a new JSON parser
		IParser parser = ctx.newJsonParser();
		return parser.encodeResourceToString(BundleProcessor.bundle);
	}

	public String encodeBundleoXmlString(){
		return ctx.newXmlParser().encodeResourceToString(BundleProcessor.bundle);
	}

	public Bundle parseBundleFromJsonString(String fhirBundleAsString){
		// Parse it
        IParser parser = ctx.newJsonParser();
        BundleProcessor.bundle = (Bundle) parser.parseResource(fhirBundleAsString);
		return BundleProcessor.bundle;
	}

}