package com.chrisheady.creditcalculator.client;

import java.util.ArrayList;
import java.util.List;

import com.chrisheady.creditcalculator.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CreditCalculatorPage implements EntryPoint {
	List<Results> results = new ArrayList<Results>();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button calculateButton = new Button("Calculate");
		final TextBox initialPrincipalField = new TextBox();
		initialPrincipalField.setText("9000");
		final TextBox monthlyPaymentField = new TextBox();
		monthlyPaymentField.setText("300");
		final TextBox interestRateField = new TextBox();
		interestRateField.setText("10");

		// We can add style names to widgets
		calculateButton.addStyleName("sendButton");

		// Add the initialPrincipalField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("initialPrincipalFieldContainer").add(initialPrincipalField);
		RootPanel.get("monthlyPaymentFieldContainer").add(monthlyPaymentField);
		RootPanel.get("interestRateFieldContainer").add(interestRateField);
		RootPanel.get("calculateButtonContainer").add(calculateButton);
		

		// Focus the cursor on the name field when the app loads
		initialPrincipalField.setFocus(true);
		initialPrincipalField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Interest Calculator");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label inputLabel = new Label();
		final HTML resultsLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Initial principal:</b>"));
		dialogVPanel.add(inputLabel);
		dialogVPanel.add(new HTML("<br><b>Interest charged:</b>"));
		dialogVPanel.add(resultsLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				displayResults();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					displayResults();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void displayResults() {
				// First, we validate the input
				String initialPrincipalString = initialPrincipalField.getText();
				String monthlyPaymentString = monthlyPaymentField.getText();
				String interestRateString = interestRateField.getText();
				
				RootPanel.get("errorLabelContainer").clear();
				
				boolean error = false;
				String errorLabelPostAmble = " is not a number.";
				RootPanel.get("errorLabelContainer").addStyleName("errorLabelContainer");
				if(!FieldVerifier.isValidDouble(initialPrincipalString)) {
					Label errorLabel = new Label();
					errorLabel.setText("*Initial Principal " + errorLabelPostAmble);
					RootPanel.get("errorLabelContainer").add(errorLabel);
					error = true;
				}
				if(!FieldVerifier.isValidDouble(monthlyPaymentString)) {
					Label errorLabel = new Label();
					errorLabel.setText("*Monthly Payment " + errorLabelPostAmble);
					RootPanel.get("errorLabelContainer").add(errorLabel);
					error = true;
				}
				if(!FieldVerifier.isValidDouble(interestRateString)) {
					Label errorLabel = new Label();
					errorLabel.setText("*Interest Rate " + errorLabelPostAmble);
					RootPanel.get("errorLabelContainer").add(errorLabel);
					error = true;
				}
				
					
				if(!error) {
					RootPanel.get("errorLabelContainer").removeStyleName("errorLabelContainer");
					
				    // Then, we calculate the results and show them.
					calculateButton.setEnabled(false);
					inputLabel.setText(initialPrincipalString);
					resultsLabel.setText("");
					
					double initialPrincipal = Double.parseDouble(initialPrincipalString);
					double monthlyPayment = Double.parseDouble(monthlyPaymentString);
					double interestRate = Double.parseDouble(interestRateString);
					
					List<RateMonths> rateMonthsList = new ArrayList<RateMonths>();
					RateMonths rateMonths = new RateMonths(interestRate, 24, 1);
					rateMonthsList.add(rateMonths);
					InterestCalculator interestCalculator = new InterestCalculator(initialPrincipal, monthlyPayment, rateMonthsList);
					double interestPaid = interestCalculator.calculate();
					String result = "" + interestPaid;
					
					// add the result to the beginning of the previous results so it shows first next render.
					results.add(0, new Results(initialPrincipal, monthlyPayment, 
							rateMonths.getPercentInterestRate(), interestPaid));
					
					resultsLabel.setHTML(result);
					dialogBox.setText("Result Box");
					dialogBox.center();
					closeButton.setFocus(true);
				}
			}
		}

		// Add a handler to send the name to the server
		final MyHandler handler = new MyHandler();
		calculateButton.addClickHandler(handler);
		initialPrincipalField.addKeyUpHandler(handler);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				calculateButton.setEnabled(true);
				calculateButton.setFocus(true);
				
				// clear all the previous calculation data before reprinting it.
				RootPanel.get("lastInitialPrincipalFieldContainer").clear();
				RootPanel.get("lastMonthlyPaymentFieldContainer").clear();
				RootPanel.get("lastInterestRateFieldContainer").clear();
				RootPanel.get("lastInterestPaidFieldContainer").clear();
				
				// add all of the previous data back in.
				for(Results nextResult: results) {
					RootPanel.get("lastInitialPrincipalFieldContainer").add(new Label("" + nextResult.getInitialPrincipal()));
					RootPanel.get("lastMonthlyPaymentFieldContainer").add(new Label("" + nextResult.getMonthlyPayment()));
					RootPanel.get("lastInterestRateFieldContainer").add(new Label("" + nextResult.getInterestRate()));
					RootPanel.get("lastInterestPaidFieldContainer").add(new Label("" + nextResult.getInterestPaid()));
				}
				RootPanel previousCalculationTable = RootPanel.get("previousCalculationTable");
				if(!previousCalculationTable.isVisible()) {
					previousCalculationTable.setVisible(true);
				}
			}
		});
	}
}
