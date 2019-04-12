# Sistemas Distribuídos - Alocação de servidores na nuvem
Projeto Sistemas Distribuídos 2018/2019, Mestrado Integrado em Engenharia Informática, Universidade do Minho, Braga - Portugal

Neste projeto pretende-se desenvolver um serviço de alocação de servidores na nuvem e de contabilização do custo incorrido pelos utilizadores.

### Como executar o projeto:

Na diretoria principal execute os seguintes passos:

  - Servidor:

```
  $ ./compile_server.sh
  $ ./run_server.sh
```

  - Clientes:
 ```
  $ ./compile_client.sh
  $ ./run_client.sh $n 
  ```
 (Onde n é a posição da janela da interface no ecrã, varia entre 1-4)

- Opcional:    
	 - Bots
  ```
  $ ./compile_bots.sh
  $ ./run_bots.sh $n_bots
  ```
(Onde n_bots é o número de bots pretendido)

### Arquitetura final

![Screenshot1](arquitetura.png)
![Screenshot2](interface.png)

## Autores

* [Joel Rodrigues](https://github.com/JoelRodrigues58)
* [Raphael Oliveira](https://github.com/raphael28)
* [Francisco Araújo](https://github.com/franciscoaraujo51)
* [Fábio Araújo](https://github.com/narcos088)
