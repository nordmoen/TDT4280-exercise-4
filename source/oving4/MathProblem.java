package oving4;

/**
 * Basic syntax tree for a math problem. This will not obey every mathematical
 * law and may very well crash because T and T2 can be different from Number
 * or MathProblem, but if used correctly this should work.
 * @author jorgno
 *
 * @param <T> The type of the first argument of this problem, should either be
 * {@link Number} or {@link MathProblem}
 * @param <T2> The type of the second argument of this problem, should either be
 * {@link Number} or {@link MathProblem}
 */
public class MathProblem <T, T2>{
	
	protected final MathOperator op;
	protected final T argument1;
	protected final T2 argument2;
	
	public MathProblem(MathOperator op, T arg1, T2 arg2){
		this.op = op;
		this.argument1 = arg1;
		this.argument2 = arg2;
	}

	public MathOperator getOp() {
		return op;
	}

	public T getArgument1() {
		return argument1;
	}

	public T2 getArgument2() {
		return argument2;
	}
	
	public boolean isLeaf(){
		return !(this.argument1 instanceof MathProblem<?, ?>) || !(this.argument2 instanceof MathProblem<?, ?>);
	}
	
	public String toString(){
		return "(" + this.op + " (" + this.argument1 + " " + this.argument2 +"))";
	}

}
