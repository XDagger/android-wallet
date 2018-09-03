package io.xdag.xdagwallet.util;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Java library to handle XDAG payment URI.
 * This library is fork from : https://github.com/SandroMachado/BitcoinPaymentURI
 *
 * The XIPs is available at: https://github.com/XDagger/XIPs/issues/4
 */

public class XdagPaymentURI {

	private static final String SCHEME = "xdag:";
	private static final String PARAMETER_AMOUNT = "amount";
	private static final String PARAMETER_LABEL = "label";
	private static final String PARAMETER_MESSAGE = "message";

	private final String address;
	private final HashMap<String, Parameter> parameters;

	private XdagPaymentURI(Builder builder) {
		this.address = builder.address;

		parameters = new HashMap<>();

		if (builder.amount != null) {
			parameters.put(PARAMETER_AMOUNT, new Parameter(String.valueOf(builder.amount), false));
		}

		if (builder.label != null) {
			parameters.put(PARAMETER_LABEL, new Parameter(builder.label, false));
		}

		if (builder.message != null) {
			parameters.put(PARAMETER_MESSAGE, new Parameter(builder.message, false));
		}

		if (builder.otherParameters != null) {
			parameters.putAll(builder.otherParameters);
		}
	}

	/**
	 * Gets the URI Xdag address.
	 *
	 * @return the URI Xdag address.
	 */

	public String getAddress() {
		return address;
	}

	/**
	 * Gets the URI amount.
	 *
	 * @return the URI amount.
	 */

	public Double getAmount() {
		if (parameters.get(PARAMETER_AMOUNT) == null) {
			return null;
		}

		return Double.valueOf(parameters.get(PARAMETER_AMOUNT).getValue());
	}

	/**
	 * Gets the URI label.
	 *
	 * @return the URI label.
	 */

	public String getLabel() {
		if (parameters.get(PARAMETER_LABEL) == null) {
			return null;
		}

		return parameters.get(PARAMETER_LABEL).getValue();
	}

	/**
	 * Gets the URI message.
	 *
	 * @return the URI message.
	 */

	public String getMessage() {
		if (parameters.get(PARAMETER_MESSAGE) == null) {
			return null;
		}

		return parameters.get(PARAMETER_MESSAGE).getValue();
	}

	/**
	 * Gets the URI parameters.
	 *
	 * @return the URI parameters.
	 */

	public HashMap<String, Parameter> getParameters() {
		HashMap<String, Parameter> filteredParameters = new HashMap<>(parameters);

		filteredParameters.remove(PARAMETER_AMOUNT);
		filteredParameters.remove(PARAMETER_LABEL);
		filteredParameters.remove(PARAMETER_MESSAGE);

		return filteredParameters;
	}

	/**
	 * Gets the URI.
	 *
	 * @return a string with the URI. This string can be used to make a Xdag payment.
	 */

	public String getURI() {
		String queryParameters = null;
		try {
			for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
				if (queryParameters == null) {
					if (entry.getValue().isRequired()) {
						queryParameters = String.format("req-%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8"));

						continue;
					}

					queryParameters = String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8"));

					continue;
				}

				if (entry.getValue().isRequired()) {
					queryParameters = String.format("%s&req-%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8"));

					continue;
				}

				queryParameters = String.format("%s&%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8"));
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}

		return String.format("%s%s%s", SCHEME, getAddress(), queryParameters == null ? "" : String.format("?%s", queryParameters));
	}

	/**
	 * Parses a string to a Xdag payment URI.
	 *
	 * @param string The string to be parsed.
	 *
	 * @return a Xdag payment URI if the URI is valid, or null for an invalid string.
	 */

	public static XdagPaymentURI parse(String string) {
		try {
			string = URLDecoder.decode(string,  "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}

		if (string == null) {
			return null;
		}

		if (string.isEmpty()) {
			return null;
		}

		if (!string.toLowerCase().startsWith(SCHEME)) {
			return null;
		}

		String xdagPaymentURIWithoutScheme = string.replaceFirst(SCHEME, "");
        ArrayList<String> xdagPaymentURIElements = new ArrayList<>(Arrays.asList(xdagPaymentURIWithoutScheme.split("\\?")));

        if (xdagPaymentURIElements.size() != 1 && xdagPaymentURIElements.size() != 2) {
        	return null;
        }

        if (xdagPaymentURIElements.get(0).length() == 0) {
        	return null;
        }

        if (xdagPaymentURIElements.size() == 1) {
        	return new Builder().address(xdagPaymentURIElements.get(0)).build();
        }

        List<String> queryParametersList = Arrays.asList(xdagPaymentURIElements.get(1).split("&"));

        if (queryParametersList.isEmpty()) {
        	return new Builder().address(xdagPaymentURIElements.get(0)).build();
        }

        HashMap<String, String> queryParametersFiltered = new HashMap<>();

        for (String query : queryParametersList) {
        	String[] queryParameter = query.split("=");

        	try {
            	queryParametersFiltered.put(queryParameter[0], queryParameter[1]);
        	}catch(ArrayIndexOutOfBoundsException exception) {
        		exception.printStackTrace();

        		return null;
        	}
        }

        Builder xdagPaymentURIBuilder = new Builder().address(xdagPaymentURIElements.get(0));

        if (queryParametersFiltered.containsKey(PARAMETER_AMOUNT)) {
        	xdagPaymentURIBuilder.amount(Double.valueOf(queryParametersFiltered.get(PARAMETER_AMOUNT)));

        	queryParametersFiltered.remove(PARAMETER_AMOUNT);
        }

        if (queryParametersFiltered.containsKey(PARAMETER_LABEL)) {
        	xdagPaymentURIBuilder.label(queryParametersFiltered.get(PARAMETER_LABEL));

        	queryParametersFiltered.remove(PARAMETER_LABEL);
        }

        if (queryParametersFiltered.containsKey(PARAMETER_MESSAGE)) {
        	xdagPaymentURIBuilder.message(queryParametersFiltered.get(PARAMETER_MESSAGE));

        	queryParametersFiltered.remove(PARAMETER_MESSAGE);
        }

		for (Map.Entry<String, String> entry : queryParametersFiltered.entrySet()) {
			xdagPaymentURIBuilder.parameter(entry.getKey(), entry.getValue());
		}

		return xdagPaymentURIBuilder.build();
	}

	public static class Builder{

		private String address;
		private Double amount;
		private String label;
		private String message;
		private HashMap<String, Parameter> otherParameters;

		/**
		 * Returns a builder for the Xdag payment URI.
		 */

		public Builder() {
		}

		/**
		 * Adds the address to the builder.
		 *
		 * @param address The address.
		 *
		 * @return the builder with the address.
		 */

		public Builder address(String address) {
			this.address = address;

			return this;
		}

		/**
		 * Adds the amount to the builder.
		 *
		 * @param amount The amount.
		 *
		 * @return the builder with the amount.
		 */

		public Builder amount(Double amount) {
			this.amount = amount;

			return this;
		}

		/**
		 * Adds the label to the builder.
		 *
		 * @param label The label.
		 *
		 * @return the builder with the label.
		 */

		public Builder label(String label) {
			this.label = label;

			return this;
		}

		/**
		 * Adds the message to the builder.
		 *
		 * @param message The message.
		 *
		 * @return the builder with the message.
		 */

		public Builder message(String message) {
			this.message = message;

			return this;
		}

		/**
		 * Adds a parameter to the builder.
		 *
		 * @param key The parameter.
		 * @param value The value.
		 *
		 * @return the builder with the parameter.
		 */

		public Builder parameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<>();
			}

			if (key.startsWith("req-")) {
				otherParameters.put(key.replace("req-", ""), new Parameter(value, true));

				return this;
			}

			otherParameters.put(key, new Parameter(value, false));

			return this;
		}

		/**
		 * Adds a required to the builder.
		 *
		 * @param key The key.
		 * @param value The value.
		 *
		 * @return the builder with the parameter.
		 */

		public Builder requiredParameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<>();
			}

			otherParameters.put(key, new Parameter(value, true));

			return this;
		}

		/**
		 * Builds a Xdag payment URI.
		 *
		 * @return a Xdag payment URI.
		 */

		public XdagPaymentURI build() {
			return new XdagPaymentURI(this);
		}

	}

}
