package com.nursinggame;

public class Financials {
  private double salary;
  private double savings;
  private double expenses;
  private double studentLoans;
  private double balance;
  private boolean isNightShift;
  private static final double NIGHT_SHIFT_DIFFERENTIAL = 1.2;

  public Financials(double startingSalary) {
    this.salary = startingSalary;
    this.savings = 1000.0;
    this.expenses = 0.0;
    this.studentLoans = 30000.0;
    this.balance = 0.0;
    this.isNightShift = false;
  }

  public double calculateShiftPay() {
    double basePay = salary / 365.0; // Daily rate
    return isNightShift ? basePay * NIGHT_SHIFT_DIFFERENTIAL : basePay;
  }

  public void adjustSalaryForPartialShift() {
    // Calculate half of the regular shift pay
    double partialShiftPay = calculateShiftPay() / 2.0;
    balance += partialShiftPay;
  }

  public void processShiftPay() {
    balance += calculateShiftPay();
  }

  public void receiveSalary() {
    double paycheck = salary / 52 / 3; // Salary per shift (assuming 3 shifts per week)
    savings += paycheck;
  }

  public void receiveNightShiftPay(double differential) {
    double paycheck = (salary / 52 / 3) * differential;
    savings += paycheck;
  }

  public boolean makePayment(double amount) {
    if (savings >= amount) {
      savings -= amount;
      return true;
    }
    return false;
  }

  public void payStudentLoans(double amount) {
    if (makePayment(amount)) {
      studentLoans = Math.max(0, studentLoans - amount);
    }
  }

  public void increaseSalary(double raise) {
    salary += raise;
  }

  // Salary adjustments
  public void adjustBaseSalary(double multiplier) {
    this.salary *= multiplier;
  }

  public void setNightShiftDifferential(boolean isNightShift) {
    this.isNightShift = isNightShift;
  }

  public void applyOvertimePay() {
    // Apply 1.5x overtime rate for the shift
    double regularShiftPay = calculateShiftPay();
    double overtimePay = regularShiftPay * 1.5;
    salary += (overtimePay - regularShiftPay); // Add the overtime differential
  }

  // Getters
  public double getSalary() {
    return salary;
  }

  public double getSavings() {
    return savings;
  }

  public double getExpenses() {
    return expenses;
  }

  public double getStudentLoans() {
    return studentLoans;
  }

  @Override
  public String toString() {
    return String.format("Salary: $%.2f/year, Savings: $%.2f, Student Loans: $%.2f",
        salary, savings, studentLoans);
  }
}