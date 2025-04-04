# Ares (Legacy)

Ares (Legacy) é um projeto que permite a utilização de comandos no Discord para gerar imagens e textos com inteligência artificial. O projeto teve sua origem quando apenas o modelo GPT-4 Base estava disponível e não existia um SDK oficial da OpenAI. 
Ele só permite apenas um token OpenAI para todas as operações.

> [!CAUTION]
> Este programa é destinado exclusivamente a fins educacionais e pode incluir erros.

## Evolução

Atualmente, o Ares é um projeto privado que foi reconstruído em C# e se tornou muito mais robusto, permitindo:

- Operação assíncrona para maior eficiência;
- Criação de canais de texto para tarefas com AI;
- Configurações personalizadas para cada guilda;
- Suporte a múltiplos provedores:
  - **OpenAI**
  - **Anthropic**
  - **DeepSeek**
  - **xAI**
- Funcionalidades avançadas:
  - Conversas
  - Conversão de texto para fala (TTS)
  - Geração de imagens
  - Vision (interpretação de imagens, etc)
 
Tudo isso podendo ser feito em um canal de texto privado. (No Discord)

## Como Utilizar (Usuário)

1. [Adicione o Ares ao seu servidor](https://discord.com/oauth2/authorize?client_id=1278447277907644578&scope=bot&permissions=2147611664)

## Como Utilizar (Desenvolvedor)

1. Clone este repositório:
   ```sh
   git clone https://github.com/rodriaum/ares-discord-legacy.git
   ```
2. Instale as dependências necessárias.
3. Configure as credenciais e tokens das APIs.
4. Execute o bot no seu servidor Discord.

## Licença

[MIT License](https://github.com/rodriaum/ares-discord-legacy?tab=MIT-1-ov-file#MIT-1-ov-file)
