# 🍽️ Raspador de Receitas - TSIN35D

Projeto da disciplina **TSIN35D - Recuperação, Integração e Reuso de Dados** (UTFPR) com o professor **Ivan Luiz Salvadori**.

Este projeto realiza **web scraping** de receitas culinárias a partir de três categorias do site [receiteria.com.br](https://www.receiteria.com.br/) e salva os dados em arquivos JSON para posterior reutilização.

## 📌 Objetivo

Implementar uma solução de raspagem de dados HTML com um fluxo **ETL** (Extração, Transformação e Carga), reutilizando as informações obtidas em formato estruturado.

---

## 🔧 Tecnologias utilizadas

- **Java 17+**
- **Jsoup** para raspagem de dados
- **Gson** para geração de arquivos JSON
- IDE sugerida: Eclipse for Enterprise, IntelliJ ou VSCode com suporte a Java

---

## 📁 Estrutura do projeto
```
receitas/
├── src/
│ └── com/example/receitas/
│ ├── Main.java
│ ├── Receita.java
│ ├── WebScrapper.java
│ └── JsonWriter.java
├── receitas_frango.json
├── receitas_massas.json
├── receitas_doces.json
├── pom.xml (caso use Maven)
└── README.md
```
--- 

## ▶️ Como executar o projeto

### 1. Clone o repositório
`git clone https://github.com/seu-usuario/receitas.git`

`cd receitas`

### 2. Adicione as dependências (caso use Maven)
Adicione ao pom.xml:
```
<dependencies>
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.17.2</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

Ou baixe manualmente os .jar de Jsoup e Gson e adicione ao classpath do seu projeto, caso não use Maven.

### 3. Compile e execute
Via terminal:
```
javac -cp "libs/*" src/com/example/receitas/*.java -d bin
java -cp "libs/*:bin" com.example.receitas.Main
```
Ou execute via sua IDE preferida.

---

## 🧠 Como funciona
O programa acessa 3 páginas de receitas:

Frango: https://www.receiteria.com.br/frango/

Massas: https://www.receiteria.com.br/massas/

Doces: https://www.receiteria.com.br/doces/

Cada uma é processada pela classe WebScrapper, que extrai:

<ul>
  <li>Título da receita</li>
  <li>Ingredientes</li>
  <li>Modo de preparo</li>
  <li>Link da receita</li>
</ul>

Os dados são armazenados como objetos Receita e exportados como .json pela classe JsonWriter.

---

## 📥 Fontes de dados
Todas as receitas foram obtidas para fins educacionais do site:

<a href="https://www.receiteria.com.br"> 🔗 Receiteria </a>

---

## 🧑‍🎓 Autor
Keila Prado de Jesus

Disciplina: TSIN35D – UTFPR

Professor: Ivan Luiz Salvadori
