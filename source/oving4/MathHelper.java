package oving4;

import java.util.Arrays;


public class MathHelper {
	public static MathProblem<?, ?> parseProblem(String op){
		String cleaned = op.replaceAll("[\\[\\]\\(\\)]", "");
		String[] splited = cleaned.split(" ");
		return (MathProblem<?, ?>) subParse(splited).prob;
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
	
	private static ParseTuple subParse(String[] parseString){
		MathOperator op = null;
		ParseTuple result = new ParseTuple();
		if(parseString[0].matches("[\\+\\-\\*\\/]")){
			op = parseOperator(parseString[0]);
			ParseTuple t1 = subParse(Arrays.copyOfRange(parseString, 1, parseString.length));
			ParseTuple t2 = subParse(t1.rest);
			result.prob = new MathProblem(op, t1.prob, t2.prob);
			result.rest = t2.rest;
		}else{
			result.prob = Double.parseDouble(parseString[0]);
			result.rest = Arrays.copyOfRange(parseString, 1, parseString.length);
		}
		
		return result;
	}
	
	public static class ParseTuple{
		public Object prob;
		public String[] rest;
	}
}
