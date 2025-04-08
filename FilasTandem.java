import java.util.*;

class Cliente {
    double tempoChegada;

    public Cliente(double tempoChegada) {
        this.tempoChegada = tempoChegada;
    }
}

enum TipoEvento {
    CHEGADA, SAIDA
}

class Evento implements Comparable<Evento> {
    double tempo;
    TipoEvento tipo;
    Fila fila;
    Cliente cliente;

    public Evento(double tempo, TipoEvento tipo, Fila fila, Cliente cliente) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.fila = fila;
        this.cliente = cliente;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }
}

class Fila {
    int servidores;
    int capacidade;
    Queue<Cliente> fila = new LinkedList<>();
    PriorityQueue<Double> servidoresOcupados = new PriorityQueue<>();
    double[] tempoPorEstado;
    int clientesPerdidos = 0;
    double ultimoTempo = 0;
    double tempoAtual = 0;
    double atendimentoMin, atendimentoMax;
    Random rnd;
    Fila proximaFila = null; // Para filas em tandem

    public Fila(int servidores, int capacidade, double atendimentoMin, double atendimentoMax, Random rnd) {
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.tempoPorEstado = new double[capacidade + 1];
        this.atendimentoMin = atendimentoMin;
        this.atendimentoMax = atendimentoMax;
        this.rnd = rnd;
    }

    public void setProximaFila(Fila proximaFila) {
        this.proximaFila = proximaFila;
    }

    public void atualizarEstado(double tempoAtual) {
        int estado = fila.size();
        tempoPorEstado[estado] += tempoAtual - ultimoTempo;
        ultimoTempo = tempoAtual;
        this.tempoAtual = tempoAtual;
    }

    public boolean podeAdicionar() {
        return fila.size() + servidoresOcupados.size() < capacidade;
    }

    public void adicionarCliente(Cliente cliente, double tempoAtual, PriorityQueue<Evento> agenda) {
        atualizarEstado(tempoAtual);

        if (!podeAdicionar()) {
            clientesPerdidos++;
            return;
        }

        if (servidoresOcupados.size() < servidores) {
            double duracao = atendimentoMin + (atendimentoMax - atendimentoMin) * rnd.nextDouble();
            double fimAtendimento = tempoAtual + duracao;
            servidoresOcupados.add(fimAtendimento);
            agenda.add(new Evento(fimAtendimento, TipoEvento.SAIDA, this, cliente));
        } else {
            fila.add(cliente);
        }
    }

    public void finalizarAtendimento(double tempoAtual, PriorityQueue<Evento> agenda) {
        atualizarEstado(tempoAtual);
        servidoresOcupados.poll();

        if (!fila.isEmpty()) {
            Cliente proximo = fila.poll();
            double duracao = atendimentoMin + (atendimentoMax - atendimentoMin) * rnd.nextDouble();
            double fimAtendimento = tempoAtual + duracao;
            servidoresOcupados.add(fimAtendimento);
            agenda.add(new Evento(fimAtendimento, TipoEvento.SAIDA, this, proximo));
        }
    }

    public void imprimirEstatisticas() {
        System.out.println("Clientes perdidos: " + clientesPerdidos);
        System.out.println("Distribuição de estados:");
        for (int i = 0; i < tempoPorEstado.length; i++) {
            System.out.printf("Estado %d: %.4f\n", i, tempoPorEstado[i] / tempoAtual);
        }
    }
}

public class FilasTandem {
    public static void main(String[] args) {
        int numEventos = 100000;
        double chegadaMin = 1.0, chegadaMax = 4.0;
        double atendimento1Min = 3.0, atendimento1Max = 4.0;
        double atendimento2Min = 2.0, atendimento2Max = 3.0;

        Random rnd = new Random(42);

        Fila fila1 = new Fila(2, 3, atendimento1Min, atendimento1Max, rnd);
        Fila fila2 = new Fila(1, 5, atendimento2Min, atendimento2Max, rnd);
        fila1.setProximaFila(fila2);

        PriorityQueue<Evento> agenda = new PriorityQueue<>();
        double tempoAtual = 1.5;

        for (int i = 0; i < numEventos; i++) {
            Cliente cliente = new Cliente(tempoAtual);
            agenda.add(new Evento(tempoAtual, TipoEvento.CHEGADA, fila1, cliente));

            double interChegada = chegadaMin + (chegadaMax - chegadaMin) * rnd.nextDouble();
            tempoAtual += interChegada;
        }

        tempoAtual = 0;
        while (!agenda.isEmpty()) {
            Evento evento = agenda.poll();
            tempoAtual = evento.tempo;

            if (evento.tipo == TipoEvento.CHEGADA) {
                evento.fila.adicionarCliente(evento.cliente, tempoAtual, agenda);
            } else if (evento.tipo == TipoEvento.SAIDA) {
                evento.fila.finalizarAtendimento(tempoAtual, agenda);

                if (evento.fila.proximaFila != null) {
                    evento.fila.proximaFila.adicionarCliente(new Cliente(tempoAtual), tempoAtual, agenda);
                }
            }
        }

        System.out.println("Fila 1 (G/G/2/3)");
        fila1.imprimirEstatisticas();

        System.out.println("\nFila 2 (G/G/1/5)");
        fila2.imprimirEstatisticas();

        System.out.printf("\nTempo global da simulação: %.2f\n", tempoAtual);
    }
}
