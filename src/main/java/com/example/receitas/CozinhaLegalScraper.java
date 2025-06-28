package com.example.receitas;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CozinhaLegalScraper extends WebScrapper {

    @Override
    protected List<String> extrairLinksReceitas(Document doc) {
        List<String> receitaUrls = new ArrayList<>();
        Elements links = doc.select("article a[href*=\"cozinhalegal.com.br/\"]");
        
        for (Element link : links) {
            String receitaUrl = link.attr("abs:href");
            if (receitaUrl.contains("cozinhalegal.com.br/") &&
                !receitaUrl.contains("/category/") &&
                !receitaUrl.contains("/tag/") &&
                !receitaUrl.equals("https://cozinhalegal.com.br/") &&
                receitaUrl.matches("https://cozinhalegal.com.br/[a-zA-Z0-9\\-]+/$")) {
                
                if (!receitaUrls.contains(receitaUrl)) {
                    receitaUrls.add(receitaUrl);
                    System.out.println("DEBUG (CozinhaLegalScraper): Added recipe link: " + receitaUrl);
                }
            } else {
                System.out.println("DEBUG (CozinhaLegalScraper): Filtered out: " + receitaUrl);
            }
        }
        System.out.println("DEBUG (CozinhaLegalScraper): Filtered recipe URLs count: " + receitaUrls.size());
        return receitaUrls;
    }

    @Override
    protected String extrairTitulo(Document doc) {
        Element tituloElement = doc.selectFirst(".elementor-widget-container .elementor-heading-title");
        if (tituloElement != null) {
            return tituloElement.text().trim();
        } else {
            return "Título não encontrado";
        }
    }

    @Override
    protected String extrairIngredientes(Document doc) {
        StringBuilder ingredientes = new StringBuilder();
        Elements ingredientesElements = doc.select(".wprm-recipe-ingredients-container .wprm-recipe-ingredient");
        
        for (Element ingredientElement : ingredientesElements) {
            String amount = ingredientElement.selectFirst(".wprm-recipe-ingredient-amount") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-amount").text().trim() : "";
            String unit = ingredientElement.selectFirst(".wprm-recipe-ingredient-unit") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-unit").text().trim() : "";
            String name = ingredientElement.selectFirst(".wprm-recipe-ingredient-name") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-name").text().trim() : "";

            ingredientes.append(amount).append(" ").append(unit).append(" ").append(name).append("\n");
        }

        if (ingredientes.length() > 0) {
            return ingredientes.toString().trim();
        }
        return "Ingredientes não encontrados";
    }

    @Override
    protected String extrairModoPreparo(Document doc) {
        StringBuilder modoPreparo = new StringBuilder();
     
        Elements instrucaoElements = doc.select(".wprm-recipe-instructions-container .wprm-recipe-instruction");
        
        int stepNum = 1;
        for (Element instructionElement : instrucaoElements) {

            Element textElement = instructionElement.selectFirst(".wprm-recipe-instruction-text");
            if (textElement != null) {
                modoPreparo.append(stepNum++).append(". ").append(textElement.text().trim()).append("\n");
            } else {
                System.out.println("DEBUG (CozinhaLegalScraper):   Inside instruction step, '.wprm-recipe-instruction-text' did NOT find text element.");
            }
        }

        if (modoPreparo.length() > 0) {
            return modoPreparo.toString().trim();
        }
        return "Modo de preparo não encontrado";
    }
}