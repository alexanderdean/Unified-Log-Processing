package hellocalculator.events;

public class PerformCalculationEvent extends Event {

  public final DirectObject directObject;

  public PerformCalculationEvent(String operation,
    String[] args, Integer result) {
    super("perform");
    this.directObject = new DirectObject(operation, args, result);
  }

  public static class DirectObject {
    public final Calculation calculation;

    public DirectObject(String operation, String[] args, Integer result) {
      this.calculation = new Calculation(operation, args, result);
    }

    public static class Calculation {
      public final String operation;
      public final String[] args;
      public final Integer result;

      public Calculation(String operation, String[] args, Integer result) {
        this.operation = operation;
        this.args = args;
        this.result = result;
      }
    }
  }
}
