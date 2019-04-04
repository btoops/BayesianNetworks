package bn.inference;

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
		for(int i = 0; i<N; i++) {
			double res=0;
			WeightedAssignment sample = this.weightedSample(bn, e);
//			sample.event.get(X);
//			dist.get(sample.event.get(X));
			res=dist.get(sample.event.get(X))+sample.weight;
			dist.remove(sample.event.get(X));
			dist.put(sample.event.get(X), res);
			//System.out.println(sample);
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
		RejectionSamplingInferencer rsi = new RejectionSamplingInferencer();
		Assignment x = e.copy();
		for(RandomVariable v : bn.getVariablesSortedTopologically()) {
			if(e.containsKey(v)) {
				x.put(v, e.get(v));
				weight *= bn.getProbability(v, x);
			}else {
				x.put(v, rsi.priorSample(bn).get(v));
			}
		}
		WeightedAssignment ans = new WeightedAssignment(x, weight);
		return ans;



	}
}

