# POO_Prova
Objetivo
Implementar um software que gerencie pontos para um grupo de usuários em relação às partidas de um Campeonato de Futebol.

Objetivos Específicos
Aplicar os conceitos fundamentais do paradigma de programação orientado a objetos: encapsulamento, construtores (padrão e sobrecarregado), herança simples, polimorfismo (sobrecarga e sobreposição).
Específicos (LPOO): aplicar melhores práticas de desenvolvimento, implementação adequada dos considerando os pilares de O.O., interfaces de usuário.
Descrição do Problema
Desenvolver um sistema para gerenciamento de apostas entre participantes de um grupo relacionado às partidas de um campeonato de futebol. Cada participante poderá registrar suas apostas sobre os resultados das partidas e receberá pontuação de acordo com a precisão de seus palpites.
O funcionamento do sistema deve seguir as seguintes regras:

Cadastro de campeonato e clubes
O sistema deverá permitir o cadastro de campeonatos de futebol.
Cada campeonato será composto por no máximo 8 (oito) clubes (times) participantes.
Os clubes deverão ser cadastrados previamente no sistema.
Cadastro de partidas
As partidas do campeonato deverão ser registradas no sistema, informando os clubes participantes, a data e o horário da partida.
Cadastro de grupos e participantes
O sistema deverá permitir que um usuário crie um grupo de apostas. O sistema comporta no máximo 5 (cinco) grupos .
Os participantes poderão se cadastrar no sistema e ingressar em um grupo. O sistema comporta no máximo 5 (cinco) participantes.
Registro de apostas
Cada participante poderá registrar sua aposta para as partidas do campeonato, informando o resultado esperado e o placar previsto. As apostas podem ser realizadas no máximo até 20 minutos antes da partida.
Atualização dos resultados
Após o término de cada partida, o administrador do sistema deverá registrar o resultado real do jogo.

Cálculo de pontuação
O sistema deverá calcular automaticamente a pontuação obtida por cada participante, de acordo com as seguintes regras:
Acertar apenas o resultado da partida (vencedor ou empate): 5 pontos.
Acertar o resultado e o placar exato: 10 pontos.
Definições
Resultado da partida: identificação do vencedor do jogo ou empate.
Placar: número de gols marcados por cada equipe na partida.
Apresentação dos resultados
O sistema deverá informar a pontuação obtida por cada participante e permitir a visualização da classificação dentro do grupo.