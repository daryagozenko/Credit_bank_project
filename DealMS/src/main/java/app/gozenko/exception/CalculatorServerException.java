package app.gozenko.exception;

public class CalculatorServerException extends RuntimeException {
  public CalculatorServerException(String message) {
    super(message);
  }
}
