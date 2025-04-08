import java.util.*;

class Cliente {
    double tempoChegada;

    public Cliente(double tempoChegada) {
        this.tempoChegada = tempoChegada;
    }
}

class Fila {
    int servidores;
    int capacidade;
    Queue<Cliente> fila;
    double[] tempoPorEstado;
    int clientesPerdidos = 0;
    double ultimoTempo = 0;
    double tempoAtual = 0;

    public Fila(int servidores, int capacidade) {
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.fila = new LinkedList<>();
        this.tempoPorEstado = new double[capacidade + 1];
    }

    public boolean podeAdicionar() {
        return fila.size() < capacidade;
    }

    public void adicionarCliente(Cliente cliente) {
        if (podeAdicionar()) {
            fila.add(cliente);
        } else {
            clientesPerdidos++;
        }
    }

    public int processarAtendimentos(double atendimentoMin, double atendimentoMax, double tempoAtual, Random rnd) {
        int atendidos = 0;
        for (int i = 0; i < servidores && !fila.isEmpty(); i++) {
            fila.poll(); 
            atendidos++;
        }
        return atendidos;
    }

    public void atualizarEstado(double tempoAtual) {
        int estado = fila.size();
        tempoPorEstado[estado] += tempoAtual - ultimoTempo;
        ultimoTempo = tempoAtual;
        this.tempoAtual = tempoAtual;
    }

    public void imprimirEstatisticas() {
        System.out.println("Clientes perdidos: " + clientesPerdidos);
        System.out.println("Distribuição de estados:");
        for (int i = 0; i < tempoPorEstado.length; i++) {
            System.out.printf("Estado %d: %.4f\n", i, tempoPorEstado[i] / tempoAtual);
        }
    }
}

public class SimuladorDuasFilasTandem {
    public static void main(String[] args) {
        int numEventos = 100000;

        // Configurações
        double chegadaMin = 1.0, chegadaMax = 4.0;
        double atendimento1Min = 3.0, atendimento1Max = 4.0;
        double atendimento2Min = 2.0, atendimento2Max = 3.0;

        Fila fila1 = new Fila(2, 3);
        Fila fila2 = new Fila(1, 5);

        Random rnd = new Random(42);
        double tempoAtual = 1.5; 

        for (int i = 0; i < numEventos; i++) {
            double interChegada = chegadaMin + (chegadaMax - chegadaMin) * rnd.nextDouble();
            tempoAtual += interChegada;
            fila1.atualizarEstado(tempoAtual);
            fila2.atualizarEstado(tempoAtual);

            fila1.adicionarCliente(new Cliente(tempoAtual));

            int atendidosFila1 = fila1.processarAtendimentos(atendimento1Min, atendimento1Max, tempoAtual, rnd);
            for (int j = 0; j < atendidosFila1; j++) {
                fila2.adicionarCliente(new Cliente(tempoAtual));
            }
            fila2.processarAtendimentos(atendimento2Min, atendimento2Max, tempoAtual, rnd);
        }

        System.out.println("Fila 1 (G/G/2/3)");
        fila1.imprimirEstatisticas();

        System.out.println("\nFila 2 (G/G/1/5)");
        fila2.imprimirEstatisticas();

        System.out.printf("\nTempo global da simulação: %.2f\n", tempoAtual);
    }
}
