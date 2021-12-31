import requests
from lxml import html
import json

class BBCRecipe:
    def __init__(self,
            url,
            name,
            serves,
            prepTime,
            cookTime,
            description,
            ingredients,
            steps):
        self.url = url
        self.name = name
        self.serves = serves
        self.prepTime = prepTime
        self.cookTime = cookTime
        self.description = description
        self.ingredients = ingredients
        self.steps = steps

    def __init__(self):
        self.url = ""
        self.name = ""
        self.serves = ""
        self.prepTime = 0
        self.cookTime = 0
        self.description = ""
        self.ingredients = []
        self.steps = []

    def toJson(self):
        return {
            "url": self.url,
            "name": self.name,
            "serves": self.serves,
            "prepTime": self.prepTime,
            "cookTime": self.cookTime,
            "description": self.description,
            "ingredients": self.ingredients,
            "steps": self.steps
        }

def GetMainPages(baseUrl):
    pages = []
    masterPage = requests.get(baseUrl)
    tree = html.fromstring(masterPage.content)
    elements = tree.xpath('//div[@class="main-nav__sub-menu main-nav__mega-menu main-nav__mega-menu--cols-4"]//div[@class="main-nav__sub-menu"]//a[@class="link main-nav__nav-link"]')
    for element in elements:
        text = element.text_content()
        value = element.get('href')
        if value is not None and text != "See more" and text != "Back to Recipes":
            pages.append(value)
    return pages

def GetDetailPages(baseUrl, pages):
    recipeLinks = []
    for page in pages:
        pageUrl = ""
        if isinstance(page, str):
            pageUrl = page
        else:
            pageUrl = page.url

        i = 1
        while True:
            try:
                URL = "{}{}?page={}".format(baseUrl, pageUrl, i)
                page = requests.get(URL)
                tree = html.fromstring(page.content)
                elements = tree.xpath('//div[@class="post__content"]//li//article//div[@class="card__section card__content"]//a')
                for element in elements:
                    recipeLinks.append("{}{}".format(BASE_URL, element.get('href')))
                if len(tree.xpath('//div[text()="Load more"]')) == 0:
                    break
                else:
                    i = i + 1
            finally:
                page.close()
    return recipeLinks

def GetTextElements(elements):
    list = []
    for element in elements:
        list.append(element.text_content())
    return list

def GetTextElementsWithXPath(elements):
    list = []
    for element in elements:
        list.append(element.xpath('//div[@class="editor-content"]')[0].text_content())
    return list

def GetRecipeInfo(pages):
    recipes = []

    for page in pages:
        page = requests.get(page)
        tree = html.fromstring(page.content)
        recipe = BBCRecipe()
        if isinstance(page, str):
            recipe.url = page
        else:
            recipe.url = page.url
        if (len(tree.xpath('//h1[@class="heading-1"]')) > 0):
            recipe.name = tree.xpath('//h1[@class="heading-1"]')[0].text_content()
        if (len(tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//div[contains(text(),"Serves")]')) > 0):
            recipe.serves = tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//div[contains(text(),"Serves")]')[0].text_content()
        if (len(tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//time')) > 0):
            recipe.prepTime = tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//time')[0].text_content()
        if (len(tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//time')) > 1):
            recipe.cookTime = tree.xpath('//ul[@class="post-header__row post-header__planning list list--horizontal"]//time')[1].text_content()
        if (tree.xpath('//div[@class="editor-content"]//p') is not None and len(tree.xpath('//div[@class="editor-content"]//p')) > 0):
            recipe.description = tree.xpath('//div[@class="editor-content"]//p')[0].text_content()
        recipe.ingredients = GetTextElements(tree.xpath('//section[@class="recipe__ingredients col-12 mt-md col-lg-6"]//section//ul//li'))
        recipe.steps = GetTextElementsWithXPath(tree.xpath('//div[@class="grouped-list"]//li'))
        recipes.append(recipe)

    return recipes

try:
    BASE_URL = "https://www.bbcgoodfood.com"

    pages = GetMainPages(BASE_URL)
    recipeLinks = GetDetailPages(BASE_URL, pages)
    recipes = GetRecipeInfo(recipeLinks)

    print(json.dumps([r.toJson() for r in recipes]))

except requests.exceptions.RequestException as e:
    print("Error!", e)