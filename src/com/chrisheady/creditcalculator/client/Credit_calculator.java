package com.chrisheady.creditcalculator.client;

import java.util.ArrayList;
import java.util.List;

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
public class Credit_calculator implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button("Calculate");
		final TextBox initialPrincipalField = new TextBox();
		initialPrincipalField.setText("9000");
		final TextBox monthlyPaymentField = new TextBox();
		monthlyPaymentField.setText("300");
		final TextBox interestRateField = new TextBox();
		interestRateField.setText("10");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the initialPrincipalField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("initialPrincipalFieldContainer").add(initialPrincipalField);
		RootPanel.get("monthlyPaymentFieldContainer").add(monthlyPaymentField);
		RootPanel.get("interestRateFieldContainer").add(interestRateField);
		RootPanel.get("calculateButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

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
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Initial principal:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Interest charged:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			private double lastInitialPrincipal;
			private double lastMonthlyPayment;
			private RateMonths lastRateMonths;
			private double lastInterestPaid;
			
			
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}
			
			public double getLastInitialPrincipal() {
				return lastInitialPrincipal;
			}
			
			public double getLastMonthlyPayment() {
				return lastMonthlyPayment;
			}
			
			public RateMonths getLastRateMonths() {
				return lastRateMonths;
			}
			
			public double getLastInterestPaid() {
				return lastInterestPaid;
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String initialPrincipalString = initialPrincipalField.getText();
				double initialPrincipal = Double.parseDouble(initialPrincipalString);
				String monthlyPaymentString = monthlyPaymentField.getText();
				double monthlyPayment = Double.parseDouble(monthlyPaymentString);
				String interestRateString = interestRateField.getText();
				double interestRate = Double.parseDouble(interestRateString);

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(initialPrincipalString);
				serverResponseLabel.setText("");
				
				List<RateMonths> rateMonthsList = new ArrayList<RateMonths>();
				RateMonths rateMonths = new RateMonths(interestRate, 24, 1);
				rateMonthsList.add(rateMonths);
				InterestCalculator interestCalculator = new InterestCalculator(initialPrincipal, monthlyPayment, rateMonthsList);
				double interestPaid = interestCalculator.calculate();
				final String response = "Interest charged: " + interestPaid;
				
				dialogBox.setText("Result Box");
				serverResponseLabel
						.removeStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(response);
				dialogBox.center();
				closeButton.setFocus(true);
				
				lastInitialPrincipal = initialPrincipal;
				lastMonthlyPayment = monthlyPayment;
				lastRateMonths = rateMonths;
				lastInterestPaid = interestPaid;
			}
		}

		// Add a handler to send the name to the server
		final MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		initialPrincipalField.addKeyUpHandler(handler);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
				
				RootPanel.get("lastInitialPrincipalFieldContainer").add(new Label("" + handler.getLastInitialPrincipal()));
				RootPanel.get("lastMonthlyPaymentFieldContainer").add(new Label("" + handler.getLastMonthlyPayment()));
				RootPanel.get("lastInterestRateFieldContainer").add(new Label("" + handler.getLastRateMonths().getPercentInterestRate()));
				RootPanel.get("lastInterestPaidFieldContainer").add(new Label("" + handler.getLastInterestPaid()));
				RootPanel previousCalculationTable = RootPanel.get("previousCalculationTable");
				if(!previousCalculationTable.isVisible()) {
					previousCalculationTable.setVisible(true);
				}
			}
		});
	}
}
