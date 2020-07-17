package com.canehealth.omopfhirmap.mapping;

import java.util.ArrayList;
import java.util.List;

import com.canehealth.omopfhirmap.models.BaseModel;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public abstract class BaseMapper<M extends BaseModel, F extends Resource>{
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

}