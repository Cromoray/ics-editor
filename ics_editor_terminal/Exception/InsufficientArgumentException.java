package Exception;

public class InsufficientArgumentException extends Exception {
    public InsufficientArgumentException(String errorMessage) {
        super(errorMessage);
    }

    public static String getErrorMessage() {
        return "Usage: ICS_Creator <inputFile.csv> <outputFile.ics>";
    }
}
