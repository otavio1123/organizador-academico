Projeto desenvolvido como Trabalho de Conclusão de Curso

O Organizador Acadêmico é um sistema web desenvolvido para auxiliar estudantes de graduação na organização e no acompanhamento da rotina acadêmica.

A proposta surgiu da necessidade de centralizar informações como semestres, disciplinas, atividades, avaliações, notas e prazos em um único sistema, facilitando o acompanhamento da trajetória acadêmica do estudante.

Sobre o projeto

O sistema está sendo desenvolvido como Projeto Final de Curso, aplicando conceitos estudados durante a graduação no desenvolvimento de uma aplicação web.

A aplicação possui frontend e backend separados, permitindo uma melhor organização das responsabilidades do sistema e facilitando sua manutenção e evolução.

Entre as funcionalidades previstas para o projeto estão:

Cadastro e login de usuários;
Recuperação e redefinição de senha;
Perfil acadêmico;
Gerenciamento de semestres;
Gerenciamento de disciplinas;
Organização de atividades;
Avaliações e notas;
Acompanhamento de prazos acadêmicos.

O projeto está sendo desenvolvido de forma incremental, com as funcionalidades sendo implementadas e testadas durante as etapas do PFC.

Funcionalidades atuais

Atualmente o sistema possui:

Cadastro de usuários;
Validação de e-mail já cadastrado;
Login com validação de senha;
Armazenamento de senhas utilizando BCrypt;
Recuperação de senha por e-mail;
Geração de token temporário para recuperação;
Armazenamento do hash do token no banco de dados;
Validade de 15 minutos para o token;
Termos de Uso;
Política de Privacidade;
Estrutura inicial da Home.

A etapa de redefinição definitiva da senha ainda está em desenvolvimento.

Estrutura do projeto

O projeto está dividido principalmente entre frontend e backend:

organizador-academico/

├── backend/
│   └── organizador-api/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   └── resources/
│       │   └── test/
│       └── pom.xml
│
├── frontend/
│   ├── css/
│   ├── js/
│   ├── cadastro.html
│   ├── home.html
│   ├── index.html
│   ├── privacidade.html
│   ├── recuperar.html
│   ├── redefinir-senha.html
│   └── termos.html
│
└── Readme.md

Frontend

Atualmente são utilizados:

HTML;
CSS;
JavaScript;
SweetAlert2.

Os arquivos HTML representam as páginas do sistema, o CSS é responsável pela parte visual e o JavaScript realiza as interações com o usuário e a comunicação com a API do backend.

Backend

As principais tecnologias utilizadas são:

Java 17;
pring Boot;
Spring Web;
Spring Data JPA;
Spring Validation;
Spring Security Crypto;
Maven;
Lombok.

A comunicação entre o frontend e o backend acontece por meio de uma API REST, utilizando requisições HTTP e dados no formato JSON.

Organização do backend

O backend segue uma separação de responsabilidades entre as principais camadas da aplicação:

Controller  
↓  
Service  
↓  
Repository  
↓  
PostgreSQL

O Controller recebe as requisições feitas pelo frontend.

O Service concentra regras e processos da aplicação.

O Repository realiza o acesso aos dados utilizando Spring Data JPA.

O banco de dados utilizado é PostgreSQL, hospedado no Supabase.

Banco de dados

O PostgreSQL é utilizado para armazenar os dados necessários para o funcionamento da aplicação.

Atualmente o banco possui informações relacionadas aos usuários e à recuperação de senha, e será expandido conforme os demais módulos forem desenvolvidos.

Na recuperação de senha, o sistema gera um token temporário e armazena apenas o hash desse token no banco de dados, juntamente com informações de criação, expiração e utilização.

As senhas dos usuários também não são armazenadas em texto puro. Antes de serem persistidas, elas são protegidas utilizando BCrypt.

Segurança e privacidade

O projeto considera aspectos de segurança e privacidade durante o desenvolvimento das funcionalidades.

Entre as medidas utilizadas atualmente estão:

Criptografia de senhas com BCrypt;
Validação de dados recebidos pela API;
Resposta genérica durante a recuperação de senha;
Token temporário para redefinição de senha;
Armazenamento apenas do hash do token;
Separação das credenciais do código por meio de variáveis de ambiente;
Termos de Uso;
Política de Privacidade.

O projeto também considera princípios relacionados à LGPD no tratamento dos dados pessoais utilizados pela aplicação.

Tecnologias

Frontend: HTML, CSS, JavaScript e SweetAlert2.

Backend:Java 17, Spring Boot, Spring Web, Spring Data JPA, Spring Validation, Spring Security Crypto, Maven e Lombok.

Banco de dados: PostgreSQL com Supabase.

Versionamento: Git e GitHub.

Protótipo

Figma - Organizador Acadêmico  https://www.figma.com/design/CndvloRCXKUTNUcvnWiNIo/ORGANIZAR-ACAD%C3%8AMICO?node-id=0-1&p=f&t=MljgUBvIpUjUbDbi-0

Acesso ao sistema

GitHub Pages - Organizador Acadêmico https://otavio1123.github.io/organizador-academico/frontend/
