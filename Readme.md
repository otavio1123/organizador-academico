Organizador Acadêmico

O Organizador Acadêmico é um sistema desenvolvido para ajudar estudantes a manter sua rotina acadêmica mais organizada em um único lugar.

A proposta do projeto é reunir informações importantes da vida acadêmica de forma simples, permitindo que o aluno acompanhe seus estudos, disciplinas, faltas e outras informações relacionadas à sua trajetória na faculdade.

Sobre o projeto

Durante a vida acadêmica, é comum o estudante precisar consultar diferentes informações em lugares diferentes. Pensando nisso, o Organizador Acadêmico foi criado para centralizar essas informações e facilitar o acompanhamento da rotina de estudos.

O sistema está sendo desenvolvido como uma aplicação web, com uma separação entre o frontend e o backend. Essa organização permite que cada parte do sistema tenha uma responsabilidade bem definida e facilita a manutenção e a evolução do projeto.

Objetivo

O principal objetivo é oferecer ao estudante uma ferramenta para organizar sua vida acadêmica e acompanhar seu progresso ao longo do curso.

Entre as informações que fazem parte da proposta do sistema estão:

Dados pessoais do estudante;

Informações do curso e da instituição;

Quantidade de semestres do curso;

Disciplinas;

Controle de faltas;

Organização das informações acadêmicas;

Acompanhamento da rotina do estudante.

A ideia é que o sistema possa crescer com o projeto e receber novas funcionalidades conforme novas necessidades forem identificadas.

Funcionalidades

O projeto conta com uma estrutura de autenticação para permitir que cada usuário tenha acesso às suas próprias informações.

Entre as funcionalidades desenvolvidas ou em desenvolvimento estão:

Cadastro e login

O estudante pode criar uma conta utilizando e-mail e senha e posteriormente utilizar esses dados para acessar o sistema.

Recuperação de senha

O sistema possui um fluxo para recuperação de senha.

Quando o usuário solicita a recuperação, o sistema utiliza o e-mail informado para iniciar o processo. Por segurança, a resposta da solicitação não revela se existe ou não uma conta cadastrada para aquele endereço.

Quando uma solicitação válida é criada, é gerado um token temporário para permitir a redefinição da senha.

Redefinição de senha

A redefinição acontece por meio de um link contendo um token temporário.

O sistema deve verificar se o token é válido, se ainda está dentro do prazo de validade e se já não foi utilizado antes de permitir a alteração da senha.

A nova senha é armazenada de forma protegida, sem guardar a senha original no banco de dados.

Perfil acadêmico

O usuário pode possuir informações relacionadas ao seu perfil acadêmico, como:

Curso;

Instituição;

Semestre;

Faltas.

Disciplinas

O sistema possui uma estrutura para relacionar disciplinas ao perfil acadêmico do estudante.

Isso permite que as disciplinas sejam organizadas de acordo com o perfil de cada usuário.

Tecnologias utilizadas

Frontend

O frontend é responsável pela interface que o usuário utiliza para acessar o sistema.

Tecnologias utilizadas:

HTML;

CSS;

JavaScript;

SweetAlert2 para mensagens e interações com o usuário.

Backend

O backend concentra as regras de negócio, autenticação, comunicação com o banco de dados e APIs utilizadas pelo frontend.

Tecnologias utilizadas:

Java;

Spring Boot;

Spring Data JPA;

Spring Validation;

Spring Security Crypto;

Maven;

Lombok.

Banco de dados

O sistema utiliza PostgreSQL como banco de dados.

O banco está hospedado no Supabase, que também fornece a infraestrutura necessária para acesso ao PostgreSQL.

Estrutura do projeto

O projeto está dividido principalmente em duas partes:

organizador-academico/
├── backend/
│   └── organizador-api/
│       ├── src/
│       │   └── main/
│       │       ├── java/
│       │       └── resources/
│       └── pom.xml
│
└── frontend/
    ├── css/
    ├── js/
    ├── index.html
    └── Recuperar.html

Essa separação facilita o desenvolvimento, pois a interface e as regras do sistema ficam organizadas em projetos diferentes.

Banco de dados

Entre as estruturas utilizadas pelo sistema estão:

Usuarios

Responsável pelos dados básicos de acesso do usuário.

Exemplos de informações:

ID;

Nome;

E-mail;

Senha.

perfis_academicos

Relaciona as informações acadêmicas ao usuário.

Exemplos:

Curso;

Instituição;

Semestre;

Faltas.

disciplina

Armazena as disciplinas relacionadas ao perfil acadêmico.

recuperacoes_senha

Utilizada no processo de recuperação de senha.

A estrutura utiliza informações como:

Token armazenado em formato de hash;

Data de criação;

Data de expiração;

Data de utilização;

Usuário relacionado.

O objetivo dessa estrutura é evitar que o token utilizado no processo de recuperação fique armazenado diretamente no banco de dados.

Segurança e privacidade

A segurança é considerada desde a construção das funcionalidades do sistema.

O projeto utiliza mecanismos para proteger informações de autenticação e evitar a exposição desnecessária de dados dos usuários.

A recuperação de senha, por exemplo, foi planejada para não informar diretamente ao usuário se determinado e-mail possui ou não uma conta cadastrada. Isso evita que a funcionalidade seja utilizada para descobrir quais e-mails estão registrados no sistema.

As senhas também não devem ser armazenadas em texto puro. O projeto utiliza BCrypt para realizar o armazenamento seguro das senhas.

Além disso, informações utilizadas para conexão com serviços externos, como banco de dados e servidor de e-mail, são configuradas por meio de variáveis de ambiente em vez de serem colocadas diretamente no código-fonte.

LGPD

O projeto considera princípios relacionados à Lei Geral de Proteção de Dados Pessoais (LGPD).

A ideia é trabalhar com os dados necessários para o funcionamento do sistema e evitar a exposição desnecessária das informações dos usuários.

Entre os pontos considerados estão:

Proteção das credenciais de acesso;

Controle de acesso às informações;

Uso de senha armazenada de forma segura;

Proteção dos tokens de recuperação;

Evitar a exposição da existência de contas durante a recuperação de senha;

Separação de informações sensíveis das configurações públicas do projeto;

Utilização responsável dos dados pessoais.

A adequação à LGPD deve continuar sendo analisada durante a evolução do projeto, principalmente conforme novas funcionalidades e novos tipos de dados forem adicionados.

Configuração do ambiente

O backend utiliza variáveis de ambiente para acessar o banco de dados e o serviço de e-mail.

Exemplo:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

Antes de executar o projeto, essas variáveis precisam estar configuradas no ambiente.

Por segurança, senhas, tokens, credenciais do banco e credenciais de e-mail não devem ser adicionados ao Git ou ao arquivo público de configuração.

Executando o backend

Entre na pasta do backend:

cd C:\Users\otavi\Downloads\organizador-academico\backend\organizador-api

Depois, configure as variáveis de ambiente necessárias.

No Windows PowerShell, por exemplo:

$env:DB_URL="sua-url-do-banco"
$env:DB_USERNAME="seu-usuario"
$env:DB_PASSWORD="sua-senha"
$env:MAIL_USERNAME="seu-email"
$env:MAIL_PASSWORD="sua-senha-ou-credencial-do-email"

Para iniciar o projeto:

.\mvnw spring-boot:run

O backend será executado na porta 8080, caso nenhuma outra porta tenha sido configurada.

Executando o frontend

O frontend pode ser aberto por meio de um servidor local ou por uma ferramenta de desenvolvimento adequada para aplicações web.

A comunicação com o backend acontece por meio das APIs disponibilizadas pelo projeto.

Durante o desenvolvimento, o backend normalmente estará disponível em:

http://localhost:8080

Versão do Java

O projeto possui a configuração de compilação definida no Maven por meio da propriedade:

<java.version>17</java.version>

Isso significa que o projeto foi configurado para utilizar Java 17 como versão de referência para compilação.

Durante o desenvolvimento, o ambiente local utilizado pode possuir uma versão diferente do Java. Por exemplo, uma máquina pode estar executando Java 25 enquanto o projeto continua configurado para Java 17.

Por isso, é importante diferenciar a versão instalada na máquina da versão definida pelo projeto. A configuração do projeto deve ser tratada como referência para garantir maior consistência entre os ambientes de desenvolvimento.

Desenvolvimento

O projeto ainda está em desenvolvimento e novas funcionalidades podem ser adicionadas conforme a necessidade.

A intenção é manter uma estrutura simples de entender, permitindo que novas partes do sistema sejam desenvolvidas sem comprometer o que já foi construído.

Próximos passos

Algumas das próximas etapas do projeto incluem:

Finalizar o fluxo completo de recuperação de senha;

Finalizar a redefinição de senha;

Melhorar as validações de cadastro e autenticação;

Evoluir o gerenciamento do perfil acadêmico;

Ampliar o gerenciamento de disciplinas;

Adicionar novas funcionalidades de organização acadêmica;

Melhorar o controle de segurança;

Realizar testes da aplicação;

Revisar continuamente os pontos relacionados à privacidade e à LGPD;

Melhorar a documentação do projeto.

Status

O projeto está em desenvolvimento.

As funcionalidades são implementadas e testadas gradualmente, buscando manter o código organizado e facilitar a evolução do sistema.

Autor

Projeto desenvolvido para fins de estudo e desenvolvimento de uma aplicação web voltada à organização acadêmica.