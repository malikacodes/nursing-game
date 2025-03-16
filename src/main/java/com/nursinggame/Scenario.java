package com.nursinggame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scenario {
  private String title;
  private String description;
  private List<String> options;
  private List<ScenarioOutcome> outcomes;
  private int difficulty;
  private Map<Integer, Scenario> followUpScenarios;
  private List<Nurse.Specialization> requiredSpecializations;
  private boolean isFollowUp;

  public Scenario(String title, String description, int difficulty) {
    this.title = title;
    this.description = description;
    this.difficulty = difficulty;
    this.options = new ArrayList<>();
    this.outcomes = new ArrayList<>();
    this.followUpScenarios = new HashMap<>();
    this.requiredSpecializations = new ArrayList<>();
    this.isFollowUp = false;
  }

  public Scenario addOption(String option, ScenarioOutcome outcome) {
    options.add(option);
    outcomes.add(outcome);
    return this;
  }

  public Scenario addFollowUp(int optionIndex, Scenario followUp) {
    followUp.isFollowUp = true;
    followUpScenarios.put(optionIndex, followUp);
    return this;
  }

  public Scenario addRequiredSpecialization(Nurse.Specialization specialization) {
    requiredSpecializations.add(specialization);
    return this;
  }

  public boolean isApplicableToNurse(Nurse nurse) {
    if (requiredSpecializations.isEmpty()) {
      return true; // Scenario applies to all specializations
    }
    return requiredSpecializations.contains(nurse.getSpecialization());
  }

  public ScenarioResult selectOption(int choice, Nurse nurse) {
    if (choice >= 0 && choice < outcomes.size()) {
      ScenarioOutcome outcome = outcomes.get(choice);
      if (nurse != null) {
        outcome.apply(nurse);
      }

      // Check if there's a follow-up scenario
      Scenario followUp = followUpScenarios.get(choice);
      return new ScenarioResult(outcome, followUp);
    }
    return new ScenarioResult(null, null);
  }

  public ScenarioOutcome previewOutcome(int choice) {
    if (choice >= 0 && choice < outcomes.size()) {
      return outcomes.get(choice);
    }
    return null;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getOptions() {
    return new ArrayList<>(options);
  }

  public int getDifficulty() {
    return difficulty;
  }

  public boolean hasFollowUp(int choice) {
    return followUpScenarios.containsKey(choice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== ").append(title).append(" ===\n");
    sb.append(description).append("\n\n");
    sb.append("Options:\n");
    for (int i = 0; i < options.size(); i++) {
      sb.append(i + 1).append(". ").append(options.get(i)).append("\n");
    }
    return sb.toString();
  }
}

class ScenarioResult {
  private final ScenarioOutcome outcome;
  private final Scenario followUpScenario;

  public ScenarioResult(ScenarioOutcome outcome, Scenario followUpScenario) {
    this.outcome = outcome;
    this.followUpScenario = followUpScenario;
  }

  public ScenarioOutcome getOutcome() {
    return outcome;
  }

  public Scenario getFollowUpScenario() {
    return followUpScenario;
  }

  public boolean hasFollowUp() {
    return followUpScenario != null;
  }
}

class ScenarioOutcome {
  private String description;
  private int knowledgeChange;
  private int patientCareChange;
  private int efficiencyChange;
  private int stressChange;
  private double financialChange;

  public ScenarioOutcome(String description, int knowledgeChange, int patientCareChange,
      int efficiencyChange, int stressChange, double financialChange) {
    this.description = description;
    this.knowledgeChange = knowledgeChange;
    this.patientCareChange = patientCareChange;
    this.efficiencyChange = efficiencyChange;
    this.stressChange = stressChange;
    this.financialChange = financialChange;
  }

  public void apply(Nurse nurse) {
    Stats stats = nurse.getStats();
    Financials financials = nurse.getFinancials();

    if (knowledgeChange != 0)
      stats.improveKnowledge(knowledgeChange);
    if (patientCareChange != 0)
      stats.improvePatientCare(patientCareChange);
    if (efficiencyChange != 0)
      stats.improveEfficiency(efficiencyChange);
    if (stressChange > 0)
      stats.addStress(stressChange);
    if (stressChange < 0)
      stats.reduceStress(-stressChange);
    if (financialChange != 0)
      financials.makePayment(-financialChange);
  }

  public String getDescription() {
    return description;
  }

  public int getKnowledgeChange() {
    return knowledgeChange;
  }

  public int getPatientCareChange() {
    return patientCareChange;
  }

  public int getEfficiencyChange() {
    return efficiencyChange;
  }

  public int getStressChange() {
    return stressChange;
  }

  public double getFinancialChange() {
    return financialChange;
  }
}