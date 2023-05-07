package com.uniovi.sdi2223entrega2test32.pageobjects;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_PrivateView extends PO_NavView {

    /**
     * Método para realizar el login de un usuario
     *
     * @param driver   driver
     * @param email    dni del usuario
     * @param password contraseña del usuario
     */
    static public void login(WebDriver driver, String email, String password) {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        PO_LoginView.fillLoginForm(driver, email, password);
        PO_View.checkElementBy(driver, "text", email);
    }

    /**
     * Método para desconectar a un usuario
     *
     * @param driver driver
     */
    static public void logout(WebDriver driver) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"logout\"]");
        elements.get(0).click();
    }

    /**
     * Método que comprueba que un texto se encuentra en la vista
     *
     * @param driver    driver
     * @param checkText texto a buscar
     */
    static public void checkElement(WebDriver driver, String checkText) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, elements.get(0).getText());
    }

    /**
     * Método que compruba si un elemento se encuentra en la vista y realiza un click
     *
     * @param driver driver
     * @param type   tipo del elemento
     * @param text   Xpath del elemento
     * @param index  indice del objeto que coincide con los datos pasados
     */
    static public void checkViewAndClick(WebDriver driver, String type, String text, int index) {
        List<WebElement> elements = checkElementBy(driver, type, text);
        elements.get(index).click();
    }

    /**
     * Método que rellena el buscador de ofertas con el texto y realiza la busqueda
     *
     * @param driver     driver
     * @param searchText texto a buscar
     */
    static public void makeSearch(WebDriver driver, String searchText) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"search\"]");
        elements.get(0).click();
        elements.get(0).sendKeys(searchText);
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"searchButton\"]");
        elements.get(0).click();
    }

    /**
     * Método para rellenar el formulario de añadir ofertas
     *
     * @param driver       driver
     * @param titlep       titulo de la oferta
     * @param descriptionp descripcion de la oferta
     * @param pricep       precio de la oferta
     */
    static public void fillFormAddOffer(WebDriver driver, String titlep, String descriptionp, String pricep) {
        // Rellenamos el formulario con los datos recibidos como paramteros.
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(titlep);
        WebElement description = driver.findElement(By.name("description"));
        description.click();
        description.clear();
        description.sendKeys(descriptionp);
        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(pricep);

        // Pulsamos el botón para enviar el formulario.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    static public void fillFormAddOfferFeatured(WebDriver driver, String titlep, String descriptionp, String pricep) {
        // Rellenamos el formulario con los datos recibidos como paramteros.
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(titlep);
        WebElement description = driver.findElement(By.name("description"));
        description.click();
        description.clear();
        description.sendKeys(descriptionp);
        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(pricep);
        WebElement featured = driver.findElement(By.name("feature"));
        featured.click();
        // Pulsamos el botón para enviar el formulario.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }


}
