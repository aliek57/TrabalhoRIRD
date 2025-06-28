package com.example.receitas;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class BlogReceitaScraper extends WebScrapper {

    @Override
    protected List<String> extrairLinksReceitas(Document doc) {
        List<String> receitaUrls = new ArrayList<>();

        Elements links = doc.select("article.elementor-post a.elementor-post__thumbnail__link");
        
        for (Element link : links) {
            String receitaUrl = link.attr("abs:href");

            if (receitaUrl.contains("receitadelicia.com.br/") && 
                !receitaUrl.contains("/category/") && 
                !receitaUrl.contains("/tag/") &&
                receitaUrl.matches("https://receitadelicia.com.br/[a-zA-Z0-9\\-]+/$")) {
                
                if (!receitaUrls.contains(receitaUrl)) {
                    receitaUrls.add(receitaUrl);
                    System.out.println("DEBUG (BlogReceitaScraper): Added recipe link: " + receitaUrl);
                }
            } else {
                System.out.println("DEBUG (BlogReceitaScraper): Filtered out: " + receitaUrl);
            }
        }
        System.out.println("DEBUG (BlogReceitaScraper): Filtered recipe URLs count: " + receitaUrls.size());
        return receitaUrls;
    }

    @Override
    protected String extrairTitulo(Document doc) {
        Element tituloElement = doc.selectFirst("h1.elementor-heading-title");
        if (tituloElement != null) {
            return tituloElement.text().trim();
        } else {
            return "Título não encontrado";
        }
    }

    @Override
    protected String extrairIngredientes(Document doc) {
        StringBuilder ingredientes = new StringBuilder();

        Elements ingredientesElements = doc.select("div.elementor-widget-container ul.wp-block-list li");
        
        for (Element ingredientElement : ingredientesElements) {
            ingredientes.append(ingredientElement.text().trim()).append("\n");
        }

        if (ingredientes.length() > 0) {
            return ingredientes.toString().trim();
        }
        return "Ingredientes não encontrados";
    }

    @Override
    protected String extrairModoPreparo(Document doc) {
        StringBuilder modoPreparo = new StringBuilder();

        Elements instrucaoElements = doc.select("div.elementor-widget-container ol.wp-block-list li");
        
        int stepNum = 1;
        for (Element instructionElement : instrucaoElements) {
            modoPreparo.append(stepNum++).append(". ").append(instructionElement.text().trim()).append("\n");
        }

        if (modoPreparo.length() > 0) {
            return modoPreparo.toString().trim();
        }
        return "Modo de preparo não encontrado";
    }
}