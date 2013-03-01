package oving4;

import jade.core.Agent;

@SuppressWarnings("serial")
public abstract class AbstractSolver extends Agent {
	
	protected final MathOperator solverType;
	
	public AbstractSolver(MathOperator type) {
		this.solverType = type;
	}
	
	public MathOperator getSolverType(){
		return this.solverType;
	}

}
