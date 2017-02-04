package npetzall.queue.helpers;

import npetzall.queue.api.UnknownSizeUnitException;

public class SizeHelper {

    private SizeHelper() {}

    public static int parse(String size) {
        if (!Character.isDigit(size.charAt(size.length()-1))) {
            return multiply(Integer.valueOf(size.substring(0, size.length()-1)), size.charAt(size.length()-1));
        } else {
            return Integer.valueOf(size);
        }
    }

    @SuppressWarnings("fallthrough")
    private static int multiply(int value, char unit) {
        switch(unit) {
            case 'g':
                value *= 1024;
            case 'm':
                value *= 1024;
            case 'k':
                value *= 1024;
            case 'b':
                value *= 1;
                break;
            default:
                throw new UnknownSizeUnitException("'"+unit +"' is not a known unit (g,m,k,b)");

        }
        return value;
    }

}
