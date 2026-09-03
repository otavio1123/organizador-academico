Organizador Acadêmico

O Organizador Acadêmico é um sistema web desenvolvido para auxiliar estudantes de graduação na organização e no acompanhamento de sua rotina acadêmica.

A proposta surgiu da necessidade de centralizar informações como disciplinas, semestres, atividades, avaliações, notas e prazos, evitando que o estudante precise controlar essas informações em diferentes lugares. A primeira versão tem como foco estudantes de graduação que desejam acompanhar melhor sua trajetória acadêmica.

Sobre o projeto

O sistema foi desenvolvido como Projeto Final de Curso, unindo conceitos estudados durante a graduação com o desenvolvimento de uma aplicação web.

A aplicação possui frontend e backend separados, permitindo organizar melhor as responsabilidades do sistema e facilitar sua manutenção e evolução.

Entre as principais funcionalidades estão:

Cadastro e login;
Recuperação e redefinição de senha;
Perfil acadêmico;
Cadastro e organização de disciplinas;
Controle de informações acadêmicas;
Acompanhamento da rotina do estudante.

Essas funcionalidades fazem parte da proposta inicial definida para o projeto.

Estrutura do código

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
Frontend

O frontend é responsável pela interface que o usuário utiliza.

Foi desenvolvido utilizando:

HTML;
CSS;
JavaScript;
SweetAlert2.

Os arquivos HTML representam as páginas do sistema, os arquivos CSS controlam a aparência e os arquivos JavaScript cuidam das interações e da comunicação com a API.

Backend

O backend concentra as regras de negócio e a comunicação com o banco de dados.

As principais tecnologias utilizadas são:

Java;
Spring Boot;
Spring Data JPA;
Spring Validation;
Spring Security Crypto;
Maven;
Lombok.

A comunicação entre frontend e backend acontece por meio de uma API REST, utilizando requisições HTTP e dados em formato JSON.

Organização das classes

A lógica do backend segue uma separação de responsabilidades:

Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL

O Controller recebe as requisições da aplicação.

O Service concentra as regras de negócio, como validações e processos do sistema.

O Repository é responsável pelo acesso aos dados utilizando Spring Data JPA.

O banco utilizado é o PostgreSQL, hospedado no Supabase.

Banco de dados

O banco armazena as informações necessárias para o funcionamento do sistema, incluindo dados dos usuários, perfil acadêmico, disciplinas e recuperação de senha.

Na recuperação de senha, por exemplo, o sistema utiliza um token temporário e armazena seu hash no banco, juntamente com informações de criação, expiração e utilização.

As senhas dos usuários são armazenadas utilizando BCrypt, evitando que sejam salvas em texto puro.

Segurança e privacidade

O projeto considera segurança e privacidade desde o desenvolvimento das funcionalidades. Entre as medidas adotadas estão a validação dos dados, proteção das credenciais e cuidados para evitar a exposição desnecessária das informações dos usuários.

A proposta também considera princípios relacionados à LGPD, principalmente no tratamento dos dados pessoais utilizados pelo sistema.

Tecnologias

Frontend: HTML, CSS, JavaScript e SweetAlert2.

Backend: Java, Spring Boot, Spring Data JPA, Spring Validation, Spring Security Crypto, Maven e Lombok.

Banco de dados: PostgreSQL com Supabase.

Versionamento: Git e GitHub.

link Figma https://www.figma.com/design/CndvloRCXKUTNUcvnWiNIo/ORGANIZAR-ACAD%C3%8AMICO?node-id=0-1&p=f&t=MljgUBvIpUjUbDbi-0
link Git Pages  