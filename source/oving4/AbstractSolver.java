package oving4;

import jade.core.Agent;

import java.util.ArrayList;

@SuppressWarnings("serial")
public abstract class AbstractSolver extends Agent {

	protected final ArrayList<MathOperator> solverTypes = new ArrayList<MathOperator>();

	public AbstractSolver(MathOperator type) {
		this.solverTypes.add(type);
	}

	public AbstractSolver(ArrayList<MathOperator> types){
		this.solverTypes.addAll(types);
	}

	public AbstractSolver(MathOperator[] types){
		for(MathOperator op : types){
			solverTypes.add(op);
		}
	}

	public ArrayList<MathOperator> getSolverTypes(){
		return this.solverTypes;
	}

	public Number solve(MathProblem<Number, Number> problem){
		if(this.solverTypes.contains(problem.getOp())){
			if(problem.isLeaf()){
				return this.subSolve(problem);
			}else{
				throw new RuntimeException("A Solver can only solve basic " +
						"math expressions not complex expressions. " +
						"Expression was: " + problem);
			}
		}
		throw new RuntimeException("This agent can't solve this type of " +
				"problem. Problem was: " + problem + 
				", this solver can solve: " + solverTypes);
	}

	abstract protected Number subSolve(MathProblem<Number, Number> problem);

}
