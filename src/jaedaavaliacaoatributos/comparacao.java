package jaedaavaliacaoatributos;

import java.util.Comparator;

public class comparacao implements Comparator<Individuo> {

    @Override
    public int compare(Individuo e1, Individuo e2) {
        if (e1.getFitnessValue() < e2.getFitnessValue()) {
            return 1;
        } else {
            return -1;
        }
    }
}
