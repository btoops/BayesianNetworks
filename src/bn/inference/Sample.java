package bn.inference;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.RandomVariable;
import bn.core.Value;
import bn.util.ArraySet;

public class Sample {
    public static Object randomSample(BayesianNetwork bn, RandomVariable V, Assignment e){
        double rand = Math.random();
        double threshold = 0.0;
        for(int i=0; i<V.getDomain().size(); i++) {
            Assignment cpy = e.copy();
            cpy.put(V, ((ArraySet<Value>) V.getDomain()).getElements().get(i));
            threshold+=bn.getProbability(V, cpy);
            if (rand <= threshold) {
                return ((ArraySet<Value>) V.getDomain()).getElements().get(i);
            }
        }
        return null;
    }
}