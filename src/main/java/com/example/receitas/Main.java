package com.example.receitas;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String urlFrango = "https://www.receiteria.com.br/frango/";
        String urlMassas = "https://www.receiteria.com.br/massas/";
        String urlDoces = "https://www.receiteria.com.br/doces/";

        WebScrapper scraperReceitaria = new ReceitariaScraper();
        JsonWriter writer = new JsonWriter();

        try {
            List<Receita> receitasFrango = scraperReceitaria.extrairReceitas(urlFrango);
            writer.escreverJson(receitasFrango, "receitas_frango.json");

            List<Receita> receitasMassas = scraperReceitaria.extrairReceitas(urlMassas);
            writer.escreverJson(receitasMassas, "receitas_massas.json");

            List<Receita> receitasDoces = scraperReceitaria.extrairReceitas(urlDoces);
            writer.escreverJson(receitasDoces, "receitas_doces.json");

        } catch (IOException e) {
            System.err.println("Erro durante a raspagem ou escrita do JSON: " + e.getMessage());
        }
    }
}