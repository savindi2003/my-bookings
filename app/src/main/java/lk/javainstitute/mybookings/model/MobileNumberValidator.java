package lk.javainstitute.mybookings.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileNumberValidator {

    // Regular expression for mobile number validation (simple 10-digit validation)
    private static final String MOBILE_NUMBER_PATTERN = "^[0-9]{10}$";

    // Pattern to compile the regular expression
    private static final Pattern pattern = Pattern.compile(MOBILE_NUMBER_PATTERN);

    public static boolean isValidMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            return false;
        }

        // Check if the mobile number matches the pattern
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.matches();
    }
}
