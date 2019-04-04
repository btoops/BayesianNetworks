package bn.inference;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
//import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.base.Distribution;
import bn.base.Value;
public class EnumerationInferencer implements Inferencer{

	@Override
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
		return enumerateAsk(X, e, network);
	}
	

	public Distribution enumerateAsk(RandomVariable X, Assignment e, BayesianNetwork bn) {
	//	System.out.println("getting in to algorithm");
//		System.out.println(X);
//		System.out.println(e);
//		System.out.println(bn);
		Distribution dist = new bn.base.Distribution(X);
		for(bn.core.Value val: X.getDomain()) {
			Assignment ex = e.copy();
			ex.put(X, val);
			dist.set(val, this.enumerateAll(bn.getVariablesSortedTopologically(), ex, bn));
			ex.remove(X);
		}
		//this.enumerateAll(bn.getVariablesSortedTopologically(), e, bn);
		dist.normalize();
		return dist;
	
	}

	
	protected double enumerateAll(java.util.List<RandomVariable> vars, Assignment e, BayesianNetwork bn) {
		if(vars.size()==0) {
			return 1.0;
		}
		RandomVariable Y = vars.get(0);
		if(e.containsKey(Y)) {
			//System.out.println(bn.getProbability(Y, e));
			return bn.getProbability(Y, e)	* enumerateAll(vars.subList(1, vars.size()), e, bn);
		}else {
			double sum = 0;
			for(bn.core.Value v: Y.getDomain()) {
				
				Assignment ey = e.copy();
				ey.put(Y, v);
				sum+=bn.getProbability(Y, ey)*enumerateAll(vars.subList(1, vars.size()), ey, bn);
				//System.out.println(sum);
				ey.remove(Y);
			}
			return sum;
		}
		
	}
}
