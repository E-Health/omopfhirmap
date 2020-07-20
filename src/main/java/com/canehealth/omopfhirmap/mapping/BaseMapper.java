package com.canehealth.omopfhirmap.mapping;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.canehealth.omopfhirmap.models.BaseModel;

import com.canehealth.omopfhirmap.models.Person;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import lombok.Data;

@Data
public class BaseMapper<M extends BaseModel, F extends Resource>{
	// * OMOP TABLES ---------:-FHIR Resources          
    // * condition_occurrence : Condition
	// * device_exposure : DeviceUseStatement
	// * drug_exposure : Medication
	// * measurement & observation : Observation
	// * procedure_occurrence : Procecure
	// * visit_occurrence: : Encounter
	// * from application.properties : Location
	// * from application.properties : Provider

	private List<M> omopResources = new ArrayList<>();
	private List<F> fhirResources = new ArrayList<>();
	public M omopResource;
	public F fhirResource;
	// Create a FHIR context
	FhirContext ctx = FhirContext.forR4();

	public String encodeResourceToJsonString(){
		// Instantiate a new JSON parser
		IParser parser = ctx.newJsonParser();
		return parser.encodeResourceToString(this.fhirResource);
	}

	public String encodeResourceToXmlString(){
		return ctx.newXmlParser().encodeResourceToString(this.fhirResource);
	}

	public F parseResourceFromJsonString(String fhirResourceAsString){
		// Parse it
		IParser parser = ctx.newJsonParser();
		return (F) parser.parseResource(fhirResourceAsString);
	}

	public F mapOmopToFhir(){
		return fhirResource;
	}

	public M mapFhirToOmop(){
		return omopResource;
	}
}