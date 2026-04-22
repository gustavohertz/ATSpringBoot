# Solução da Questão 2: Investigação de Desempenho

## O Problema
O endpoint `/missoes/top15dias` estava consultando a *materialized view* `operacoes.mv_painel_tatico_missao` diretamente do banco a cada requisição. Como esse endpoint consolida um dashboard dinâmico com grande visibilidade pela guild de operações (agregando métricas variadas como COUNT, SUM e AVG), execuções excessivamente frequentes pela API poderiam sobrecarregar o banco de dados desnecessariamente, atrasando a resposta.

## Estratégia Escolhida e Modificações
Para contornar o processamento recorrente e redundante de consultas caras no banco, a estratégia adotada foi a aplicação nativa de **Caching em Memória com Expiração Temporizada (Time-To-Live)**. Dessa forma economizamos recurso computacional (sem adicionar dependências adicionais pesadas de terceiros) e evitamos criar um estrangulamento I/O (Input/Output).

1. **Habilitação de Cache Global e Agendamentos (`Application.java`)**: 
   * Adicionamos a anotação `@EnableCaching` para autorizar o ecossistema do Spring a criar espaços em memória que armazenassem retornos fixos de métodos.
   * Adicionamos `@EnableScheduling` para habilitarmos varreduras automatizadas nos processos em segundo plano.

2. **Interceptando as Consultas com Cache (`PainelTaticoMissaoService.java`)**:
   * Anotamos o método `getTop10MissoesTaticas()` com **`@Cacheable("topMissoesCache")`**. A partir desse momento, quando a primeira requisição atinge a rota HTTP, o Spring intercepta, realiza a consulta demorada no banco e arquiva a lista serializada dessas top 15 missões na memória local da API (em um mapa usando "topMissoesCache"). Se no segundo seguinte ocorrerem mil chamadas simultâneas, **todas elas ignorarão o banco** e retornarão de forma relâmpago entregando a lista salva em cache!

3. **Garantindo Consistência e Frescor (Time-To-Live Manual)**:
   * A desvantagem de usar cache puro é a obsolescência (se os dados mudam no banco, o usuário não enxergaria a mudança). Para trazer um "nível aceitável" de frescor exigido pela tarefa, escrevemos um método "gatilho" vazio chamado `limparCacheTopMissoes()` anotado com **`@CacheEvict(value = "topMissoesCache", allEntries = true)`** associado a um cronômetro **`@Scheduled(fixedRate = 60000)`**.
   * Isso força a API a mandar um expurgo em disco/memória no cache "topMissoesCache" silenciosamente e automaticamente a cada exatos **60 segundos** (1 minuto). Ou seja, se o banco mudar, no pior cenário o usuário só vê um dado com 1 minuto de atraso, o que é plenamente aceitável para um Painel Tático analítico, reduzindo centenas de chamadas pesadas por minuto a apenas 1 processamento por minuto. E sem encostar um dedo sequer em ALTER nas tabelas do Banco de Dados!
