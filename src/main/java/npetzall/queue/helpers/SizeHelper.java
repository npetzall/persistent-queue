package npetzall.queue.helpers;

public class SizeHelper {

    private SizeHelper() {}

    public static int parse(String size) {
        int rv = 0;
        if (!Character.isDigit(size.charAt(size.length()-1))) {
            rv += Integer.valueOf(size.substring(0, size.length()-1));
        } else {
            rv += Integer.valueOf(size);
        }
        switch(size.substring(size.length()-1)) {
            case "g":
                rv *= 1024;
            case "m":
                rv *= 1024;
            case "k":
                rv *= 1024;
            case "b":
                rv *= 1;
                break;
            default:
                rv *= 1;

        }
        return rv;
    }

}
