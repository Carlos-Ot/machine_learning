package random_subspace;

import net.sf.javaml.core.Dataset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class RandomSubspace {


    private Dataset selectRandomSubspace(Dataset data, int k) {

        //Usar o Set para gerar o conjunto randomico de características

        Set<Integer> attributes = new HashSet<>();
        attributes.add(1);

        data.instance(1).removeAttributes(attributes);

        return null;
    }

    private Dataset expandAttributes(Dataset data) {
        return null;
    }
}
