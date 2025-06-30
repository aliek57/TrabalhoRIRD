package com.example.receitas;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String urlReceiteria = "https://www.receiteria.com.br/frango/";
        String urlCozinhaLegal = "https://cozinhalegal.com.br/bolos/";
        String urlBlogReceita = "https://receitadelicia.com.br/aves/";
        
        JsonWriter writer = new JsonWriter();

        try {      	
        	WebScrapper receiteriaScraper = new ReceitariaScraper();
            List<Receita> receitasFrangoReceiteria = receiteriaScraper.extrairReceitas(urlReceiteria);
            writer.escreverJson(receitasFrangoReceiteria, "receitas_receiteria.json");

            WebScrapper cozinhaLegalScraper = new CozinhaLegalScraper();
            List<Receita> receitasCozinhaLegalBolos = cozinhaLegalScraper.extrairReceitas(urlCozinhaLegal);
            writer.escreverJson(receitasCozinhaLegalBolos, "receitas_cozinhalegal.json");
            
            WebScrapper blogReceitaScraper = new BlogReceitaScraper();
            List<Receita> receitasAvesReceitaDelicia = blogReceitaScraper.extrairReceitas(urlBlogReceita);
            writer.escreverJson(receitasAvesReceitaDelicia, "receitas_receitadelicia.json");

        } catch (IOException e) {
            System.err.println("Erro durante a raspagem ou escrita do JSON: " + e.getMessage());
        }
    }
}