package app.gozenko.validation;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidateData {
    public static final String validName = "^[a-zA-Z]{2,30}$";
    public static final BigDecimal validAmount = BigDecimal.valueOf(20000);
    public static final Integer validTerm = 6;
    public static final String validEmail = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$";
    public static final String validBirthday = "^\\d{4}-\\d{2}-\\d{2}$";
    public static final Integer legalAge = 18;
    public static final String validPassportSeries = "^\\d{4}$";
    public static final String validPassportNumber = "^\\d{6}$";


    public static boolean checkLegalAge(LocalDate birthday){
        try{
            LocalDate today = LocalDate.now();

            LocalDate yearsAgo = today.minusYears(legalAge);
            return !birthday.isAfter(yearsAgo);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e+" Некорректная дата");
        }
    }
}
