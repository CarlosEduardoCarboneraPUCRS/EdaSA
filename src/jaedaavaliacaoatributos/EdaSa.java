package jaedaavaliacaoatributos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public final class EdaSa {

    //<editor-fold defaultstate="collapsed" desc="1° Configuração das Bases de Dados e atributos iniciais">    	    
    //Arquivo com Atributos Numéricos
    private static final String _arquivo = "C:\\ArffTeste\\ionosphere.arff";

    //Arquivo com Atributos Nominais
    //private static final String _arquivo = "C:\\ArffTeste\\vote.arff";
    private static final int _QUANTIDADE = 1000;
    private static final int _GERACOES = 100;
    private static final int _NroFolds = 10;
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

    public EdaSa(Instances dados) throws Exception {
        //1° Passo - Gerar a população Inicial - 50% de probabilidade para 0 ou 1
        //2° Passo - Calcular o Fitness
        //3° Passo - Pegar os 50% Melhores indivíduos
        //4° Passo - Calcular os % de cada posição do Cromossomo
        GerarPopulacaoInicial(dados);

    }

    public void GerarPopulacaoInicial(Instances dados) throws Exception {
        try {
            //Declaração Variáveis e Objetos
            m_populacao = new Individuo[_QUANTIDADE];
            int qtdCromossomos = dados.numAttributes() - 1;

            //Inicializar a população (pelo tamanho definido)
            for (int i = 0; i < _QUANTIDADE; i++) {
                //Inicialização do Objeto
                m_populacao[i] = new Individuo(qtdCromossomos);

                //Geração da população com 50% de probabilidade
                m_populacao[i].CromossomosRandomicos(0.5);

            }

            //Cálculo do Fitness(Quantidade de 1´s encontrados X Cromossomo)
            CalculaFitness(m_populacao, qtdCromossomos, dados);

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

    public void CalculaFitness(Individuo[] Individuos, int nroAtribs, Instances dados) throws Exception {
        //Declaração Variáveis e Objetos
        try {
            //Percorrer todos os indivíduos
            for (Individuo Individuo : Individuos) {
                //Declaração variáveis e atributos
                Remove rm = new Remove();
                String regs = "";

                //Percorrer TODOS os atributos do individuo
                for (int iatr = 1; iatr < nroAtribs - 1; iatr++) {
                    //Se for igual a 1 Seleciona o Atributo
                    if (Individuo.getCromossomo(iatr) == 1) {
                        //Concatenação - Indiv começam em 1
                        regs += String.valueOf(iatr + 1).concat(",");

                    }

                }

                //Definição do Classificador
                NaiveBayes nb = new NaiveBayes();

                //Definição dos atributos
                rm.setOptions(new String[]{"-R", regs.substring(0, regs.length() - 1)});

                //Declaração do Classificador em cima do filtro estabelecido
                FilteredClassifier fc = new FilteredClassifier();
                Evaluation eval = new Evaluation(dados);

                //Filtrar os registros
                fc.setFilter(rm);

                //Setar o classificador
                fc.setClassifier(nb);

                //Calcular a Taxa de Erro
                eval.crossValidateModel(fc, dados, _NroFolds, new Random(1));

                //Atualizar o valor de Fitness do indivíduo
                Individuo.setFitnessValue(eval.errorRate());
                
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        //Declaração Variáveis e 
        EdaSa edasa = new EdaSa(new Processamento(_arquivo).lerArquivoDados());

    }

}
