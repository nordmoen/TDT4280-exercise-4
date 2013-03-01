package oving4;

import jade.core.Agent;

@SuppressWarnings("serial")
public class TaskAdmin extends Agent {

	public static MathOperator parseOperator(String op){
		char convertedOp = 'N';
		String trimedOp = op.trim();
		if(trimedOp.length() == 1){
			convertedOp = trimedOp.charAt(0);
		}
		
		switch (convertedOp) {
		case '+':
			return MathOperator.PLUS;
		case '-':
			return MathOperator.MINUS;
		case '*':
			return MathOperator.MULTIPLY;
		case '/':
			return MathOperator.DIVIDE;
		default:
			throw new RuntimeException("Could not parse '" + op + 
					"' into a proper MathOperator");
		}
	}
}
