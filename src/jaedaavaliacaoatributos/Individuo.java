package jaedaavaliacaoatributos;

public class Individuo {

    //Propriedades da classe    
    private int[] cromossomos;
    private double fitnessValor;

    //Método inicializador
    public Individuo(int tamanho) {
        //Definir o tamanho do vetor
        cromossomos = new int[tamanho];

    }

    //Método inicializador
    public Individuo(int[] cromossomos, double fitnessValor) {
        this.cromossomos = cromossomos;
        this.fitnessValor = fitnessValor;
    }

    //Métodos Get´s e Set´s
    public double getFitnessValue() {
        return fitnessValor;
    }

    public void setFitnessValue(double fitnessValue) {
        this.fitnessValor = fitnessValue;
    }

    public int getCromossomo(int index) {
        return cromossomos[index];
    }

    public int[] getCromossomo() {
        return cromossomos;
    }

    public void setCromossomo(int index, int cromossomo) {
        this.cromossomos[index] = cromossomo;
    }

    public void setCromossomo(int[] cromossomo) {
        this.cromossomos = cromossomo;
    }

    //Gerar os cromossomos randômicamente (0´s ou 1´s)
    public void CromossomosRandomicos(double probabilidade) {
        //Percorrer o tamanho do Vetor
        for (int iPos = 0; iPos < this.cromossomos.length; iPos++) {
            //Setar Randomicamente o % de probabilidade
            this.setCromossomo(iPos, EdaSa._MT.nextBoolean(probabilidade) ? 1 : 0);

        }

    }

    public void avaliacao() {
        //Declaração Variáveis e Objetos
        int qtdOcorrencias = 0;

        for (int i = 0; i < this.cromossomos.length; ++i) {
            //Somar a Quantidade
            qtdOcorrencias += this.getCromossomo(i);

        }

        //Setar o Valor de Fitness
        this.setFitnessValue(qtdOcorrencias);

    }

}
