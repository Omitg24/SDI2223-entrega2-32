package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class PO_PrivateView extends PO_NavView {

    /**
     * Método para realizar el login de un usuario
     *
     * @param driver   driver
     * @param email      dni del usuario
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
        String loginText = PO_HomeView.getP().getString("msg.signup", PO_Properties.getSPANISH());
        PO_PrivateView.clickOption(driver, "logout", "text", loginText);
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
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"searchTextForm\"]/div/div/input");
        elements.get(0).click();
        elements.get(0).sendKeys(searchText);
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"searchTextForm\"]/div/button");
        elements.get(0).click();
    }


}
