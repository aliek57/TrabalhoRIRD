package com.example.receitas;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String urlReceiteria = "https://www.receiteria.com.br/frango/";
        String urlCozinhaLegal = "https://cozinhalegal.com.br/bolos/";
        
        JsonWriter writer = new JsonWriter();

        try {
        	
        	WebScrapper receiteriaScraper = new ReceitariaScraper();
            List<Receita> receitasFrangoReceiteria = receiteriaScraper.extrairReceitas(urlReceiteria);
            writer.escreverJson(receitasFrangoReceiteria, "receitas_frango_receiteria.json");

            WebScrapper cozinhaLegalScraper = new CozinhaLegalScraper();
            List<Receita> receitasCozinhaLegalBolos = cozinhaLegalScraper.extrairReceitas(urlCozinhaLegal);
            writer.escreverJson(receitasCozinhaLegalBolos, "receitas_bolos_cozinhalegal.json");

        } catch (IOException e) {
            System.err.println("Erro durante a raspagem ou escrita do JSON: " + e.getMessage());
        }
    }
}