package com.canehealth.omopfhirmap.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "com.canehealth.omopfhirmap.model.ProcedureCost")
@Table(name = "procedure_cost")
public class ProcedureCost {

  @Id
  @Column(name = "\"_id\"", nullable = false)
  private Integer Id;
  @Column(name = "\"procedure_cost_id\"", nullable = false)
  private Integer procedureCostId;
  @Column(name = "\"procedure_occurrence_id\"", nullable = false)
  private Integer procedureOccurrenceId;
  @Column(name = "\"currency_concept_id\"", nullable = true)
  private Integer currencyConceptId;
  @Column(name = "\"paid_copay\"", nullable = true)
  private Float paidCopay;
  @Column(name = "\"paid_coinsurance\"", nullable = true)
  private Float paidCoinsurance;
  @Column(name = "\"paid_toward_deductible\"", nullable = true)
  private Float paidTowardDeductible;
  @Column(name = "\"paid_by_payer\"", nullable = true)
  private Float paidByPayer;
  @Column(name = "\"paid_by_coordination_benefits\"", nullable = true)
  private Float paidByCoordinationBenefits;
  @Column(name = "\"total_out_of_pocket\"", nullable = true)
  private Float totalOutOfPocket;
  @Column(name = "\"total_paid\"", nullable = true)
  private Float totalPaid;
  @Column(name = "\"revenue_code_concept_id\"", nullable = true)
  private Integer revenueCodeConceptId;
  @Column(name = "\"payer_plan_period_id\"", nullable = true)
  private Integer payerPlanPeriodId;
  @Column(name = "\"revenue_code_source_value\"", nullable = true)
  private String revenueCodeSourceValue;
}