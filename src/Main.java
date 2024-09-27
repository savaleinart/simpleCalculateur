import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static Pattern multiplicationPattern = Pattern.compile("(.+)([*/])(.+)");
    static Pattern additionPattern = Pattern.compile("(.+)([+-])(.+)");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(stringToExpression(scanner.nextLine()).solve());
    }


    public static Solvable stringToExpression(String str) {

        Matcher matcher = additionPattern.matcher(str);
        if (matcher.find()) {
            return new ComplexExpression(matcher.group(2), stringToExpression(matcher.group(1)), stringToExpression(matcher.group(3)));
        } else {
            matcher = multiplicationPattern.matcher(str);
            if (matcher.find()) {
                return new ComplexExpression(matcher.group(2), stringToExpression(matcher.group(1)), stringToExpression(matcher.group(3)));
            } else {
                return new SimpleValue(Double.parseDouble(str));
            }
        }

    }


}