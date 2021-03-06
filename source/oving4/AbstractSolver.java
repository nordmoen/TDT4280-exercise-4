package oving4;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractSolver extends Agent {
	
	private final class Proposal{
		public final AID proposer;
		public final MathProblem<Number, Number> message;
		public final long estimate;
		
		public Proposal(AID a, MathProblem<Number, Number> m, long e){
			this.proposer = a;
			this.message = m;
			this.estimate = e;
		}
	}

	protected final List<MathOperator> solverTypes = new ArrayList<MathOperator>();
	protected final List<Proposal> props = new ArrayList<AbstractSolver.Proposal>();

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
	
	/**
	 * Anything which needs to be setup in a constructor can be added in this
	 * method to ensure that all constructors does the same thing.
	 */
	protected void setup(){
		super.setup();
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
					case ACLMessage.REJECT_PROPOSAL:
						handleRejection(msg);
						break;
					case ACLMessage.ACCEPT_PROPOSAL:
						handleAccept(msg);
						break;
					default:
						System.err.println("Got strange message: " + msg);
						break;
					}
				}
			}

		});
	}
	
	private void handleAccept(ACLMessage msg){
		System.out.println("Got accept message");
		Proposal p = findFirstProposal(msg.getSender());
		this.props.remove(p);
		try {
			Thread.sleep(p.estimate);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		reply.setContent(this.solve(p.message) + "");
		this.send(reply);
	}
	
	private void handleRejection(ACLMessage msg) {
		System.out.println("Got reject message");
		Proposal p = findFirstProposal(msg.getSender());
		if(p != null){
			props.remove(p);
		}
	}
	
	private Proposal findFirstProposal(AID sender){
		for(Proposal p : props){
			if(p.proposer.equals(sender)){
				return p;
			}
		}
		return null;
	}
	
	private void handleCFP(ACLMessage msg){
		System.out.println("Got CFP message");
		MathProblem<Number, Number> prob = MathHelper.parseProblem(msg.getContent());
		long time = this.estimateTime(prob);
		Proposal p = new Proposal(msg.getSender(), prob, time);
		this.props.add(p);
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.PROPOSE);
		reply.setContent(time + "");
		this.send(reply);
	}
	
	protected void registerSolver(){
		DFAgentDescription desc = new DFAgentDescription();
		desc.setName(this.getAID());
		for(MathOperator op : solverTypes){
			ServiceDescription d = new ServiceDescription();
			d.setName(this.getLocalName());
			d.setType(op.toString());
			desc.addServices(d);
		}
		try {
			DFService.register(this, desc);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<MathOperator> getSolverTypes(){
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
	
	protected long estimateTime(MathProblem<Number, Number> problem){
        return (long) (props.size()*10*Math.random());
    }

}
