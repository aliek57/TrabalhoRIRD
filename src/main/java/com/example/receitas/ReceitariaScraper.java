package com.example.receitas;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ReceitariaScraper extends WebScrapper {

    @Override
    protected List<String> extrairLinksReceitas(Document doc) {
        List<String> receitaUrls = new ArrayList<>();
        Elements links = doc.select(".item.hover-zoom a[href*=/receita/]");
        for (Element link : links) {
            String receitaUrl = link.attr("abs:href");
            if (receitaUrl.contains("/receita/")) {
                receitaUrls.add(receitaUrl);
            }
        }
        return receitaUrls;
    }

    @Override
    protected String extrairTitulo(Document doc) {
        Element tituloElement = doc.selectFirst(".title h1");
        return tituloElement != null ? tituloElement.text() : "Título não encontrado";
    }

    @Override
    protected String extrairIngredientes(Document doc) {
        String jsonLD = extrairJsonLD(doc);
        if (jsonLD != null) {
            return extrairIngredientesDoJsonLD(jsonLD);
        }
        return "Ingredientes não encontrados";
    }

    @Override
    protected String extrairModoPreparo(Document doc) {
        String jsonLD = extrairJsonLD(doc);
        if (jsonLD != null) {
            return extrairModoPreparoDoJsonLD(jsonLD);
        }
        return "Modo de preparo não encontrado";
    }
}