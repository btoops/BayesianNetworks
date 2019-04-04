package bn.inference;

import java.util.ArrayList;

import bn.base.Domain;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;

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
//			sample.event.get(X);
//			dist.get(sample.event.get(X));
			res=dist.get(sample.event.get(X))+sample.weight;
			dist.remove(sample.event.get(X));
			dist.put(sample.event.get(X), res);
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
			System.out.println("v: " + v);
			if(e.containsKey(v)) {
				x.put(v, e.get(v));
				//System.out.println("x: " + x);
				weight *= bn.getProbability(v, x);
				//System.out.println("weight: " + weight);
			}else {

				x.put(v, lwi.priorSample2(bn, e).get(v));

			}
			//System.out.println("x2: " + x);
		}
		WeightedAssignment ans = new WeightedAssignment(x, weight);
		//System.out.println("WeightedAssignment: " + ans);
		return ans;

	}
	
	
	
	public Assignment priorSample2(BayesianNetwork bn, Assignment evidence) {
		Assignment e = new bn.base.Assignment();
		ArrayList<RandomVariable> topSort = (ArrayList<RandomVariable>) bn.getVariablesSortedTopologically();
		for(int i = 0; i < topSort.size(); i++) {
			RandomVariable xi = topSort.get(i);
			System.out.println("xi: " + xi);
			if(evidence.containsKey(xi)) {
				//System.out.println("xi equals evidence");
				e.put(xi, evidence.get(xi));
				continue;
			}
			ArrayList<Double> dist= new ArrayList<>();
			for(int j = 0; j < xi.getDomain().size(); j++) {
				System.out.println("" + i + ":" + j);
				Domain domain = (Domain) xi.getDomain();
			    //System.out.println("domain of xi: " + domain);
				//System.out.println("evidence.conatinsKey(xi): " + evidence.containsKey(xi));
				if(evidence.containsKey(xi)) {
					//e.put(xi, evidence.get(xi));
					//System.out.println("e1if put: " + e.put(xi, evidence.get(xi)));
					//System.out.println("e1if: " + e);
				}else {
					e.put(xi, domain.getElements().get(j));
					System.out.println("e2else: " + e);
				}


				dist.add(bn.getProbability(xi, e));
				e.remove(xi);
			}

			double chance = Math.random();
			int index = this.getIndexWithChance(chance, dist);
			Domain domain = (Domain) xi.getDomain();
			e.put(xi, domain.getElements().get(index));
		    System.out.println("dist: " + dist);
		}
		System.out.println("e: " + e + "\n");
		//System.out.println("evidence: " + evidence);
		
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

