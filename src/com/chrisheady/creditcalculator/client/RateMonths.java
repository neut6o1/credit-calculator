package com.chrisheady.creditcalculator.client;

public class RateMonths {
	public double percentInterestRate;
	public int numberOfMonths;
	public int compoundedAmountWithinMonth;

	public RateMonths(double percentInterestRate, int numberOfMonths,
			int compoundedAmountWithinMonth) {
		this.percentInterestRate = percentInterestRate;
		this.numberOfMonths = numberOfMonths;
		this.compoundedAmountWithinMonth = compoundedAmountWithinMonth;
	}
	
	public double getInterestRatePerCompounding() {
		return this.percentInterestRate / 100 / 12 / this.compoundedAmountWithinMonth;
	}
	
	
	public double getMonthlyInterestCharge(double principal) {
		double monthlyInterestCharged = 0;
		for(int i = 0; i < compoundedAmountWithinMonth; i++) {
			double interestRate = principal * getInterestRatePerCompounding();
			principal += interestRate;
			monthlyInterestCharged += interestRate;
		}
		System.out.println("Next month interest charged: " + monthlyInterestCharged);
		return monthlyInterestCharged;
	}
	
	public int getNumberOfMonths() {
		return numberOfMonths;
	}
}