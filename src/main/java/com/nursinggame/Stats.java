package com.nursinggame;

public class Stats {
  private int knowledge;
  private int patientCare;
  private int efficiency;
  private int stress;
  private int reputation;
  private int energy;
  private static final int MAX_STAT = 100;
  private static final int MIN_STAT = 0;

  public Stats() {
    this.knowledge = 50;
    this.patientCare = 50;
    this.efficiency = 50;
    this.stress = 0;
    this.reputation = 50;
    this.energy = 70;
  }

  public void improveKnowledge(int amount) {
    this.knowledge = Math.min(100, this.knowledge + amount);
  }

  public void improvePatientCare(int amount) {
    this.patientCare = Math.min(100, this.patientCare + amount);
  }

  public void improveEfficiency(int amount) {
    this.efficiency = Math.min(100, this.efficiency + amount);
  }

  public void addStress(int amount) {
    this.stress = Math.min(100, this.stress + amount);
  }

  public void reduceStress(int amount) {
    this.stress = Math.max(0, this.stress - amount);
  }

  public void decreasePatientCare(int amount) {
    patientCare = Math.max(MIN_STAT, patientCare - amount);
  }

  public void decreaseEfficiency(int amount) {
    efficiency = Math.max(MIN_STAT, efficiency - amount);
  }

  public void increaseEfficiency(int amount) {
    efficiency = Math.min(MAX_STAT, efficiency + amount);
  }

  public void increaseStress(int amount) {
    stress = Math.min(MAX_STAT, stress + amount);
  }

  public void decreaseReputation(int amount) {
    reputation = Math.max(MIN_STAT, reputation - amount);
  }

  // Getters
  public int getKnowledge() {
    return knowledge;
  }

  public int getPatientCare() {
    return patientCare;
  }

  public int getEfficiency() {
    return efficiency;
  }

  public int getStress() {
    return stress;
  }

  public int getReputation() {
    return reputation;
  }

  public int getEnergy() {
    return energy;
  }

  // Setters
  public void setKnowledge(int value) {
    this.knowledge = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void setPatientCare(int value) {
    this.patientCare = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void setEfficiency(int value) {
    this.efficiency = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void setEnergy(int value) {
    this.energy = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void setReputation(int value) {
    this.reputation = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void setStress(int value) {
    this.stress = Math.max(MIN_STAT, Math.min(MAX_STAT, value));
  }

  public void normalizeStats() {
    knowledge = Math.max(MIN_STAT, Math.min(MAX_STAT, knowledge));
    patientCare = Math.max(MIN_STAT, Math.min(MAX_STAT, patientCare));
    efficiency = Math.max(MIN_STAT, Math.min(MAX_STAT, efficiency));
    stress = Math.max(MIN_STAT, Math.min(MAX_STAT, stress));
    reputation = Math.max(MIN_STAT, Math.min(MAX_STAT, reputation));
    energy = Math.max(MIN_STAT, Math.min(MAX_STAT, energy));
  }

  @Override
  public String toString() {
    return "Knowledge: " + knowledge + "%, " +
        "Patient Care: " + patientCare + "%, " +
        "Efficiency: " + efficiency + "%, " +
        "Energy: " + energy + "%, " +
        "Reputation: " + reputation + "%, " +
        "Stress: " + stress + "%";
  }
}