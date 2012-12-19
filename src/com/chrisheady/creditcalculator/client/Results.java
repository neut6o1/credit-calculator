package com.chrisheady.creditcalculator.client;

public class Results {
	private double initialPrincipal;
	private double monthlyPayment;
	private double interestRate;
	private double interestPaid;
	
	public Results(double initialPrincipal, double monthlyPayment,
			double interestRate, double interestPaid) {
		this.initialPrincipal = initialPrincipal;
		this.monthlyPayment = monthlyPayment;
		this.interestRate = interestRate;
		this.interestPaid = interestPaid;
	}
	
	public double getInitialPrincipal() {
		return initialPrincipal;
	}
	public void setInitialPrincipal(double initialPrincipal) {
		this.initialPrincipal = initialPrincipal;
	}
	public double getMonthlyPayment() {
		return monthlyPayment;
	}
	public void setMonthlyPayment(double monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public double getInterestPaid() {
		return interestPaid;
	}
	public void setInterestPaid(double interestPaid) {
		this.interestPaid = interestPaid;
	}
}
