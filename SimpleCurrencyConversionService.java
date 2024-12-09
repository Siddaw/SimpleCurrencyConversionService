import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SimpleCurrencyConversionService {

    static class ExchangeRateProvider {
        private final Map<String, Double> rates;

        public ExchangeRateProvider(Map<String, Double> externalRates) {
            if (externalRates == null || externalRates.isEmpty()) {
                rates = new HashMap<>();
                rates.put("USD", 1.0); // Default rates
                rates.put("EUR", 0.85);
                rates.put("UGX", 3700.0);
                System.out.println("Warning: Using default exchange rates.");
            } else {
                rates = externalRates;
            }
        }

        public double getRate(String currency) {
            return rates.getOrDefault(currency, -1.0);
        }
    }


    static class TransactionLogger {
        public void log(String fromCurrency, String toCurrency, double amount, double rate) {
            System.out.printf("Transaction Log - %s -> %s | Amount: %.2f | Rate: %.2f | Time: %s%n",
                    fromCurrency, toCurrency, amount, rate, LocalDateTime.now());
        }
    }


    static class CurrencyConverter {
        private final ExchangeRateProvider rateProvider;
        private final TransactionLogger logger;

        public CurrencyConverter(ExchangeRateProvider rateProvider, TransactionLogger logger) {
            this.rateProvider = rateProvider;
            this.logger = logger;
        }

        public double convert(String fromCurrency, String toCurrency, double amount) throws IllegalArgumentException {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            double fromRate = rateProvider.getRate(fromCurrency);
            double toRate = rateProvider.getRate(toCurrency);

            if (fromRate <= 0 || toRate <= 0) {
                throw new IllegalArgumentException("Invalid currency code.");
            }

            double convertedAmount = (amount / fromRate) * toRate;
            logger.log(fromCurrency, toCurrency, amount, toRate / fromRate);
            return convertedAmount;
        }
    }

    // Main method
    public static void main(String[] args) {

        Map<String, Double> externalRates = new HashMap<>();
        externalRates.put("USD", 1.0);
        externalRates.put("EUR", 0.85);
        externalRates.put("UGX", 3800.0);


        ExchangeRateProvider rateProvider = new ExchangeRateProvider(externalRates);
        TransactionLogger logger = new TransactionLogger();
        CurrencyConverter converter = new CurrencyConverter(rateProvider, logger);


        System.out.println("Test 1: Valid Transaction");
        try {
            double result = converter.convert("USD", "EUR", 100);
            System.out.printf("Converted Amount: %.2f EUR%n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }


        System.out.println("\nTest 2: Invalid Amount");
        try {
            converter.convert("USD", "EUR", -50);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nTest 3: Invalid Currency Code");
        try {
            converter.convert("USD", "XYZ", 100);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }


        System.out.println("\nTest 4: Default Rates");
        ExchangeRateProvider defaultRateProvider = new ExchangeRateProvider(null);
        CurrencyConverter defaultConverter = new CurrencyConverter(defaultRateProvider, logger);
        try {
            double result = defaultConverter.convert("USD", "UGX", 1);
            System.out.printf("Converted Amount: %.2f UGX%n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
