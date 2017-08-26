package random_subspace;

import net.sf.javaml.core.Dataset;

import java.util.Set;

public class RandomDataset {
    private Set<Integer> attributesMap;
    private Dataset dataset;

    public RandomDataset(Set<Integer> map, Dataset dataset) {
        this.attributesMap = map;
        this.dataset = dataset;
    }

    public Set<Integer> getAttributesMap() {
        return attributesMap;
    }

    public void setAttributesMap(Set<Integer> attributesMap) {
        this.attributesMap = attributesMap;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }
}
