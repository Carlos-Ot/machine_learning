package random_subspace;

import net.sf.javaml.classification.Classifier;

import java.util.Set;

public class RndSubPool {
    private Set<Integer> map;
    private Classifier classifier;

    public RndSubPool(Set<Integer> map, Classifier classifier) {
        this.map = map;
        this.classifier = classifier;
    }

    public RndSubPool(){
    }

    public Set<Integer> getMap() {
        return map;
    }

    public void setMap(Set<Integer> map) {
        this.map = map;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }
}
