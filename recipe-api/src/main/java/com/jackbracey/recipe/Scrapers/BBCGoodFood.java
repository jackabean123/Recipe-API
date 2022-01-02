package com.jackbracey.recipe.Scrapers;

import com.google.common.base.Strings;
import com.jackbracey.recipe.POJOs.Ingredient;
import com.jackbracey.recipe.POJOs.Recipe;
import com.jackbracey.recipe.POJOs.RecipeDataSources;
import com.jackbracey.recipe.Service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BBCGoodFood extends Scraper {

    private RecipeService recipeService;
    private final List<String> extraRecipesForCrawling = new ArrayList<>();

    public BBCGoodFood(List<String> scrapedUrls, RecipeService recipeService) {
        super("https://www.bbcgoodfood.com", scrapedUrls);
        this.recipeService = recipeService;
    }

    @Override
    public void run() {
        List<String> pages = GetMainPages();
        List<String> recipeLinks = GetDetailPages(pages);
        List<Recipe> recipes = GetRecipeDetails(recipeLinks);

        List<com.jackbracey.recipe.Domain.Recipe> domainRecipes = recipes
                .stream()
                .map(recipe -> recipe.convertToRepoObject(RecipeDataSources.BBCGoodFood))
                .collect(Collectors.toList());

        for (com.jackbracey.recipe.Domain.Recipe recipe : domainRecipes)
            recipeService.createRecipe(recipe);
    }

    private List<Recipe> GetRecipeDetails(List<String> recipeLinks) {
        List<Recipe> recipes = new ArrayList<>();

        for (String url : recipeLinks) {
            Document html = GetPageByUrl(url);
            Recipe recipe = new Recipe();
            recipe.setUrl(url);
            if (html.selectXpath("//h1[@class=\"heading-1\"]").size() > 0)
                recipe.setName(html.selectXpath("//h1[@class=\"heading-1\"]").get(0).text());
            if (html.selectXpath("//ul[@class=\"post-header__row post-header__planning list list--horizontal\"]//div[contains(text(),\"Serves\")]").size() > 0)
                recipe.setServes(Integer.parseInt(
                        html.selectXpath("//ul[@class=\"post-header__row post-header__planning list list--horizontal\"]//div[contains(text(),\"Serves\")]")
                                .get(0).text().substring(7)));
            if (html.selectXpath("//ul[@class=\"post-header__row post-header__planning list list--horizontal\"]//time").size() > 2)
                recipe.setTotalTime(GetCookTime(html));
            if (html.selectXpath("//div[@class=\"editor-content\"]//p").size() > 0)
                recipe.setDescription(html.selectXpath("//div[@class=\"editor-content\"]//p").get(0).text());
            if (html.selectXpath("//section[@class=\"recipe__ingredients col-12 mt-md col-lg-6\"]//section//ul//li").size() > 0)
                recipe.setIngredients(GetIngredients(html));
//            if (html.selectXpath("").size() > 0)
//                recipe.setSteps(html.selectXpath("").get(0).text());
        }

        return recipes;
    }

    private List<Ingredient> GetIngredients(Document html) {
        Elements elements = html.selectXpath("//section[@class=\"recipe__ingredients col-12 mt-md col-lg-6\"]//section//ul//li");
        if (elements.size() > 0) {
            List<Ingredient> ingredients = new ArrayList<>();

            for (Element element : elements) {

            }

            return ingredients;
        }
        return null;
    }

    private Integer GetCookTime(Document html) {
        Elements times = html.selectXpath("//ul[@class=\"post-header__row post-header__planning list list--horizontal\"]//time");
        if (times.size() > 2) {
            String prepTimeInitialString = times.get(0).text();
            String cookTimeInitialString = times.get(1).text();

            Integer prep = Integer.parseInt(prepTimeInitialString.substring(prepTimeInitialString.indexOf("Prep:") + 5, prepTimeInitialString.indexOf(" mins")));
            Integer cook = Integer.parseInt(cookTimeInitialString.substring(cookTimeInitialString.indexOf("Cook:") + 6, cookTimeInitialString.indexOf(" mins")));

            return prep + cook;
        }
        return 0;
    }

    public List<String> GetMainPages() {
        List<String> pages = new ArrayList<>();
        Document document = GetPage();

        Elements elements = document.selectXpath("//div[@class=\"main-nav__sub-menu main-nav__mega-menu main-nav__mega-menu--cols-4\"]//div[@class=\"main-nav__sub-menu\"]//a[@class=\"link main-nav__nav-link\"]");
        for (Element element : elements) {
            String text = element.text();
            String href = GetHref(element);
            if (!text.equalsIgnoreCase("See more") && !text.equalsIgnoreCase("Back to Recipes"))
                pages.add(href);
        }
        return pages;
    }

    public List<String> GetDetailPages(List<String> pages) {
        List<String> detailPages = new ArrayList<>();
        for (String page : pages) {
            int i = 1;
            while (true) {
                log.info("Scraping " + baseUrl + page + "?page=" + i);
                Document document = GetPage(page, i);
                Elements elements = document.selectXpath("//div[@class=\"post__content\"]//li//article//div[@class=\"card__section card__content\"]//a");
                for (Element element : elements) {
                    String href = String.format("%s%s", baseUrl, GetHref(element));
                    if (!scrapedUrls.contains(href))
                        detailPages.add(href);
                    else
                        log.info(href + " has already been scraped, ignoring.");
                }
                if (document.selectXpath("//div[text()=\"Load more\"]").size() > 0)
                    i++;
                else if (document.selectXpath("//div[@class=\"dynamic-list dynamic-list--separated\"]//*[text()[contains(.,'See more')]]").size() > 0) {
                    extraRecipesForCrawling.add(GetHref(document.selectXpath("//div[@class=\"dynamic-list dynamic-list--separated\"]//*[text()[contains(.,'See more')]]/..").get(0)));
                    break;
                } else
                    break;
            }
        }
        if (extraRecipesForCrawling.size() > 0)
            detailPages.addAll(CrawlExtraRecipes());
        return detailPages;
    }

    private List<String> CrawlExtraRecipes() {
        List<String> detailPages = new ArrayList<>();

        for (String url : extraRecipesForCrawling) {
            int pageNumber = 1;
            Integer totalPages = null;
            while (true) {
                String nextPageUrl = totalPages == null ? "" : GetUrlWithPage(url, pageNumber);
                log.info("Scraping " + String.format("%s%s", baseUrl, Strings.isNullOrEmpty(nextPageUrl) ? url : nextPageUrl));
                Document html = GetPageByUrl(String.format("%s%s", baseUrl, Strings.isNullOrEmpty(nextPageUrl) ? url : nextPageUrl));

                for (Element element : html.selectXpath("//a[@class=\"img-container img-container--square-thumbnail\"]")) {
                    String href = GetHref(element);
                    if (!scrapedUrls.contains(href))
                        detailPages.add(href);
                    else
                        log.info(href + " has already been scraped, ignoring.");
                }

                if (totalPages == null)
                    totalPages = GetTotalPages(html.html());

                if (pageNumber == totalPages)
                    break;
                else
                    pageNumber++;
            }
        }

        return detailPages;
    }

    private String GetUrlWithPage(String url, int pageNumber) {
        String before = url.substring(0, url.lastIndexOf("/"));
        String after = url.substring(url.lastIndexOf("/") + 1);
        return String.format("%s/page/%d/%s", before, pageNumber, after);
    }

    private String GetPaginationString(String html) {
        String sub1 = html.substring(html.indexOf("pagination") + 13);
        return sub1.substring(0, sub1.indexOf("}"));
    }

    private Integer GetPaginationValue(String html, String targetValue) {
        String paginationString = GetPaginationString(html);
        String sub1 = paginationString.substring(paginationString.indexOf(targetValue) + (targetValue.length() + 2));
        String sub2 = sub1.substring(0, sub1.indexOf("\"")-1);
        return Integer.parseInt(sub2);
    }

    private Integer GetTotalPages(String html) {
        Integer total = GetPaginationValue(html, "total");
        Integer limit = GetPaginationValue(html, "limit");

        return (int) Math.ceil((double) total / limit);
    }

}
