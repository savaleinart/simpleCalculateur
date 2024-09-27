public class SimpleValue implements Solvable {

    double value;

    public SimpleValue(double value) {
        this.value = value;
    }

    public double solve() {
        return value;
    }
}
