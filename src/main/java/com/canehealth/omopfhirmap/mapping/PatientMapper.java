package com.canehealth.omopfhirmap.mapping;

import com.canehealth.omopfhirmap.models.Person;
import com.canehealth.omopfhirmap.services.PersonService;
import com.canehealth.omopfhirmap.utils.AddOmopKeyAsIdentifier;
import com.canehealth.omopfhirmap.utils.OmopConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class PatientMapper extends BaseMapper<Person, Patient>
                            implements IBaseMapper{

    @Value("${omopfhir.system.name}")
    private String myIdentifierSystem;

    @Autowired
    PersonService personService;

    public void mapOmopToFhir(){
        AddOmopKeyAsIdentifier<Person> addOmopKeyAsIdentifier = new AddOmopKeyAsIdentifier<>();
        this.fhirResource.addIdentifier(addOmopKeyAsIdentifier.add(this.omopResource, myIdentifierSystem));

        // gender	Σ	0..1	code	male | female | other | unknown
        if(this.omopResource.getGenderConceptId().equals(OmopConstants.OMOP_FEMALE))
            this.fhirResource.setGender(Enumerations.AdministrativeGender.FEMALE);
        else if (this.omopResource.getGenderConceptId().equals(OmopConstants.OMOP_MALE))
            this.fhirResource.setGender(Enumerations.AdministrativeGender.MALE);
        else
            this.fhirResource.setGender(Enumerations.AdministrativeGender.UNKNOWN);

        // birthDate	Σ	0..1	date	The date of birth for the individual
        Calendar calendar = Calendar.getInstance();
        int yob, mob, dob;
        if (this.omopResource.getYearOfBirth() != null)
            yob = this.omopResource.getYearOfBirth();
        else
            yob = 1;
        if (this.omopResource.getMonthOfBirth() != null)
            mob = this.omopResource.getMonthOfBirth();
        else
            mob = 1;
        if (this.omopResource.getDayOfBirth() != null)
            dob = this.omopResource.getDayOfBirth();
        else
            dob = 1;
        calendar.set(yob, mob - 1, dob);
        this.fhirResource.setBirthDate(calendar.getTime());

        // TODO: Map race and ethnicity.

        //  generalPractitioner		0..*	Reference(Organization | Practitioner | PractitionerRole)
        //  Patient's nominated primary care provider
        if(this.omopResource.getProviderId()!=null) {
            List<Reference> generalPractitioners = this.fhirResource.getGeneralPractitioner();
            Practitioner practitioner = new Practitioner();
            practitioner.setId(this.omopResource.getProviderId().toString());
            Reference reference = new Reference();
            reference.setResource(practitioner);
            generalPractitioners.add(reference);
            this.fhirResource.setGeneralPractitioner(generalPractitioners);
        }

        // managingOrganization	Σ	0..1	Reference(Organization)
        // Organization that is the custodian of the patient record
        if(this.omopResource.getLocationId()!=null) {
            Organization organization = new Organization();
            organization.setId(this.omopResource.getLocationId().toString());
            Reference reference = new Reference();
            reference.setResource(organization);
            this.fhirResource.setManagingOrganization(reference);
        }
    }

    public void mapFhirToOmop(){
            List<Identifier> identifiers = this.fhirResource.getIdentifier();
            for(Identifier identifier: identifiers){
                if(identifier.getSystem().equals(myIdentifierSystem)){
                    String myId = identifier.getValue();
                    Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                    List<Person> persons = personService.listByPersonAndPeriod(Integer.parseInt(myId), today , today);
                    if(persons.isEmpty()) {
                        System.out.println("Does not exist");
                        // person_source_value	varchar(50)
                        // An (encrypted) key derived from the person identifier in the source data.
                        // This is necessary when a use case requires a link back to the person data
                        // at the source dataset.
                        // TODO encrypt ID
                        this.omopResource.setPersonSourceValue(this.fhirResource.getId());
                        //year_of_birth month_of_birth day_of_birth
                        Calendar cal = Calendar.getInstance();
                        if(this.fhirResource.getBirthDate() != null) {
                            cal.setTime(this.fhirResource.getBirthDate());
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH) + 1; //0 -> 1
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            this.omopResource.setYearOfBirth(year);
                            this.omopResource.setMonthOfBirth(month);
                            this.omopResource.setDayOfBirth(day);
                        }
                    }
                    else {
                        // Exists
                        this.omopResource = null;
                    }
                }
            }
    }
}