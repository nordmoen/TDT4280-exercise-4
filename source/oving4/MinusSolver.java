package oving4;

@SuppressWarnings("serial")
public class MinusSolver extends AbstractSolver {

	public MinusSolver() {
		super(MathOperator.MINUS);
	}

	@Override
	protected Number subSolve(MathProblem<Number, Number> problem) {
		return problem.getArgument1().doubleValue() - problem.getArgument2().doubleValue();
	}

}
