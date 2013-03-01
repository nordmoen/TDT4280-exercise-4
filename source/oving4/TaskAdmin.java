package oving4;

import jade.core.Agent;

@SuppressWarnings("serial")
public class TaskAdmin extends Agent {
	
	public static void main(String[] args) {
		System.out.println(MathHelper.parseProblem("+ * 55 20 + 10 2"));
		System.out.println(MathHelper.parseProblem("[+ 20 10]"));
		System.out.println(MathHelper.parseProblem("(+ ((* (2 3)) 2))"));
	}
}
