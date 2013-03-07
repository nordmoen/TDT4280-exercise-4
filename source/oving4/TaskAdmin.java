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
public class TaskAdmin extends Agent {

	private class Problem{
		public final MathProblem<?, ?> prob;
		public final ACLMessage reply;

		public Problem(MathProblem<?, ?> p, ACLMessage m){
			this.prob = p;
			this.reply = m;
		}
	}

	private final List<Problem> problems = new ArrayList<Problem>();
	@Override
	protected void setup() {
		super.setup();

		this.addBehaviour(new CyclicBehaviour(){

			@Override
			public void action() {
				ACLMessage msg = receive();
				if(msg != null){
					switch (msg.getPerformative()) {
					case ACLMessage.QUERY_REF:
						MathProblem<?, ?> problem = MathHelper.parseProblem(msg.getContent());
						Problem prob = new Problem(problem, msg.createReply());
						problems.add(prob);
						solveProblem();
						break;
					default:
						System.out.println("Got unrecoqnized performative: " + 
								msg.getPerformative() + ", " + msg.getContent());
						break;
					}
				}
			}
		});
	}

	/**
	 * Solve the last problem added to the queue of problems
	 */
	private void solveProblem(){
		Problem prob;
		synchronized (problems) {
			//Syncronized if the behavior above is concurrent with the rest
			//of the class, this means we can work concurrent without problems
			//of concurreny issues
			if(problems.isEmpty()){
				return;
			}else{
				prob = problems.remove(problems.size() - 1);
			}
		}
		if(prob != null){
			Number ans = this.solve(prob.prob);
			System.out.println("Problem: " + prob.prob + ", solved: " + ans);
			prob.reply.setContent(ans + "");
			this.send(prob.reply);
		}
	}

	private Number solve(MathProblem<?, ?> p){
		if(p.isLeaf()){
			MathProblem<Number, Number> p1 = new MathProblem<Number, Number>(p.getOp(),
					(Number) p.getArgument1(), (Number) p.getArgument2());
			Number result = null;
			do{
				result = this.auction(p1);
			}while(result == null);
			return result;
		}else{
			Number one;
			Number two;
			if(p.argument1IsNumber()){
				one = (Number) p.getArgument1();
			}else{
				one = this.solve((MathProblem<?, ?>) p.getArgument1());
			}
			if(p.argument2IsNumber()){
				two = (Number) p.getArgument2();
			}else{
				two = this.solve((MathProblem<?, ?>) p.getArgument2());
			}
			MathProblem<Number, Number> p1 = new MathProblem<Number, Number>(p.getOp(),
					one, two);
			Number result = null;
			do{
				result = this.auction(p1);
			}while(result == null);
			return result;
		}
	}

	private Number auction(MathProblem<Number, Number> problem){
		DFAgentDescription desc = new DFAgentDescription();
		ServiceDescription s = new ServiceDescription();
		s.setType(problem.getOp().toString());
		desc.addServices(s);
		DFAgentDescription[] agents = null;
		try {
			agents = DFService.search(this, desc);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		if(agents != null){
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			for(DFAgentDescription d : agents){
				msg.addReceiver(d.getName());
			}
			msg.setContent(problem.toString());
			this.send(msg);
			long minTime = Long.MAX_VALUE;
			AID min = null;
			for(int i = 0; i < agents.length; i++){
				ACLMessage reply = this.blockingReceive();
				if(reply != null){
					if(reply.getPerformative() == ACLMessage.PROPOSE){
						long time = Long.parseLong(reply.getContent());
						if(time < minTime){
							minTime = time;
							min = reply.getSender();
						}
					}
				}
			}
			ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			for(DFAgentDescription d : agents){
				if(!d.getName().equals(min))
					reject.addReceiver(d.getName());
			}
			this.send(reject);
			if(min != null){
				ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				accept.addReceiver(min);
				this.send(accept);
				ACLMessage answer = this.blockingReceive();
				if(answer != null){
					if(answer.getPerformative() == ACLMessage.INFORM && answer.getSender().equals(min)){
						return Double.parseDouble(answer.getContent());
					}
				}
			}
		}else{
			System.err.println("Could not find any agents which could solve: " + 
		problem + ", make sure there are some agents of the needed type!");
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(MathHelper.parseProblem("+ * 55 20 + 10 2").isLeaf());
		System.out.println(MathHelper.parseProblem("+ 20 30").isLeaf());
		System.out.println(MathHelper.parseProblem("+ * 50 20 10").isLeaf());
		System.out.println(MathHelper.parseProblem("[+ 20 10]"));
		System.out.println(MathHelper.parseProblem("(+ ((* (2 3)) 2))"));
	}
}
