package oving4;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
					if(msg.getPerformative() == ACLMessage.QUERY_REF){
						MathProblem<?, ?> problem = MathHelper.parseProblem(msg.getContent());
						Problem prob = new Problem(problem, msg.createReply());
						problems.add(prob);
						solveProblem();
					}else{
						System.out.println("Got unrecoqnized performative: " + 
					msg.getPerformative() + ", " + msg.getContent());
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
			prob.reply.setContent(ans + "");
			this.send(prob.reply);
		}
	}
	
	private Number solve(MathProblem<?, ?> p){
		if(p.isLeaf()){
			MathProblem<Number, Number> p1 = new MathProblem<Number, Number>(p.getOp(),
					(Number) p.getArgument1(), (Number) p.getArgument2());
			return auction(p1);
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
			return this.auction(p1);
		}
	}
	
	private Number auction(MathProblem<Number, Number> problem){
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
