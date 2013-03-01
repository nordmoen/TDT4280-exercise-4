package oving4;

@SuppressWarnings("serial")
public class MultiplySolver extends AbstractSolver {

	public MultiplySolver() {
		super(MathOperator.MULTIPLY);
	}

	@Override
	protected Number subSolve(MathProblem<Number, Number> problem) {
		return problem.getArgument1().doubleValue() * problem.getArgument2().doubleValue();
	}

}
