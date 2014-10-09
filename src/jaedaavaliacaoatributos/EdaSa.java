package jaedaavaliacaoatributos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import weka.core.Instances;

public final class EdaSa {

    //<editor-fold defaultstate="collapsed" desc="1° Configuração das Bases de Dados e atributos iniciais">    	    
    //Arquivo com Atributos Numéricos
    private static final String _arquivo = "C:\\ArffTeste\\ionosphere.arff";

    //Arquivo com Atributos Nominais
    //private static final String _arquivo = "C:\\ArffTeste\\vote.arff";
    private static final int _QUANTIDADE = 1000;
    private static final int _GERACOES = 100;
    public static final prjageda.MersenneTwister _MT = new prjageda.MersenneTwister();

    static Individuo[] melhorPopulacao;
    private Individuo[] m_populacao;
    static double[] probabilidades = new double[_QUANTIDADE];
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="2° Definição dos Métodos de Get´s e Set´s">
    public Individuo[] getPopulation() {
        return this.m_populacao;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="3° Definição do Método Inicializador da Classe e Demais Métodos">
    public EdaSa() {

    }

    public EdaSa(int nroCromossomos) {
        //1° Passo - Gerar a população Inicial - 50% de probabilidade para 0 ou 1
        //2° Passo - Calcular o Fitness
        //3° Passo - Pegar os 50% Melhores indivíduos
        //4° Passo - Calcular os % de cada posição do Cromossomo
        GerarPopulacaoInicial(nroCromossomos);

    }

    public enum Algoritmos {

        //Definição dos Algoritmos
        NaiveBayes, KNN;

    }

    public void GerarPopulacaoInicial(int qtdCromossomos) {
        try {
            //Declaração Variáveis e Objetos
            m_populacao = new Individuo[_QUANTIDADE];

            //Inicializar a população (pelo tamanho definido)
            for (int i = 0; i < _QUANTIDADE; i++) {
                //Inicialização do Objeto
                m_populacao[i] = new Individuo(qtdCromossomos);

                //Geração da população com 50% de probabilidade
                m_populacao[i].CromossomosRandomicos(0.5);

            }

            //Cálculo do Fitness(Quantidade de 1´s encontrados X Cromossomo)
            CalculoFitness();

            //Pegar os 50% melhores indivíduos da população
            melhorPopulacao = findBestPopulation();

            //Calcular o Vetor de Probabilidades
            double percentual = 0;

            for (int j = 0; j < _QUANTIDADE; j++) {
                //Inicializar a variável
                percentual = 0;

                //Percorre a quantidade de indivíduos existentes na posição "j" do cromossomos
                for (int i = 0; i < melhorPopulacao.length; i++) {
                    //Totalizar o Indivíduo
                    percentual += melhorPopulacao[i].getCromossomo(j);

                }

                //Resultado da Probabilidade do cromossomo da posição "j"
                probabilidades[j] = percentual == 0 ? 0 : percentual / melhorPopulacao.length;               

            }

        } catch (Exception e) {
            throw e;

        }

    }

    public void CalculoFitness() {
        //Declaração Variáveis e Objetos
        for (int i = 0; i < _QUANTIDADE; i++) {
            m_populacao[i].avaliacao();

        }

    }

    public Individuo[] findBestPopulation() {
        //Definir a melhor população
        Individuo[] bestPopulation = new Individuo[_QUANTIDADE / 2];
        List<Individuo> dados = new ArrayList<>();

        //Inicializar o vetor
        for (int indice = 0; indice < _QUANTIDADE; indice++) {
            int[] cromossomos = new int[_QUANTIDADE];

            //Setar os valores do cromossomo  e o Fitness
            for (int j = 0; j < _QUANTIDADE; j++) {
                cromossomos[j] = m_populacao[indice].getCromossomo(j);
            }

            dados.add(new Individuo(cromossomos, m_populacao[indice].getFitnessValue()));
        }

        //Ordenar decrescente
        Collections.sort(dados, new comparacao());

        //Inicializar o vetor
        for (int indice = 0; indice < _QUANTIDADE / 2; indice++) {
            //Alocar memória p/ o Objeto
            bestPopulation[indice] = new Individuo(_QUANTIDADE);

            //Atribuições das propriedades
            bestPopulation[indice].setCromossomo(dados.get(indice).getCromossomo());
            bestPopulation[indice].setFitnessValue(dados.get(indice).getFitnessValue());

        }
        //Definir o retorno
        return bestPopulation;

    }
    // </editor-fold>

    public static void main(String[] args) throws Exception {
        //Leitura do Arquivo Arrf
        Instances dados = new Processamento(_arquivo).lerArquivoDados();

        //Declaração Variáveis e 
        EdaSa edasa = new EdaSa(dados.numAttributes() - 1);

    }

}
