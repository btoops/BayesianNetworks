package bn.inference;

import java.util.List;
import java.util.Set;

import bn.base.Domain;
import bn.base.StringValue;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;

public class GibbsInferencer implements Inferencer{
	
	public Assignment state;
	
	public Distribution gibbsAsk(RandomVariable X, Assignment e, BayesianNetwork bn, int N) {
		
		Distribution dist = new bn.base.Distribution(X);

		for(int j = 0; j < X.getDomain().size(); j++) {
			Domain domain = (Domain) X.getDomain();
			dist.put(domain.getElements().get(j), 0.0);
		}
		
		
		List<RandomVariable> ListofVars = bn.getVariablesSortedTopologically();
		this.state = this.InitializeState(ListofVars, e);
		List<RandomVariable> nonEvidence = this.getNonEvidence(bn.getVariablesSortedTopologically(), e);
		for(int i=0; i<N; i++) {
			for(int j=0;j<nonEvidence.size();j++) {
				//int j = (int) (Math.random()*nonEvidence.size());
				this.sampleNonEvidence(nonEvidence.get(j),bn,state);
				this.reorderState(state, bn);
			}
			//System.out.println(this.state);
			double pre = dist.get(state.get(X));
			dist.put(state.get(X), pre+1);
			//System.out.println(dist);
		}
		dist.normalize();
		return dist;
	}

	private void reorderState(Assignment state, BayesianNetwork bn) {
		// TODO Auto-generated method stub
		List<RandomVariable> ListofVars = bn.getVariablesSortedTopologically();
		Assignment a = new bn.base.Assignment();
		for(int i=0; i<ListofVars.size(); i++) {
			a.put(ListofVars.get(i), state.get(ListofVars.get(i)));
		}
		this.state=a;
	}

	public void sampleNonEvidence(RandomVariable randomVariable, BayesianNetwork bn, Assignment state) {
		// TODO Auto-generated method stub
		Assignment e = this.getMKBlankAssignment(randomVariable, state, bn);
//		System.out.println(state);
		
		e.remove(randomVariable);
		System.out.println(randomVariable);
		System.out.println(e);
		EnumerationInferencer exact=new EnumerationInferencer();
		//double prob = bn.getProbability(randomVariable,this.state);
		Distribution dist =exact.query(randomVariable, e, bn);
	//	System.out.println(dist);
		double prob = dist.get(state.get(randomVariable));
		
//		System.out.println(randomVariable);
		System.out.println(prob);
//		System.out.println(this.state.get(randomVariable));
		double chance = Math.random();
		
		if(chance>=prob) {
			state.put(randomVariable, state.get(randomVariable));
		}else {
			state.put(randomVariable, this.returnFlipedVal(state.get(randomVariable)));
		}
	}
	
	public Value returnFlipedVal(Value v) {
		if(v.toString().equals("true")) {
			return new StringValue("false");
		}else {
			return new StringValue("true");
		}
	}
	private Assignment getMKBlankAssignment(RandomVariable randomVariable, Assignment state, BayesianNetwork bn) {
		// TODO Auto-generated method stub
		Assignment evidence = this.getAssignmentofVariables(state, this.getAllMKVariable(randomVariable, bn));
		return evidence;
	}

	public Assignment getAssignmentofVariables(Assignment state, List<RandomVariable> allMKVariable) {
		// TODO Auto-generated method stub
		Assignment e = new bn.base.Assignment();
		for(int i=0;i<allMKVariable.size(); i++) {
			e.put(allMKVariable.get(i), state.get(allMKVariable.get(i)));
		}
		return e;
	}

	public List<RandomVariable> getAllMKVariable(RandomVariable randomVariable, BayesianNetwork bn) {
		List<RandomVariable> ListofVars = bn.getVariablesSortedTopologically();
		Set<RandomVariable> children = bn.getChildren(randomVariable);
		Set<RandomVariable> parents = bn.getParents(randomVariable);
		for(RandomVariable r: children) {
			parents.addAll(bn.getParents(r));
		}
		parents.addAll(children);
		
		for(int i=0; i<ListofVars.size(); i++) {
			if(parents.contains(ListofVars.get(i))) {
				continue;
			}else {
				ListofVars.remove(i);
			}
		}
		parents.remove(randomVariable);
		return ListofVars;
		// TODO Auto-generated method stub
	}

	@Override
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public Assignment InitializeState(List<RandomVariable> ListofVars, Assignment evidence) {
		Assignment state = new bn.base.Assignment();
		for(int i=0; i<ListofVars.size(); i++) {
			if(evidence.containsKey(ListofVars.get(i))) {
				state.put(ListofVars.get(i), evidence.get(ListofVars.get(i)));
			}else {
				state.put(ListofVars.get(i), this.randomInit());
			}
		}
		return state;
	}
	
	public List<RandomVariable> getNonEvidence(List<RandomVariable> ListofVars, Assignment e){
		for(int i=0; i<ListofVars.size(); i++) {
			if(e.containsKey(ListofVars.get(i))) {
				ListofVars.remove(i);
			}
		}
		return ListofVars;
	}
	
	public Value randomInit() {
		double chance = Math.random();
		if(chance<=0.5) {
			return new StringValue("true");
		}else {
			return new StringValue("false");
		}
	}
}
