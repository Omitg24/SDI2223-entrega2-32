package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_HomeView extends PO_NavView {
    static public void checkWelcomeToPage(WebDriver driver, int language) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    static public List<WebElement> getWelcomeMessageText(WebDriver driver, int language) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        return SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    static public void checkChangeLanguage(WebDriver driver, String textLanguage1, String textLanguage,
                                           int locale1, int locale2) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcomeToPage(driver, locale1);
        //Cambiamos a segundo idioma
        changeLanguage(driver, textLanguage);
        //Comprobamos que el texto de bienvenida haya cambiado a segundo idioma
        PO_HomeView.checkWelcomeToPage(driver, locale2);
        //Volvemos a Español.
        changeLanguage(driver, textLanguage1);
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcomeToPage(driver, locale1);
    }

}
