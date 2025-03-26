import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class FilaSimples {
    private int servidores;
    private int capacidade;
    private Queue<Double> fila;
    private double tempo;
    private int numClientesPerdidos;
    private double[] tempoPorEstado;
    private double ultimoTempo;
    private static Random random = new Random(42);

    public FilaSimples(int servidores, int capacidade) {
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.fila = new LinkedList<>();
        this.tempo = 0;
        this.numClientesPerdidos = 0;
        this.tempoPorEstado = new double[capacidade + 1];
        this.ultimoTempo = 0;
    }

    private double nextRandom() {
        return random.nextDouble();
    }

    private void chegada() {
        double interarrivalTime = 2 + (3 * nextRandom()); // Tempo entre 2 e 5
        tempo += interarrivalTime;
        if (fila.size() < capacidade) {
            fila.add(tempo);
        } else {
            numClientesPerdidos++;
        }
        atualizarEstados();
    }

    private void saida() {
        if (!fila.isEmpty()) {
            double atendimentoTime = 3 + (2 * nextRandom()); // Tempo entre 3 e 5
            tempo += atendimentoTime;
            for (int i = 0; i < Math.min(servidores, fila.size()); i++) {
                fila.poll();
            }
        }
        atualizarEstados();
    }

    private void atualizarEstados() {
        int estado = fila.size();
        tempoPorEstado[estado] += tempo - ultimoTempo;
        ultimoTempo = tempo;
    }

    public void rodarSimulacao(int eventos) {
        tempo = 2.0; // Primeiro cliente chega no tempo 2.0
        while (eventos > 0) {
            if (nextRandom() < 0.5) {
                chegada();
            } else {
                saida();
            }
            eventos--;
        }
        exibirResultados();
    }

    private void exibirResultados() {
        System.out.println("\nSimulação G/G/" + servidores + "/" + capacidade);
        System.out.println("Clientes perdidos: " + numClientesPerdidos);
        System.out.println("Tempo global da simulação: " + String.format("%.2f", tempo));
        System.out.println("Distribuição de probabilidade dos estados da fila:");
        double totalTime = tempo;
        for (int i = 0; i < tempoPorEstado.length; i++) {
            double prob = totalTime > 0 ? tempoPorEstado[i] / totalTime : 0;
            System.out.println("Estado " + i + ": " + String.format("%.4f", prob));
        }
    }
}

public class SimuladorFila {
    public static void main(String[] args) {
        FilaSimples filaGG1_5 = new FilaSimples(1, 5);
        filaGG1_5.rodarSimulacao(100000);

        FilaSimples filaGG2_5 = new FilaSimples(2, 5);
        filaGG2_5.rodarSimulacao(100000);
    }
}
