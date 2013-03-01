package oving4;

import java.util.Arrays;

public class MathHelper {
	public static MathProblem<?, ?> parseProblem(String op){
		String cleaned = op.replaceAll("[\\[\\]\\(\\)]", "");
		String[] splited = cleaned.split(" ");
		System.out.println(Arrays.toString(splited));
		return null;
	}

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
