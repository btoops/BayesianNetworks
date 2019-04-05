package bn.inference;

import java.util.ArrayList;

import bn.base.Domain;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.core.Value;
import bn.util.ArraySet;

public class LikelihoodWeightingInferencer {

	public Distribution LikelihoodWeighting(RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		Distribution dist = new bn.base.Distribution(X);

		for(int j = 0; j < X.getDomain().size(); j++) {
			Domain domain = (Domain) X.getDomain();
			dist.put(domain.getElements().get(j), 0.0);
		}
		//System.out.println(dist);


		double res=0;
		for(int i = 0; i<N; i++) {
			
			WeightedAssignment sample = this.weightedSample(bn, e);
			double weight = sample.weight;
			Assignment event = sample.event;
			dist.put(event.get(X), dist.get(event.get(X))+weight);
//			//			sample.event.get(X);
//			//			dist.get(sample.event.get(X));
//			res=dist.get(sample.event.get(X))+sample.weight;
//			dist.remove(sample.event.get(X));
//			dist.put(sample.event.get(X), res);
			//System.out.println(sample);
			//System.out.println(dist);
		}

		dist.normalize();
		return dist;
	}



	public class WeightedAssignment{

		double weight;
		Assignment event;

		public WeightedAssignment(Assignment a, double w) {
			weight = w;
			event = a;
		}

		@Override
		public String toString() {
			return "" + weight + " " + event;
		}

	}



	public WeightedAssignment weightedSample(BayesianNetwork bn, Assignment e) {
		double weight = 1;
		LikelihoodWeightingInferencer lwi = new LikelihoodWeightingInferencer();
		Assignment x = e.copy();
		for(RandomVariable v : bn.getVariablesSortedTopologically()) {
			
			if(e.containsKey(v)) { //if xi is in e
				x.put(v, e.get(v));
				//System.out.println("x: " + x);
				weight = weight * bn.getProbability(bn.getProbability(v, x), x);
				//System.out.println("weight: " + weight);
			}else {
				x.put(v, lwi.randomSample(bn, e).get(v));

			}
			//System.out.println("x2: " + x);
		}
		WeightedAssignment ans = new WeightedAssignment(x, weight);
		//System.out.println("WeightedAssignment: " + ans);
		return ans;

	}

	public Assignment randomSample(BayesianNetwork bn, Assignment evidence) {

		Assignment e = evidence.copy();
		//System.out.println("eSTART: " + e);
		ArrayList<RandomVariable> topSort = (ArrayList<RandomVariable>) bn.getVariablesSortedTopologically();
		for(int i = 0; i < topSort.size(); i++) {
			RandomVariable xi = topSort.get(i);
			ArrayList<Double> dist= new ArrayList<>();
			Domain domain = (Domain) xi.getDomain();
			for(int j = 0; j < xi.getDomain().size(); j++) {

				e.put(xi, domain.getElements().get(j));
				//System.out.println(e);
				dist.add(bn.getProbability(xi, e));
				//e.remove(xi);
			}
			double chance = Math.random();
			int index = this.getIndexWithChance(chance, dist);
			//System.out.println("distribution: " + dist);
			e.put(xi, domain.getElements().get(index));

			for(int j = 0; j < evidence.size(); j++) {
				if(evidence.containsKey(xi)) {
					e.put(xi, evidence.get(xi));
				}
			}
			//System.out.println(dist);
		}

		//System.out.println("eFINAL: " + e);

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


}

