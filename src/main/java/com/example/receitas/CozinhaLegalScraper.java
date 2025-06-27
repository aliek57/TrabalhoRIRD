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
        // Re-evaluating the selector for links on the category page.
        // Let's try to find all <a> tags that are descendants of <article> elements,
        // and whose href contains the site's domain to ensure they are internal links.
        Elements links = doc.select("article a[href*=\"cozinhalegal.com.br/\"]");
        System.out.println("DEBUG (CozinhaLegalScraper): Selector 'article a[href*=\"cozinhalegal.com.br/\"]' found " + links.size() + " potential recipe links on category page before filtering.");

        for (Element link : links) {
            String receitaUrl = link.attr("abs:href");
            // Filtering: we want links to specific recipes, not categories or other pages.
            // Recipe URLs on Cozinha Legal typically follow a pattern like /post-name/
            // and do not contain /category/ or /tag/.
            if (receitaUrl.contains("cozinhalegal.com.br/") && // Ensure it's the right domain
                !receitaUrl.contains("/category/") &&         // Exclude category links
                !receitaUrl.contains("/tag/") &&              // Exclude tag links
                !receitaUrl.equals("https://cozinhalegal.com.br/") && // Exclude homepage
                receitaUrl.matches("https://cozinhalegal.com.br/[a-zA-Z0-9\\-]+/$")) { // Must end with a word/dash/slash
                
                // Add a check to prevent adding the same link multiple times if different selectors point to it
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
        // Based on the example, the main title is in h1.entry-title
        Element tituloElement = doc.selectFirst("h1.entry-title");
        if (tituloElement != null) {
            System.out.println("DEBUG (CozinhaLegalScraper): Selector 'h1.entry-title' found title: " + tituloElement.text());
            return tituloElement.text().trim();
        } else {
            System.out.println("DEBUG (CozinhaLegalScraper): Selector 'h1.entry-title' did NOT find title.");
            return "Título não encontrado";
        }
    }

    @Override
    protected String extrairIngredientes(Document doc) {
        StringBuilder ingredientes = new StringBuilder();
        // Select all <li> elements with class 'wprm-recipe-ingredient' within the ingredients container
        Elements ingredientesElements = doc.select(".wprm-recipe-ingredients-container .wprm-recipe-ingredient");
        System.out.println("DEBUG (CozinhaLegalScraper): Selector '.wprm-recipe-ingredients-container .wprm-recipe-ingredient' found " + ingredientesElements.size() + " ingredient elements.");

        for (Element ingredientElement : ingredientesElements) {
            // Extract amount, unit, and name spans and concatenate them
            String amount = ingredientElement.selectFirst(".wprm-recipe-ingredient-amount") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-amount").text().trim() : "";
            String unit = ingredientElement.selectFirst(".wprm-recipe-ingredient-unit") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-unit").text().trim() : "";
            String name = ingredientElement.selectFirst(".wprm-recipe-ingredient-name") != null ? ingredientElement.selectFirst(".wprm-recipe-ingredient-name").text().trim() : "";

            // Append with spaces, and a newline for each ingredient
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
        // Select all <li> elements with class 'wprm-recipe-instruction' within the instructions container
        Elements instrucaoElements = doc.select(".wprm-recipe-instructions-container .wprm-recipe-instruction");
        System.out.println("DEBUG (CozinhaLegalScraper): Selector '.wprm-recipe-instructions-container .wprm-recipe-instruction' found " + instrucaoElements.size() + " instruction elements.");

        int stepNum = 1;
        for (Element instructionElement : instrucaoElements) {
            // The actual instruction text is inside <div class="wprm-recipe-instruction-text">
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