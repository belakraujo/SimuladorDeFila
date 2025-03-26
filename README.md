### **Resumo do Código**  
Este código implementa um **simulador de filas G/G/c/K** em Java. Ele usa um gerador de números aleatórios para simular a chegada e atendimento de clientes, gerenciando estatísticas como clientes perdidos e distribuição de estados da fila.  

**Duas filas são simuladas:**  
1. **G/G/1/5** → 1 servidor, capacidade máxima de 5 clientes.  
2. **G/G/2/5** → 2 servidores, capacidade máxima de 5 clientes.  

A simulação começa com um cliente chegando no tempo **2.0** e roda até **100.000 eventos** serem processados.  


### **Como Rodar o Código**  
1. **Copie e salve o código** em um arquivo chamado `Fila.java`.  
2. **Compile o código** no terminal/cmd:  
   ```bash
   javac Fila.java
   ```  
3. **Execute a simulação** com:  
   ```bash
   java Fila
   ```  
