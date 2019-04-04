package bn.inference;

import java.util.ArrayList;

import bn.core.Distribution;
import bn.base.Domain;
import bn.base.Value;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inferencer;
import bn.core.RandomVariable;

public class RejectionSamplingInferencer implements Inferencer{
	
	public Distribution rejectionSampling(RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		Assignment x;
		Distribution counter = new bn.base.Distribution(X);
		
		for(bn.core.Value v : X.getDomain()) {
			counter.set(v, 0);
		}
		for(int i = 0; i < N; i ++) {
			x = priorSample(bn);
			if(x.containsAll(e)) {
//				System.out.println("putting");
				counter.put(x.get(X), counter.get(x.get(X)) + 1.0);
			}
		}
		System.out.println("counter: " + counter);
		counter.normalize();

		return counter;
	}
	

	
	
	
	public Assignment priorSample(BayesianNetwork bn) {

		Assignment e = new bn.base.Assignment();
		ArrayList<RandomVariable> topSort = (ArrayList<RandomVariable>) bn.getVariablesSortedTopologically();
		for(int i = 0; i < topSort.size(); i++) {
			RandomVariable xi = topSort.get(i);
			ArrayList<Double> dist= new ArrayList();
			for(int j = 0; j < xi.getDomain().size(); j++) {
				Domain domain = (Domain) xi.getDomain();
				e.put(xi, domain.getElements().get(j));
				//System.out.println(e);
				dist.add(bn.getProbability(xi, e));
				e.remove(xi);
			}
			double chance = Math.random();
			int index = this.getIndexWithChance(chance, dist);
			Domain domain = (Domain) xi.getDomain();
			e.put(xi, domain.getElements().get(index));
			//System.out.println(dist);
		}
		//System.out.println(e);
		
		return e;
	}
	
	public int getIndexWithChance(double chance, ArrayList<Double> dist) {
		for(int i=0; i<dist.size(); i++) {
			chance-=dist.get(i);
			if(chance<=0) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public Distribution query(RandomVariable X, bn.core.Assignment e, bn.core.BayesianNetwork network) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
