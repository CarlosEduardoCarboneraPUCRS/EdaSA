package jaedaavaliacaoatributos;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Processamento {

    //<editor-fold defaultstate="collapsed" desc="Declaração Atributos e Método(s) Construtor(es) da Classe">    
    //Declaração de objetos privados
    private String _caminhoDados;

    //Declaração de objetos públicos
    public static final int _qtdElitismo = 2;

    public String getCaminhoDados() {
        return _caminhoDados;
    }

    public void setCaminhoDados(String caminho) {
        this._caminhoDados = caminho;
    }

    public Processamento(String local) {
        //Inicialização dos atributos
        this._caminhoDados = local;

    }

    public Processamento() {

    }
    //</editor-fold>        

    //<editor-fold defaultstate="collapsed" desc="Métodos de Processamento Diversos">        
    //Leitura do Arquivo
    public Instances lerArquivoDados() {
        //Declaração Variáveis e Objetos
        Instances dados = null;

        try {
            //Inicialização da Leitura
            dados = new DataSource(_caminhoDados).getDataSet();

            //Setar o atribArv1 classe
            if (dados.classIndex() == -1) {
                dados.setClassIndex(dados.numAttributes() - 1);

            }

        } catch (Exception e) {
            //Se ocorreu alguma exceção
            e.printStackTrace();

        }

        //Definição do Retorno
        return dados;

    }   
    
    //</editor-fold>  

}
