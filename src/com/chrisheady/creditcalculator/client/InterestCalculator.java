package com.chrisheady.creditcalculator.client;

import java.util.List;

/**
 * Calculates the amount of months to pay off debt as well as the interest costs.
 * 
 * @author chrisheady
 */

public class InterestCalculator {
	private double principal;
	private double monthlyPayment;
	private List<RateMonths> rateMonthsList;
	
	public InterestCalculator(double principal, double monthlyPayment, List<RateMonths> rateMonthsList) {
		this.principal = principal;
		this.monthlyPayment = monthlyPayment;
		this.rateMonthsList = rateMonthsList;
	}
	
	public String calculate() {
		int totalMonths = 0;
		double totalInterestCharged = 0;
		
		int compoundedAmountWithinMonth = 1; // 1 for monthly compounding, 30 for daily compounding
		
		rateMonthsList.add(new RateMonths(0, 12, compoundedAmountWithinMonth));
		rateMonthsList.add(new RateMonths(10, 24, compoundedAmountWithinMonth));
	
		for(RateMonths rateMonth : rateMonthsList) {
			if(principal < 0) {
				break;
			}
			
			int numberOfMonths = rateMonth.getNumberOfMonths();
			for(int j = 0; j < numberOfMonths; j++) {
				if(principal < 0) {
					break;
				}
				double monthlyInterestCharged = rateMonth.getMonthlyInterestCharge(principal);
				principal += monthlyInterestCharged;
				totalInterestCharged += monthlyInterestCharged;
				principal -= monthlyPayment;
				totalMonths++;
			}
		}
		
		System.out.println("Total Months: " + totalMonths);
		System.out.println("Money left to pay off: " + principal);
		String interestCharged = "Interest charged: " + totalInterestCharged;
		System.out.println(interestCharged);
		return interestCharged;
	}
}
