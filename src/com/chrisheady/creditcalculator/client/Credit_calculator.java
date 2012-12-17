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

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
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
				rateMonthsList.add(new RateMonths(interestRate, 24, 1));
				InterestCalculator interestCalculator = new InterestCalculator(initialPrincipal, monthlyPayment, rateMonthsList);
				final String response = interestCalculator.calculate();
				
				dialogBox.setText("Result Box");
				serverResponseLabel
						.removeStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(response);
				dialogBox.center();
				closeButton.setFocus(true);
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		initialPrincipalField.addKeyUpHandler(handler);
	}
}
