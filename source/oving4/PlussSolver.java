package oving4;

@SuppressWarnings("serial")
public class PlussSolver extends AbstractSolver {

	public PlussSolver() {
		super(MathOperator.PLUS);
	}

	@Override
	protected Number subSolve(MathProblem<Number, Number> problem) {
		return problem.argument1.doubleValue() + problem.argument2.doubleValue();
	}

}
