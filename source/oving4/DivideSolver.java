package oving4;

@SuppressWarnings("serial")
public class DivideSolver extends AbstractSolver {

	public DivideSolver() {
		super(MathOperator.DIVIDE);
	}

	@Override
	protected Number subSolve(MathProblem<Number, Number> problem) {
		return problem.getArgument1().doubleValue() / problem.getArgument2().doubleValue();
	}

}
