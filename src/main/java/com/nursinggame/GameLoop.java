package com.nursinggame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameLoop {
  private Nurse nurse;
  private List<Scenario> dayScenarios;
  private List<Scenario> nightScenarios;
  private Scanner scanner;
  private Random random;
  private int shift;
  private boolean isRunning;
  private ShiftType currentShift;
  private int consecutiveShifts;
  private final int MAX_CONSECUTIVE_SHIFTS = 3;
  private int questionsAnsweredThisShift;
  private final int QUESTIONS_PER_SHIFT = 3;
  private boolean isNewGrad; // Track if the player is a new graduate

  public enum ShiftType {
    DAY_FIRST("Day Shift (7AM-1PM)"),
    DAY_SECOND("Day Shift (1PM-7PM)"),
    NIGHT_FIRST("Night Shift (7PM-1AM)"),
    NIGHT_SECOND("Night Shift (1AM-7AM)");

    private final String description;

    ShiftType(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

    public boolean isNightShift() {
      return this == NIGHT_FIRST || this == NIGHT_SECOND;
    }
  }

  public GameLoop() {
    this.dayScenarios = new ArrayList<>();
    this.nightScenarios = new ArrayList<>();
    this.scanner = new Scanner(System.in);
    this.random = new Random();
    this.shift = 1;
    this.isRunning = false;
    this.currentShift = ShiftType.DAY_FIRST;
    this.consecutiveShifts = 0;
    this.questionsAnsweredThisShift = 0;
    initializeScenarios();
  }

  private void initializeScenarios() {
    // Basic scenarios that apply to all specialties
    initializeCommonScenarios();

    // Specialty-specific scenarios
    initializeICUScenarios();
    initializeERScenarios();
    initializeMedSurgScenarios();
  }

  private void initializeCommonScenarios() {
    if (isNewGrad) {
      initializeNewGradCommonScenarios();
    } else {
      initializeExperiencedCommonScenarios();
    }
  }

  private void initializeICUScenarios() {
    if (isNewGrad) {
      initializeNewGradICUScenarios();
    } else {
      initializeExperiencedICUScenarios();
    }
  }

  private void initializeERScenarios() {
    if (isNewGrad) {
      initializeNewGradERScenarios();
    } else {
      initializeExperiencedERScenarios();
    }
  }

  private void initializeMedSurgScenarios() {
    if (isNewGrad) {
      initializeNewGradMedSurgScenarios();
    } else {
      initializeExperiencedMedSurgScenarios();
    }
  }

  private void initializeNewGradCommonScenarios() {
    // Day shift scenarios
    Scenario medicationScenario = new Scenario(
        "Morning Medication Round",
        "During medication administration, you notice a patient's medication dosage seems higher than usual. What do you do?",
        1).addOption(
            "Double-check the medication order in the system",
            new ScenarioOutcome(
                "You found a prescription error and prevented a medication incident, but it delayed your other tasks.",
                2, 2, -2, -2, 0))
        .addOption(
            "Administer the medication as written to stay on schedule",
            new ScenarioOutcome("You maintained efficiency but missed a potential error.",
                -2, -2, 2, 2, 0));
    dayScenarios.add(medicationScenario);

    // Add follow-up to medication scenario
    Scenario medicationFollowUp = new Scenario(
        "Medication Error Prevention",
        "After finding the dosage error, what's your next step?",
        1).addOption(
            "Document the near-miss and report it to pharmacy",
            new ScenarioOutcome("This improves system safety but takes time from patient care.",
                2, -2, 2, -2, 0))
        .addOption(
            "Just correct the order and move on to patients",
            new ScenarioOutcome("You saved time but missed improving system safety.",
                -2, 2, -2, 2, 0));
    medicationScenario.addFollowUp(0, medicationFollowUp);

    dayScenarios.add(new Scenario(
        "Family Conference",
        "A patient's family is demanding to speak with the doctor about their care plan. The doctor is currently in a procedure.",
        2).addOption(
            "Listen to their concerns and explain you'll coordinate with the doctor when available",
            new ScenarioOutcome("The family feels heard, but other tasks got delayed.",
                -2, 2, -2, 2, 0))
        .addOption(
            "Tell them to wait and focus on completing your scheduled tasks",
            new ScenarioOutcome("You stayed on schedule but the family is upset.",
                2, -2, 2, -2, 0)));

    // Night shift common scenarios
    nightScenarios.add(new Scenario(
        "Night Round Assessment",
        "At 2 AM, your patient complains they can't sleep due to anxiety. Their vital signs are stable.",
        1).addOption(
            "Spend time talking with the patient about their concerns",
            new ScenarioOutcome("Patient feels better but you fell behind on documentation.",
                -2, 2, -2, 2, 0))
        .addOption(
            "Focus on completing your charting and request sleep medication",
            new ScenarioOutcome("Documentation is complete but patient satisfaction decreased.",
                2, -2, 2, -2, 0)));
  }

  private void initializeExperiencedCommonScenarios() {
    // Day shift scenarios
    Scenario medicationScenario = new Scenario(
        "Morning Medication Round",
        "During medication administration, you notice a patient's medication dosage seems higher than usual. What do you do?",
        1).addOption(
            "Double-check the medication order in the system",
            new ScenarioOutcome(
                "You found a prescription error and prevented a medication incident, but it delayed your other tasks.",
                2, 2, -2, -2, 0))
        .addOption(
            "Administer the medication as written to stay on schedule",
            new ScenarioOutcome("You maintained efficiency but missed a potential error.",
                -2, -2, 2, 2, 0));
    dayScenarios.add(medicationScenario);

    // Add follow-up to medication scenario
    Scenario medicationFollowUp = new Scenario(
        "Medication Error Prevention",
        "After finding the dosage error, what's your next step?",
        1).addOption(
            "Document the near-miss and report it to pharmacy",
            new ScenarioOutcome("This improves system safety but takes time from patient care.",
                2, -2, 2, -2, 0))
        .addOption(
            "Just correct the order and move on to patients",
            new ScenarioOutcome("You saved time but missed improving system safety.",
                -2, 2, -2, 2, 0));
    medicationScenario.addFollowUp(0, medicationFollowUp);

    dayScenarios.add(new Scenario(
        "Family Conference",
        "A patient's family is demanding to speak with the doctor about their care plan. The doctor is currently in a procedure.",
        2).addOption(
            "Listen to their concerns and explain you'll coordinate with the doctor when available",
            new ScenarioOutcome("The family feels heard, but other tasks got delayed.",
                -2, 2, -2, 2, 0))
        .addOption(
            "Tell them to wait and focus on completing your scheduled tasks",
            new ScenarioOutcome("You stayed on schedule but the family is upset.",
                2, -2, 2, -2, 0)));

    // Night shift common scenarios
    nightScenarios.add(new Scenario(
        "Night Round Assessment",
        "At 2 AM, your patient complains they can't sleep due to anxiety. Their vital signs are stable.",
        1).addOption(
            "Spend time talking with the patient about their concerns",
            new ScenarioOutcome("Patient feels better but you fell behind on documentation.",
                -2, 2, -2, 2, 0))
        .addOption(
            "Focus on completing your charting and request sleep medication",
            new ScenarioOutcome("Documentation is complete but patient satisfaction decreased.",
                2, -2, 2, -2, 0)));
  }

  private void initializeNewGradICUScenarios() {
    // Early Deterioration Scenario
    dayScenarios.add(new Scenario(
        "Early Signs of Deterioration",
        "Your intubated patient, admitted for sepsis, has stable vitals but suddenly becomes tachycardic (HR 120) and hypotensive (BP 80/50). The SpO2 remains at 97%.",
        2).addOption(
            "Increase IV fluids and reassess in 15 minutes",
            new ScenarioOutcome(
                "Patient stabilizes, but the underlying cause is not fully addressed.",
                5, 5, -5, 5, 0))
        .addOption(
            "Call the provider and request a sepsis reassessment",
            new ScenarioOutcome(
                "Early recognition leads to proper management, preventing further decline.",
                10, 5, -5, 5, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Ventilator Alarm Scenario
    dayScenarios.add(new Scenario(
        "Ventilator Alarm Management",
        "A patient on mechanical ventilation is suddenly triggering a high-pressure alarm. The respiratory therapist is unavailable for 10 minutes.",
        2).addOption(
            "Check for mucus plugging and attempt suctioning",
            new ScenarioOutcome(
                "Suctioning improves compliance, reducing the alarm.",
                10, 10, -5, 5, 0))
        .addOption(
            "Silence the alarm and wait for the respiratory therapist",
            new ScenarioOutcome(
                "Patient's condition worsens, and their oxygen saturation drops.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Medication Drip Scenario
    dayScenarios.add(new Scenario(
        "Vasopressor Management",
        "Your patient is on a norepinephrine drip for blood pressure support. The blood pressure has increased from 80/40 to 150/90.",
        2).addOption(
            "Titrate the norepinephrine down and monitor BP",
            new ScenarioOutcome(
                "BP stabilizes, avoiding potential hypertension.",
                10, 10, -5, 5, 0))
        .addOption(
            "Leave the drip rate unchanged and reassess in an hour",
            new ScenarioOutcome(
                "Hypertension worsens, leading to further interventions.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Sedation Assessment Scenario
    nightScenarios.add(new Scenario(
        "Sedation Level Assessment",
        "Your intubated patient is receiving continuous propofol sedation. The Richmond Agitation-Sedation Scale (RASS) is -4, but the physician ordered light sedation (RASS -2).",
        2).addOption(
            "Reduce the sedation per protocol and monitor response",
            new ScenarioOutcome(
                "Patient becomes more alert and follows commands.",
                10, 10, 5, -5, 0))
        .addOption(
            "Keep sedation unchanged and reassess later",
            new ScenarioOutcome(
                "Prolonged deep sedation delays extubation.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Rapid Response Scenario
    nightScenarios.add(new Scenario(
        "Acute Respiratory Distress",
        "A post-surgical ICU patient suddenly develops acute shortness of breath, tachypnea (RR 30), and a drop in SpO2 (88%). You suspect a pulmonary embolism (PE).",
        2).addOption(
            "Call the Rapid Response Team and prepare for intervention",
            new ScenarioOutcome(
                "Early intervention prevents further deterioration.",
                10, 10, -5, 5, 0))
        .addOption(
            "Increase oxygen and wait to see if symptoms resolve",
            new ScenarioOutcome(
                "Delayed diagnosis results in hemodynamic instability.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));
  }

  private void initializeExperiencedICUScenarios() {
    // Ventilator Weaning Challenge
    dayScenarios.add(new Scenario(
        "Ventilator Weaning Challenge",
        "Your ICU patient, previously sedated and ventilated for respiratory failure, is now on minimal ventilator support. The provider orders a spontaneous breathing trial (SBT), but the patient appears mildly tachypneic after 10 minutes.",
        3).addOption(
            "Stop the SBT and notify the provider",
            new ScenarioOutcome(
                "Extubation is delayed, but the patient avoids respiratory distress.",
                10, 10, -5, 5, 0))
        .addOption(
            "Encourage the patient to continue the trial",
            new ScenarioOutcome(
                "The patient fatigues, leading to a failed extubation attempt.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Septic Shock Crisis
    dayScenarios.add(new Scenario(
        "Managing a Septic Shock Crisis",
        "A post-op ICU patient with sepsis is hypotensive (BP 75/40) despite IV fluids and norepinephrine. The provider has not yet placed orders for additional interventions.",
        3).addOption(
            "Request vasopressin as a second-line agent",
            new ScenarioOutcome(
                "Blood pressure stabilizes with multimodal vasopressor therapy.",
                15, 10, -5, 5, 0))
        .addOption(
            "Continue IV fluids aggressively",
            new ScenarioOutcome(
                "Fluid overload leads to pulmonary edema.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Neurological Emergency
    dayScenarios.add(new Scenario(
        "Acute Neurological Decline",
        "A neuro ICU patient post-aneurysm coiling suddenly develops unequal pupils and a drop in GCS (Glasgow Coma Scale).",
        3).addOption(
            "Elevate the head of the bed, perform a neuro exam, and call neurosurgery",
            new ScenarioOutcome(
                "Prompt response prevents further deterioration.",
                10, 15, -5, 5, 0))
        .addOption(
            "Administer pain medications and reassess in 30 minutes",
            new ScenarioOutcome(
                "Delayed intervention results in permanent neurological damage.",
                -10, -15, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Multi-Organ Failure
    dayScenarios.add(new Scenario(
        "Multi-Organ Failure Management",
        "A patient with worsening acute kidney injury (AKI) now has elevated potassium (K+ 6.5) and peaked T-waves on ECG. The nephrologist is unavailable for 20 minutes.",
        3).addOption(
            "Administer calcium gluconate and insulin/glucose per protocol",
            new ScenarioOutcome(
                "Hyperkalemia is corrected, avoiding arrhythmias.",
                15, 10, -5, 5, 0))
        .addOption(
            "Call nephrology and wait for dialysis",
            new ScenarioOutcome(
                "Delayed treatment leads to ventricular fibrillation.",
                -10, -15, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Pulmonary Embolism
    dayScenarios.add(new Scenario(
        "Pulmonary Embolism Response",
        "A post-op ICU patient suddenly develops acute shortness of breath, tachypnea (RR 30), and a drop in SpO2 (88%). You suspect a pulmonary embolism (PE).",
        3).addOption(
            "Call the Rapid Response Team and prepare for intervention",
            new ScenarioOutcome(
                "Early intervention prevents further deterioration.",
                0, 3, -2, 2, 0))
        .addOption(
            "Increase oxygen and wait to see if symptoms resolve",
            new ScenarioOutcome(
                "Delayed diagnosis results in hemodynamic instability.",
                0, -3, 2, -1, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Code Leadership
    nightScenarios.add(new Scenario(
        "ICU Code Leadership",
        "During your shift, a patient in the next room goes into cardiac arrest. The charge nurse is unavailable, and you are the most senior nurse in the area.",
        3).addOption(
            "Take control, assign roles, and lead the code",
            new ScenarioOutcome(
                "The code runs smoothly, and the patient regains circulation.",
                15, 15, 10, 10, 0))
        .addOption(
            "Wait for the charge nurse or provider to arrive",
            new ScenarioOutcome(
                "Delayed interventions lead to prolonged downtime and worsened prognosis.",
                -10, -15, 5, 5, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Ethics Dilemma
    nightScenarios.add(new Scenario(
        "Ethics and Family Disagreement",
        "A critically ill patient on multiple vasopressors and mechanical ventilation is showing signs of irreversible multi-organ failure. The family is divided on continuing aggressive treatment versus comfort measures.",
        3).addOption(
            "Arrange a multidisciplinary family meeting with an ethics consult",
            new ScenarioOutcome(
                "A consensus is reached, reducing family distress.",
                10, 10, -5, 10, 0))
        .addOption(
            "Continue aggressive treatment and defer the conversation",
            new ScenarioOutcome(
                "Patient remains on futile treatment, increasing distress for staff and family.",
                -5, -10, 5, 15, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Ventilator Emergency
    nightScenarios.add(new Scenario(
        "High-Pressure Ventilator Alarm",
        "A patient on mechanical ventilation suddenly triggers a high-pressure alarm. The respiratory therapist is not available for 10 minutes.",
        3).addOption(
            "Check for mucus plugging and attempt suctioning",
            new ScenarioOutcome(
                "Suctioning improves compliance, reducing the alarm.",
                10, 10, -5, 5, 0))
        .addOption(
            "Silence the alarm and wait for the respiratory therapist",
            new ScenarioOutcome(
                "The patient's condition worsens, leading to an oxygen desaturation event.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Post-Intubation Crisis
    nightScenarios.add(new Scenario(
        "Post-Intubation Hypotension",
        "A septic patient was just intubated and is now showing a drop in blood pressure (70/40). The intensivist is managing another unstable patient.",
        3).addOption(
            "Start a vasopressor infusion per protocol",
            new ScenarioOutcome(
                "The blood pressure stabilizes, preventing further deterioration.",
                15, 10, -5, 5, 0))
        .addOption(
            "Give a fluid bolus and reassess in 10 minutes",
            new ScenarioOutcome(
                "BP remains low, and the patient develops worsening shock.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));

    // Delirium Management
    nightScenarios.add(new Scenario(
        "Managing Delirium in the ICU",
        "An elderly patient with sepsis and respiratory failure has been on mechanical ventilation for five days. Today, they are agitated, pulling at their ET tube, and exhibiting ICU delirium.",
        3).addOption(
            "Attempt non-pharmacologic interventions first",
            new ScenarioOutcome(
                "The patient calms down without additional sedation.",
                10, 15, -5, -5, 0))
        .addOption(
            "Increase sedation and restrain the patient",
            new ScenarioOutcome(
                "The patient is calmer, but deeper sedation delays ventilator weaning.",
                -5, -10, 5, 10, 0))
        .addRequiredSpecialization(Nurse.Specialization.ICU));
  }

  private void initializeNewGradERScenarios() {
    // Basic triage scenario
    dayScenarios.add(new Scenario(
        "Triage Assessment",
        "Two patients arrive: an elderly person with chest pain and a child with a fever of 101°F. Who do you assess first?",
        1).addOption(
            "Elderly patient with chest pain",
            new ScenarioOutcome("Correct triage priority but anxious parent waiting.",
                2, 2, -2, -2, 0))
        .addOption(
            "Child with fever",
            new ScenarioOutcome("Parent satisfied but delayed critical assessment.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));

    // Basic trauma scenario
    dayScenarios.add(new Scenario(
        "Simple Trauma",
        "A patient arrives with a laceration to their arm. Bleeding is controlled but they're anxious.",
        1).addOption(
            "Complete full set of vitals before cleaning wound",
            new ScenarioOutcome("Thorough assessment but increased patient anxiety.",
                2, 2, -2, -2, 0))
        .addOption(
            "Clean wound first to reassure patient",
            new ScenarioOutcome("Patient calmer but baseline assessment delayed.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));

    // Night shift scenario
    nightScenarios.add(new Scenario(
        "Intoxicated Patient",
        "An intoxicated patient is becoming verbally aggressive with staff.",
        1).addOption(
            "Call security and establish clear boundaries",
            new ScenarioOutcome("Safe approach but escalated patient agitation.",
                2, 2, -2, -2, 0))
        .addOption(
            "Try to de-escalate situation yourself",
            new ScenarioOutcome("Maintained calm but risked personal safety.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));
  }

  private void initializeExperiencedERScenarios() {
    // Complex trauma scenario
    dayScenarios.add(new Scenario(
        "Mass Casualty Incident",
        "Three critical trauma patients arrive simultaneously. One has tension pneumothorax symptoms.",
        3).addOption(
            "Focus on pneumothorax patient and delegate others",
            new ScenarioOutcome("Saved critical patient but delayed team organization.",
                2, 2, -2, -2, 0))
        .addOption(
            "Quickly triage all three and assign teams",
            new ScenarioOutcome("Good team coordination but delayed individual care.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));

    // Complex medical scenario
    dayScenarios.add(new Scenario(
        "Cardiac Emergency",
        "Code STEMI arrives. Cath lab is ready but patient develops V-tach during handoff.",
        3).addOption(
            "Start ACLS protocol and delay transfer",
            new ScenarioOutcome("Immediate rhythm management but delayed reperfusion.",
                2, 2, -2, -2, 0))
        .addOption(
            "Rapid transfer to cath lab with escort",
            new ScenarioOutcome("Quick reperfusion but risky transport.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));

    // Night shift complex scenario
    nightScenarios.add(new Scenario(
        "Pediatric Emergency",
        "4-year-old seizing patient arrives. IV access is difficult and parents are distraught.",
        3).addOption(
            "Attempt IV while another nurse gives IM medication",
            new ScenarioOutcome("Comprehensive care but delayed family support.",
                2, 2, -2, -2, 0))
        .addOption(
            "Give IM medication first and reassure family",
            new ScenarioOutcome("Quick symptom control but delayed IV access.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.ER));
  }

  private void initializeNewGradMedSurgScenarios() {
    // Basic post-op scenario
    dayScenarios.add(new Scenario(
        "First Post-Op Assessment",
        "Your post-appendectomy patient complains of 6/10 pain at the incision site.",
        1).addOption(
            "Complete full assessment before giving pain meds",
            new ScenarioOutcome("Thorough care but delayed pain relief.",
                2, 2, -2, -2, 0))
        .addOption(
            "Give pain medication first then assess",
            new ScenarioOutcome("Quick comfort but missed baseline assessment.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));

    // Basic patient education scenario
    dayScenarios.add(new Scenario(
        "Discharge Teaching",
        "Your diabetic patient is being discharged but seems unsure about insulin administration.",
        1).addOption(
            "Spend extra time teaching and have them demonstrate",
            new ScenarioOutcome("Good education but fell behind on other tasks.",
                2, 2, -2, -2, 0))
        .addOption(
            "Provide handouts and quick overview to stay on schedule",
            new ScenarioOutcome("Maintained schedule but risked patient compliance.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));

    // Night shift scenario
    nightScenarios.add(new Scenario(
        "New Admission",
        "You receive a new admission at 2 AM while all your other patients are stable.",
        1).addOption(
            "Complete full admission process now",
            new ScenarioOutcome("Thorough admission but disrupted other patients' sleep.",
                2, 2, -2, -2, 0))
        .addOption(
            "Quick safety checks and complete paperwork later",
            new ScenarioOutcome("Maintained quiet but delayed important documentation.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));
  }

  private void initializeExperiencedMedSurgScenarios() {
    // Complex post-op scenario
    dayScenarios.add(new Scenario(
        "Post-Op Complication",
        "Your post-colectomy patient develops rapid atrial fibrillation and complains of shortness of breath.",
        3).addOption(
            "Start oxygen and call rapid response",
            new ScenarioOutcome("Quick escalation but created unit chaos.",
                2, 2, -2, -2, 0))
        .addOption(
            "Assess thoroughly before calling rapid response",
            new ScenarioOutcome("Detailed assessment but delayed intervention.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));

    // Complex medical scenario
    dayScenarios.add(new Scenario(
        "Sepsis Alert",
        "Your pneumonia patient meets sepsis criteria but is refusing additional IV access.",
        3).addOption(
            "Take time to educate and gain cooperation",
            new ScenarioOutcome("Patient agreement gained but delayed treatment.",
                2, 2, -2, -2, 0))
        .addOption(
            "Call provider for alternative approach",
            new ScenarioOutcome("Maintained timeline but missed education opportunity.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));

    // Night shift complex scenario
    nightScenarios.add(new Scenario(
        "Acute Mental Status Change",
        "Your elderly patient becomes suddenly confused and tries to leave the unit at 3 AM.",
        3).addOption(
            "Full delirium assessment and family notification",
            new ScenarioOutcome("Comprehensive care but increased unit disruption.",
                2, 2, -2, -2, 0))
        .addOption(
            "Redirect patient and monitor closely",
            new ScenarioOutcome("Maintained calm but delayed full workup.",
                -2, -2, 2, 2, 0))
        .addRequiredSpecialization(Nurse.Specialization.MED_SURG));
  }

  public void start() {
    System.out.println("Welcome to Nursing Career Simulator!");
    System.out.print("Enter your name: ");
    String name = scanner.nextLine();

    // Ask if new graduate or experienced with stat previews
    System.out.println("\nChoose your starting level:");
    System.out.println("1. New Graduate Nurse");
    System.out.println("   Starting Stats:");
    System.out.println("   • Knowledge: 40% (Learning the basics)");
    System.out.println("   • Patient Care: 45% (Developing clinical skills)");
    System.out.println("   • Efficiency: 40% (Building time management)");
    System.out.println("   • Energy: 80% (Young and enthusiastic)");
    System.out.println("   • Reputation: 30% (Building trust)");
    System.out.println("   • Base Salary: 80% of specialty rate");
    System.out.println("\n2. Experienced Nurse");
    System.out.println("   Starting Stats:");
    System.out.println("   • Knowledge: 60% (Strong foundation)");
    System.out.println("   • Patient Care: 60% (Refined clinical skills)");
    System.out.println("   • Efficiency: 55% (Established workflow)");
    System.out.println("   • Energy: 60% (Seasoned endurance)");
    System.out.println("   • Reputation: 70% (Established credibility)");
    System.out.println("   • Base Salary: 120% of specialty rate");

    int experienceChoice;
    do {
      System.out.print("\nEnter your choice (1-2): ");
      try {
        experienceChoice = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        experienceChoice = 0;
      }
    } while (experienceChoice < 1 || experienceChoice > 2);

    isNewGrad = (experienceChoice == 1);

    // Specialization selection with detailed information
    System.out.println("\nChoose your specialization:");

    Nurse.Specialization[] specializations = Nurse.Specialization.values();
    for (int i = 0; i < specializations.length; i++) {
      System.out.println("Option " + (i + 1) + ":\n");
      System.out.println(specializations[i].getDetailedDescription());
      System.out.println("⸻\n");
    }

    int specialtyChoice;
    do {
      System.out.print("Enter your choice (1-" + specializations.length + "): ");
      try {
        specialtyChoice = Integer.parseInt(scanner.nextLine()) - 1;
      } catch (NumberFormatException e) {
        specialtyChoice = -1;
      }
    } while (specialtyChoice < 0 || specialtyChoice >= specializations.length);

    // Shift preference selection
    System.out.println("\nChoose your preferred shift:");
    System.out.println("1. Day Shift");
    System.out.println("   • Patient Care: +10% (More direct patient interaction)");
    System.out.println("   • Knowledge: +5% (More procedures and teaching opportunities)");
    System.out.println("   • Efficiency: -5% (More interruptions and tasks)");
    System.out.println("   • Energy: +10% (Natural sleep cycle)");
    System.out.println("   • Base Pay: Standard rate");
    System.out.println("\n2. Night Shift");
    System.out.println("   • Patient Care: -5% (Less patient interaction)");
    System.out.println("   • Knowledge: +10% (More independence in decision making)");
    System.out.println("   • Efficiency: +10% (Fewer interruptions)");
    System.out.println("   • Energy: -15% (Fighting natural sleep cycle)");
    System.out.println("   • Base Pay: +20% night differential");

    int shiftChoice;
    do {
      System.out.print("\nEnter your choice (1-2): ");
      try {
        shiftChoice = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        shiftChoice = 0;
      }
    } while (shiftChoice < 1 || shiftChoice > 2);

    boolean preferNightShift = (shiftChoice == 2);

    // Create nurse with adjusted starting stats based on choices
    nurse = new Nurse(name, specializations[specialtyChoice]);
    adjustStartingStats(isNewGrad, preferNightShift);

    // Set initial shift based on preference
    if (preferNightShift) {
      currentShift = ShiftType.NIGHT_FIRST;
      System.out.println("\nStarting on night shift (7PM-1AM)");
    } else {
      currentShift = ShiftType.DAY_FIRST;
      System.out.println("\nStarting on day shift (7AM-1PM)");
    }

    System.out.println("\nWelcome to " + nurse.getSpecialization().getDisplayName() + "!");
    System.out.println(nurse.getSpecialization().getDescription());

    // Show starting stats and pay
    System.out.println("\nStarting Stats:");
    System.out.println("Knowledge: " + nurse.getStats().getKnowledge() + "%");
    System.out.println("Patient Care: " + nurse.getStats().getPatientCare() + "%");
    System.out.println("Efficiency: " + nurse.getStats().getEfficiency() + "%");
    System.out.println("Energy: " + nurse.getStats().getEnergy() + "%");
    System.out.println("Reputation: " + nurse.getStats().getReputation() + "%");
    System.out.println("Stress: " + nurse.getStats().getStress() + "%");
    System.out.println("Starting Pay: $" + String.format("%.2f", nurse.getFinancials().getSalary()) + "/year");
    if (preferNightShift) {
      System.out.println("Night Shift Differential: +20%");
    }
    System.out.println();

    // Initialize scenarios based on experience level
    initializeScenarios();

    isRunning = true;

    while (isRunning) {
      displayStatus();
      processShift();

      if (consecutiveShifts >= MAX_CONSECUTIVE_SHIFTS) {
        System.out.println("\nYou've worked " + MAX_CONSECUTIVE_SHIFTS + " consecutive shifts. You must take a break.");
        takeBreak();
      } else {
        System.out.print("\nContinue to next shift? (y/n): ");
        String input = scanner.nextLine().toLowerCase();
        if (input.equals("n")) {
          isRunning = false;
        } else {
          advanceShift();
        }
      }
    }

    endGame();
  }

  private void adjustStartingStats(boolean isNewGrad, boolean preferNightShift) {
    Stats stats = nurse.getStats();
    Financials financials = nurse.getFinancials();

    // Base stat adjustments for experience level
    if (isNewGrad) {
      stats.setKnowledge(40); // New grads start with lower knowledge
      stats.setPatientCare(45);
      stats.setEfficiency(40);
      stats.setEnergy(80); // Higher energy as new grad
      stats.setReputation(30); // Lower starting reputation
      financials.adjustBaseSalary(0.8); // 80% of base specialty salary
    } else {
      stats.setKnowledge(60); // Experienced nurses start with higher knowledge
      stats.setPatientCare(60);
      stats.setEfficiency(55);
      stats.setEnergy(60); // Lower energy but more endurance
      stats.setReputation(70); // Higher starting reputation
      financials.adjustBaseSalary(1.2); // 120% of base specialty salary
    }

    // Apply specialty modifiers (these are already defined in the Specialization
    // enum)
    Nurse.Specialization spec = nurse.getSpecialization();
    SpecializationAttributes attrs = spec.getAttributes();

    // Apply specialty stat impacts
    stats.setKnowledge(stats.getKnowledge() + attrs.getKnowledgeModifier());
    stats.setPatientCare(stats.getPatientCare() + attrs.getPatientCareModifier());
    stats.setEfficiency(stats.getEfficiency() + attrs.getEfficiencyModifier());
    stats.setStress(stats.getStress() + attrs.getStressModifier());
    stats.setReputation(stats.getReputation() + attrs.getReputationModifier());

    // Shift preference adjustments
    if (preferNightShift) {
      stats.setKnowledge(stats.getKnowledge() + 10); // More independence in decision making
      stats.setEfficiency(stats.getEfficiency() + 10); // Fewer interruptions
      stats.setPatientCare(stats.getPatientCare() - 5); // Less patient interaction
      stats.setEnergy(stats.getEnergy() - 15); // Fighting natural sleep cycle
      financials.setNightShiftDifferential(true); // +20% night differential
    } else {
      stats.setPatientCare(stats.getPatientCare() + 10); // More direct patient interaction
      stats.setKnowledge(stats.getKnowledge() + 5); // More procedures and teaching
      stats.setEfficiency(stats.getEfficiency() - 5); // More interruptions
      stats.setEnergy(stats.getEnergy() + 10); // Natural sleep cycle
    }

    // Ensure all stats are within bounds
    stats.normalizeStats();
  }

  private void displayStatus() {
    System.out.println("\n=== Shift " + shift + " (" + currentShift.getDescription() + ") ===");
    System.out.println("Consecutive shifts worked: " + consecutiveShifts);
    System.out.println(nurse);
  }

  private void processShift() {
    questionsAnsweredThisShift = 0;
    // Apply specialization-specific shift effects
    nurse.applyShiftEffects(currentShift.isNightShift());

    // Get four different scenarios for this shift
    List<Scenario> shiftScenarios = new ArrayList<>();

    // Get first scenario
    Scenario scenario = getScenarioForShift();
    shiftScenarios.add(scenario);

    // Get second scenario (different from first)
    scenario = getScenarioForShift(shiftScenarios);
    shiftScenarios.add(scenario);

    // First half of shift (2 different scenarios)
    processScenario(shiftScenarios.get(0));
    questionsAnsweredThisShift++;
    processScenario(shiftScenarios.get(1));
    questionsAnsweredThisShift++;

    // Break question
    System.out.print("\nDo you need to leave early due to illness? (y/n): ");
    String input = scanner.nextLine().toLowerCase();
    if (input.equals("y")) {
      handleEarlyDeparture();
      return;
    }

    // Get two more different scenarios for second half
    scenario = getScenarioForShift(shiftScenarios);
    shiftScenarios.add(scenario);
    scenario = getScenarioForShift(shiftScenarios);
    shiftScenarios.add(scenario);

    // Second half of shift (2 more different scenarios)
    processScenario(shiftScenarios.get(2));
    questionsAnsweredThisShift++;
    processScenario(shiftScenarios.get(3));
    questionsAnsweredThisShift++;
  }

  private void processScenario(Scenario scenario) {
    if (scenario == null) {
      System.out.println("\nNo appropriate scenarios available for your specialization this shift.");
      return;
    }

    // Check for mistake probability based on efficiency
    int efficiency = nurse.getStats().getEfficiency();
    boolean mistakeMade = false;

    if (efficiency < 30) {
      // High chance of mistake (40%)
      mistakeMade = random.nextInt(100) < 40;
    } else if (efficiency < 50) {
      // Medium chance of mistake (20%)
      mistakeMade = random.nextInt(100) < 20;
    } else if (efficiency < 70) {
      // Low chance of mistake (10%)
      mistakeMade = random.nextInt(100) < 10;
    }

    // Display scenario title and description
    System.out.println("\n=== " + scenario.getTitle() + " ===");
    System.out.println(scenario.getDescription() + "\n");

    if (mistakeMade) {
      System.out.println("WARNING: Due to low efficiency, you made a mistake!");
      System.out.println("• Patient Care decreased by 10%");
      System.out.println("• Reputation decreased by 5%");
      System.out.println("• Stress increased by 10%\n");

      nurse.getStats().decreasePatientCare(10);
      nurse.getStats().decreaseReputation(5);
      nurse.getStats().increaseStress(10);
    }

    // Display options without showing potential outcomes
    List<String> options = scenario.getOptions();
    for (int i = 0; i < options.size(); i++) {
      System.out.println((i + 1) + ") " + options.get(i));
    }
    System.out.println(); // Add blank line between options

    int choice;
    do {
      System.out.print("Enter your choice (1-" + options.size() + "): ");
      try {
        choice = Integer.parseInt(scanner.nextLine()) - 1;
      } catch (NumberFormatException e) {
        choice = -1;
      }
    } while (choice < 0 || choice >= options.size());

    // Store initial stats for comparison
    int initialKnowledge = nurse.getStats().getKnowledge();
    int initialPatientCare = nurse.getStats().getPatientCare();
    int initialEfficiency = nurse.getStats().getEfficiency();
    int initialStress = nurse.getStats().getStress();
    int initialReputation = nurse.getStats().getReputation();

    ScenarioResult result = scenario.selectOption(choice, nurse);
    System.out.println("\nOutcome: " + result.getOutcome().getDescription());

    // Display actual stat changes after the choice is made
    System.out.println("\nImpact of your decision:");
    displayStatChange("Knowledge", initialKnowledge, nurse.getStats().getKnowledge());
    displayStatChange("Patient Care", initialPatientCare, nurse.getStats().getPatientCare());
    displayStatChange("Efficiency", initialEfficiency, nurse.getStats().getEfficiency());
    displayStatChange("Stress", initialStress, nurse.getStats().getStress());
    displayStatChange("Reputation", initialReputation, nurse.getStats().getReputation());
    System.out.println(); // Add blank line for readability

    // Handle follow-up scenario if it exists
    if (result.hasFollowUp()) {
      System.out.println("\nFollow-up situation:");
      processScenario(result.getFollowUpScenario());
    }
  }

  private void displayPotentialChange(String statName, int change) {
    if (change != 0) {
      String changeSymbol = change > 0 ? "+" : "";
      System.out.printf("%s: %s%d%n", statName, changeSymbol, change);
    }
  }

  private void displayStatChange(String statName, int oldValue, int newValue) {
    int change = newValue - oldValue;
    if (change != 0) {
      String changeSymbol = change > 0 ? "+" : "";
      System.out.printf("%s: %s%d points (%d → %d)%n",
          statName, changeSymbol, change, oldValue, newValue);
    }
  }

  private Scenario getScenarioForShift() {
    List<Scenario> currentScenarios = (currentShift.isNightShift()) ? nightScenarios : dayScenarios;

    // Filter scenarios by nurse's specialization
    List<Scenario> appropriateScenarios = currentScenarios.stream()
        .filter(s -> s.isApplicableToNurse(nurse))
        .collect(Collectors.toList());

    if (appropriateScenarios.isEmpty()) {
      return null;
    }

    return appropriateScenarios.get(random.nextInt(appropriateScenarios.size()));
  }

  private Scenario getScenarioForShift(List<Scenario> excludeScenarios) {
    List<Scenario> currentScenarios = (currentShift.isNightShift()) ? nightScenarios : dayScenarios;

    // Filter scenarios by nurse's specialization and exclude the given scenarios
    List<Scenario> appropriateScenarios = currentScenarios.stream()
        .filter(s -> s.isApplicableToNurse(nurse) && !excludeScenarios.contains(s))
        .collect(Collectors.toList());

    if (appropriateScenarios.isEmpty()) {
      return null;
    }

    return appropriateScenarios.get(random.nextInt(appropriateScenarios.size()));
  }

  private void advanceShift() {
    shift++;
    consecutiveShifts++;
    // Cycle through shifts
    currentShift = switch (currentShift) {
      case DAY_FIRST -> ShiftType.DAY_SECOND;
      case DAY_SECOND -> ShiftType.NIGHT_FIRST;
      case NIGHT_FIRST -> ShiftType.NIGHT_SECOND;
      case NIGHT_SECOND -> ShiftType.DAY_FIRST;
    };
  }

  private void takeBreak() {
    // Check if efficiency is too low for overtime
    boolean efficiencyTooLow = nurse.getStats().getEfficiency() < 30;

    if (!efficiencyTooLow) {
      System.out.println("\nYou've worked " + MAX_CONSECUTIVE_SHIFTS + " consecutive shifts.");
      System.out.println("1. Take a mandatory break");
      System.out.println("2. Work overtime (Warning: This will decrease efficiency and increase mistake probability)");

      int choice;
      do {
        System.out.print("\nEnter your choice (1-2): ");
        try {
          choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
          choice = 0;
        }
      } while (choice < 1 || choice > 2);

      if (choice == 2) {
        // Work overtime
        nurse.getStats().decreaseEfficiency(15);
        nurse.getStats().increaseStress(20);
        nurse.getFinancials().applyOvertimePay();
        consecutiveShifts = 0; // Reset consecutive shifts after overtime

        System.out.println("\nWorking overtime...");
        System.out.println("• Efficiency decreased by 15%");
        System.out.println("• Stress increased by 20%");
        System.out.println("• Overtime pay: 1.5x regular rate");

        // Check if efficiency is now dangerously low
        if (nurse.getStats().getEfficiency() < 30) {
          System.out.println("\nWARNING: Your efficiency is dangerously low!");
          System.out.println("You are now more likely to make mistakes in patient care.");
        }
        return;
      }
    } else {
      System.out
          .println("\nYour efficiency is too low (" + nurse.getStats().getEfficiency() + "%) to safely work overtime.");
      System.out.println("You must take a break to recover.");
    }

    // Take break
    consecutiveShifts = 0;
    nurse.getStats().reduceStress(10);
    nurse.getStats().increaseEfficiency(20);
    System.out.println("\nYou took a mandatory break.");
    System.out.println("• Stress decreased by 10%");
    System.out.println("• Efficiency increased by 20%");
    System.out.println("\nReturning to " + currentShift.getDescription());
  }

  private void handleEarlyDeparture() {
    System.out.println("\nLeaving shift early due to illness...");

    // Get the full shift pay before reduction
    double fullShiftPay = nurse.getFinancials().calculateShiftPay();
    double partialShiftPay = fullShiftPay / 2.0;

    // Reduce pay by 50%
    nurse.getFinancials().adjustSalaryForPartialShift();
    // Decrease reputation
    nurse.getStats().decreaseReputation(5);
    // Reduce stress and increase energy from rest
    nurse.getStats().reduceStress(15);
    nurse.getStats().increaseEfficiency(10);

    // Show all the changes with +/- indicators
    System.out.printf("Pay: -$%.2f (Received: $%.2f instead of $%.2f)\n",
        fullShiftPay - partialShiftPay, partialShiftPay, fullShiftPay);
    System.out.println("Reputation: -5 points");
    System.out.println("Stress: -15 points (Getting some rest)");
    System.out.println("Energy: +10 points (Recovery time)");
  }

  private void endGame() {
    System.out.println("\n=== Game Over ===");
    System.out.println(
        "You completed " + (shift - 1) + " shifts as a " + nurse.getSpecialization().getDisplayName() + " nurse!");
    System.out.println("\nFinal Status:");
    System.out.println("Stats:");
    System.out.println("• Knowledge: " + nurse.getStats().getKnowledge() + "%");
    System.out.println("• Patient Care: " + nurse.getStats().getPatientCare() + "%");
    System.out.println("• Efficiency: " + nurse.getStats().getEfficiency() + "%");
    System.out.println("• Energy: " + nurse.getStats().getEnergy() + "%");
    System.out.println("• Reputation: " + nurse.getStats().getReputation() + "%");
    System.out.println("• Stress: " + nurse.getStats().getStress() + "%");
    System.out.println("\nFinancial Status:");
    System.out.println("• Final Salary: $" + String.format("%.2f", nurse.getFinancials().getSalary()) + "/year");
  }
}