package oving4;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

@SuppressWarnings("serial")
public abstract class AbstractSolver extends Agent {

	protected final ArrayList<MathOperator> solverTypes = new ArrayList<MathOperator>();

	public AbstractSolver(MathOperator type) {
		this.solverTypes.add(type);
		this.setupAgent();
	}

	public AbstractSolver(ArrayList<MathOperator> types){
		this.solverTypes.addAll(types);
		this.setupAgent();
	}

	public AbstractSolver(MathOperator[] types){
		for(MathOperator op : types){
			solverTypes.add(op);
		}
		this.setupAgent();
	}
	
	/**
	 * Anything which needs to be setup in a constructor can be added in this
	 * method to ensure that all constructors does the same thing.
	 */
	private void setupAgent(){
		this.registerSolver();
		this.addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg = receive();
				if(msg != null){
					switch (msg.getPerformative()) {
					case ACLMessage.CFP:
						handleCFP(msg);
						break;

					default:
						break;
					}
				}
			}
		});
	}
	
	private void handleCFP(ACLMessage msg){
		MathProblem<Number, Number> prob = MathHelper.parseProblem(msg.getContent());
		double time = this.estimateTime(prob);
	}
	
	protected void registerSolver(){
		DFAgentDescription desc = new DFAgentDescription();
		for(MathOperator op : solverTypes){
			desc.addLanguages(op.toString()); 
			//I'm a bit unsure if language is
			//the correct variable
		}
		try {
			DFService.register(this, desc);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	abstract protected double estimateTime(MathProblem<Number, Number> problem);

}
