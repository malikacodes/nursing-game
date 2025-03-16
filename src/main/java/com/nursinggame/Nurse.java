package com.nursinggame;

public class Nurse {
  public enum Specialization {
    MED_SURG("Medical-Surgical", "General nursing care, post-operative care, and chronic condition management",
        new SpecializationAttributes(
            50000.0, // starting salary
            0.5, // experience multiplier
            -1, // stress per shift modifier
            2, // efficiency per shift modifier
            0.5, // reputation loss multiplier
            "Focus: General nursing care, post-operative care, and chronic condition management.\n\n" +
                "Strengths:\n" +
                "• Manages six to eight stable patients (Stress -5%, Efficiency +5%)\n" +
                "• Highly efficient documentation skills (Efficiency +10%, Patient Care -5%)\n" +
                "• Lower severity of mistakes (Reputation Loss -10%, Knowledge -5%)\n\n" +
                "Challenges:\n" +
                "• Slower clinical knowledge progression (Knowledge -10%, Efficiency +5%)\n" +
                "• Lower starting salary (Financial Growth -10%, Stress -5%)\n" +
                "• Frequent patient call lights (Efficiency -5%, Patient Care +5%)\n\n" +
                "Starting Stats Impact:\n" +
                "• Knowledge: -10%\n" +
                "• Patient Care: -5%\n" +
                "• Efficiency: +10%\n" +
                "• Stress: -5%\n" +
                "• Starting Salary: $50,000")),
    ICU("Intensive Care", "Critical care for unstable and high-acuity patients",
        new SpecializationAttributes(
            75000.0, // starting salary
            1.0, // experience multiplier
            3, // stress per shift modifier
            -2, // efficiency per shift modifier
            2.0, // reputation loss multiplier
            "Focus: Critical care for unstable and high-acuity patients.\n\n" +
                "Strengths:\n" +
                "• Manages only two critically ill patients (Knowledge +10%, Stress +10%)\n" +
                "• Higher salary (Financial Growth +20%, Stress +5%)\n" +
                "• Opportunities for advancement (Reputation +10%, Efficiency +5%)\n\n" +
                "Challenges:\n" +
                "• Hourly documentation requirements (Efficiency -5%, Patient Care -5%)\n" +
                "• High-stress environment (Reputation +10%, Stress +15%)\n" +
                "• Mistakes have severe consequences (Knowledge +5%, Reputation -15%)\n\n" +
                "Starting Stats Impact:\n" +
                "• Knowledge: +10%\n" +
                "• Patient Care: -5%\n" +
                "• Efficiency: -5%\n" +
                "• Stress: +15%\n" +
                "• Starting Salary: $75,000")),
    ER("Emergency", "Acute care and rapid assessment of emergent conditions",
        new SpecializationAttributes(
            65000.0, // starting salary
            1.2, // experience multiplier
            2, // stress per shift modifier
            2, // efficiency per shift modifier
            2.0, // reputation loss multiplier
            "Focus: Acute care and rapid assessment of emergent conditions.\n\n" +
                "Strengths:\n" +
                "• Quick decision-making improves efficiency (Efficiency +10%, Patient Care -5%)\n" +
                "• Higher pay incentives (Financial Growth +20%, Stress +5%)\n" +
                "• Lower fatigue accumulation (Stress -10%, Efficiency +5%)\n\n" +
                "Challenges:\n" +
                "• Unstable patient conditions (Knowledge +10%, Patient Care -10%)\n" +
                "• High initial stress load (Stress +15%, Efficiency +5%)\n" +
                "• Unpredictable workloads (Efficiency +5%, Stress +10%)\n\n" +
                "Starting Stats Impact:\n" +
                "• Knowledge: +10%\n" +
                "• Patient Care: -10%\n" +
                "• Efficiency: +15%\n" +
                "• Stress: +15%\n" +
                "• Starting Salary: $65,000"));

    private final String displayName;
    private final String description;
    private final SpecializationAttributes attributes;

    Specialization(String displayName, String description, SpecializationAttributes attributes) {
      this.displayName = displayName;
      this.description = description;
      this.attributes = attributes;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getDescription() {
      return description;
    }

    public SpecializationAttributes getAttributes() {
      return attributes;
    }

    public String getDetailedDescription() {
      return String.format("%s - %s\n\n%s",
          displayName, description, attributes.perksAndWeaknesses);
    }
  }

  private String name;
  private Specialization specialization;
  private Stats stats;
  private Financials financials;

  public Nurse(String name, Specialization specialization) {
    this.name = name;
    this.specialization = specialization;
    this.stats = new Stats();
    this.financials = new Financials(specialization.getAttributes().startingSalary);

    // Apply initial specialization effects
    if (specialization == Specialization.ER) {
      stats.addStress(3); // ER starts with higher stress
    }
  }

  public void applyShiftEffects(boolean isNightShift) {
    SpecializationAttributes attrs = specialization.getAttributes();

    // Apply stress modification
    stats.addStress(attrs.stressPerShiftModifier);

    // Apply efficiency modification
    stats.improveEfficiency(attrs.efficiencyPerShiftModifier);

    // Apply night shift additional effects
    if (isNightShift) {
      stats.addStress(1); // Additional stress for night shift
      if (specialization == Specialization.ER) {
        financials.receiveNightShiftPay(1.25); // ER gets 25% night differential
      } else {
        financials.receiveNightShiftPay(1.15); // Others get 15% night differential
      }
    }
  }

  // Getters
  public String getName() {
    return name;
  }

  public Specialization getSpecialization() {
    return specialization;
  }

  public Stats getStats() {
    return stats;
  }

  public Financials getFinancials() {
    return financials;
  }

  @Override
  public String toString() {
    return "Nurse " + name + "\n" +
        "Specialization: " + specialization.getDisplayName() + "\n" +
        "Stats: " + stats + "\n" +
        "Financials: " + financials;
  }
}

class SpecializationAttributes {
  final double startingSalary;
  final double experienceMultiplier;
  final int stressPerShiftModifier;
  final int efficiencyPerShiftModifier;
  final double reputationLossMultiplier;
  final String perksAndWeaknesses;

  public SpecializationAttributes(
      double startingSalary,
      double experienceMultiplier,
      int stressPerShiftModifier,
      int efficiencyPerShiftModifier,
      double reputationLossMultiplier,
      String perksAndWeaknesses) {
    this.startingSalary = startingSalary;
    this.experienceMultiplier = experienceMultiplier;
    this.stressPerShiftModifier = stressPerShiftModifier;
    this.efficiencyPerShiftModifier = efficiencyPerShiftModifier;
    this.reputationLossMultiplier = reputationLossMultiplier;
    this.perksAndWeaknesses = perksAndWeaknesses;
  }

  // Stat modifiers based on specialization descriptions
  public int getKnowledgeModifier() {
    if (perksAndWeaknesses.contains("Knowledge: +10%"))
      return 10;
    if (perksAndWeaknesses.contains("Knowledge: -10%"))
      return -10;
    if (perksAndWeaknesses.contains("Knowledge: +5%"))
      return 5;
    if (perksAndWeaknesses.contains("Knowledge: -5%"))
      return -5;
    return 0;
  }

  public int getPatientCareModifier() {
    if (perksAndWeaknesses.contains("Patient Care: +10%"))
      return 10;
    if (perksAndWeaknesses.contains("Patient Care: -10%"))
      return -10;
    if (perksAndWeaknesses.contains("Patient Care: +5%"))
      return 5;
    if (perksAndWeaknesses.contains("Patient Care: -5%"))
      return -5;
    return 0;
  }

  public int getEfficiencyModifier() {
    if (perksAndWeaknesses.contains("Efficiency: +15%"))
      return 15;
    if (perksAndWeaknesses.contains("Efficiency: +10%"))
      return 10;
    if (perksAndWeaknesses.contains("Efficiency: +5%"))
      return 5;
    if (perksAndWeaknesses.contains("Efficiency: -5%"))
      return -5;
    return 0;
  }

  public int getStressModifier() {
    if (perksAndWeaknesses.contains("Stress: +15%"))
      return 15;
    if (perksAndWeaknesses.contains("Stress: +10%"))
      return 10;
    if (perksAndWeaknesses.contains("Stress: +5%"))
      return 5;
    if (perksAndWeaknesses.contains("Stress: -5%"))
      return -5;
    if (perksAndWeaknesses.contains("Stress: -10%"))
      return -10;
    return 0;
  }

  public int getReputationModifier() {
    if (perksAndWeaknesses.contains("Reputation: +10%"))
      return 10;
    if (perksAndWeaknesses.contains("Reputation: -10%"))
      return -10;
    if (perksAndWeaknesses.contains("Reputation: +5%"))
      return 5;
    if (perksAndWeaknesses.contains("Reputation: -5%"))
      return -5;
    return 0;
  }
}