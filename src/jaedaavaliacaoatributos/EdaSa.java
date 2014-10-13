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
    static Individuo[] m_populacao;
    static double[] probabilidades = new double[_QUANTIDADE /2];
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="2° Definição dos Métodos de Get´s e Set´s">
    public Individuo[] getPopulation() {
        return this.m_populacao;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="3° Definição do Método Inicializador da Classe e Demais Métodos">
    public EdaSa() {

    }

    public static void GerarPopulacaoInicial(Instances dados) throws Exception {
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
            CalcularFitness(m_populacao, qtdCromossomos, dados);

            //Pegar os 50% melhores indivíduos da população
            melhorPopulacao = findBestPopulation(qtdCromossomos);

            //Calcular o Vetor de Probabilidades
            double percentual;

            //Percorrer a quantidade de registros existentes
            for (int j = 0; j < qtdCromossomos; j++) {
                //Inicializar a variável
                percentual = 0;

                //Percorre os cromossomos existentes na posição "j" e totaliza a valor(1 - Válido / 0 - Inválido)
                for (Individuo individuo : melhorPopulacao) {
                    //Totalizar o Indivíduo
                    percentual += individuo.getCromossomo(j);

                }

                //Resultado da Probabilidade do cromossomo da posição "j"
                probabilidades[j] = percentual == 0 ? 0 : percentual / melhorPopulacao.length;

            }

        } catch (Exception e) {
            throw e;

        }

    }

    public static void CalcularFitness(Individuo[] Individuos, int nroAtribs, Instances dados) throws Exception {
        try {
            //Declaração variáveis e atributos
            Remove rm;

            //Percorrer todos os indivíduos
            for (Individuo Individuo : Individuos) {
                //Declaração variáveis e Inicializações
                rm = new Remove();
                String regs = "";

                //Percorrer TODOS os atributos do individuo
                for (int iatr = 1; iatr < nroAtribs; iatr++) {
                    //Se for igual a 1 Seleciona o Atributo
                    if (Individuo.getCromossomo(iatr) == 1) {
                        //Concatenação - Os Atributos na base de dados(Weka) começam em "1"..."N"
                        regs += String.valueOf(iatr + 1).concat(",");

                    }

                }

                //Definição do Classificador
                NaiveBayes nb = new NaiveBayes();

                //Definição dos atributos - { Remover o último "," }
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

    public static Individuo[] findBestPopulation(int nroCromossomos) {
        //Declaração Variáveis e Objetos
        Individuo[] bestPopulation = new Individuo[_QUANTIDADE / 2];
        List<Individuo> dados = new ArrayList<>();

        //Percorrer todos os indivíduos
        for (Individuo individuo : m_populacao) {
            //Adicionar o Indivíduo
            dados.add(new Individuo(individuo.getCromossomo(), individuo.getFitnessValue()));

        }

        //Ordenar decrescente
        Collections.sort(dados);

        //Percorrer o vetor e adicionar os melhores indivíduos (50% deles)
        for (int i = 0; i < _QUANTIDADE / 2; i++) {
            //Alocar memória p/ o Objeto
            bestPopulation[i] = new Individuo(dados.get(i).getCromossomo().length);

            //Atribuições das propriedades
            bestPopulation[i].setCromossomo(dados.get(i).getCromossomo());
            bestPopulation[i].setFitnessValue(dados.get(i).getFitnessValue());

        }

        //Definir o retorno
        return bestPopulation;

    }
    // </editor-fold>

    public static void main(String[] args) throws Exception {
        //Declaração Variáveis e Objetos
        Instances dados = new Instances(new Processamento(_arquivo).lerArquivoDados());

        //1° Passo - Gerar a população Inicial - 50% de probabilidade para 0 ou 1
        //2° Passo - Calcular o Fitness
        //3° Passo - Pegar os 50% Melhores indivíduos
        //4° Passo - Calcular os % de cada posição do Cromossomo
        GerarPopulacaoInicial(dados);
        int nroGeracoes = 0;

        //Enquanto puder processar
        while (nroGeracoes < _QUANTIDADE) {
            //Gerar População
            GerarPopulacao(dados);

            //Atualizar a posição
            nroGeracoes += 1;

        }

    }

    private static void GerarPopulacao(Instances dados) {
        try {
            //Declaração Variáveis e Objetos
            m_populacao = new Individuo[_QUANTIDADE];
            int qtdCromossomos = dados.numAttributes() - 1;

            //Inicializar a população (pelo tamanho definido)
            for (int i = 0; i < _QUANTIDADE; i++) {
                //Inicialização do Objeto
                m_populacao[i] = new Individuo(qtdCromossomos);

                //Geração da população com 50% de probabilidade
                m_populacao[i].CromossomosRandomicos(probabilidades);

            }

            //Cálculo do Fitness(Quantidade de 1´s encontrados X Cromossomo)
            CalcularFitness(m_populacao, qtdCromossomos, dados);

            //Pegar os 50% melhores indivíduos da população
            melhorPopulacao = findBestPopulation(qtdCromossomos);

            //Calcular o Vetor de Probabilidades
            double percentual;

            //Percorrer a quantidade de cromossomos existentes
            for (int j = 0; j < qtdCromossomos; j++) {
                //Inicializar a variável
                percentual = 0;

                //Percorre os cromossomos existentes na posição "j" e totaliza a valor(1 - Válido / 0 - Inválido)
                for (Individuo individuo : melhorPopulacao) {
                    //Totalizar o Indivíduo
                    percentual += individuo.getCromossomo(j);

                }

                //Resultado da Probabilidade do cromossomo da posição "j"
                probabilidades[j] = percentual == 0 ? 0 : percentual / melhorPopulacao.length;

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }
    
}
