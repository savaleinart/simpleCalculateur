public class ComplexExpression implements Solvable {
    String operand;
    Solvable leftMember;
    Solvable rightMember;

    public ComplexExpression(String operand, Solvable leftMember, Solvable rightMember) {
        this.operand = operand;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
    }

    public double solve() {
        return switch (operand) {
            case "+" -> leftMember.solve() + rightMember.solve();
            case "*" -> leftMember.solve() * rightMember.solve();
            case "-" -> leftMember.solve() - rightMember.solve();
            case "/" -> leftMember.solve() / rightMember.solve();
            default -> throw new RuntimeException("Invalid operand");
        };
    }
}
