package com.uniovi.sdi2223entrega2test.n;

import com.uniovi.sdi2223entrega2test.n.pageobjects.*;
import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    //static String Geckodriver = "C:\\Path\\geckodriver-v0.30.0-win64.exe";
    static String Geckodriver = "geckodriver-v0.30.0-win64.exe";
    static String URL = "http://localhost:8081";    //static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
    //static String Geckodriver = "/Users/USUARIO/selenium/geckodriver-v0.30.0-macos";
    //Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    private MongoDB m;

    private String BASE_API_URL ="http://localhost:8081/apiclient";


    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
//Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
        try {
            m = new MongoDB();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        m.resetMongo();
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    @Test
    @Order(1)
    void PR01() {
        Assertions.assertTrue(true, "PR01 sin hacer");
    }

    @Test
    @Order(2)
    public void PR02() {
        Assertions.assertTrue(false, "PR02 sin hacer");
    }

    @Test
    @Order(3)
    public void PR03() {
        Assertions.assertTrue(false, "PR03 sin hacer");
    }

    @Test
    @Order(4)
    public void PR04() {
        Assertions.assertTrue(false, "PR04 sin hacer");
    }

    @Test
    @Order(5)
    public void PR05() {
        Assertions.assertTrue(false, "PR05 sin hacer");
    }

    @Test
    @Order(6)
    public void PR06() {
        Assertions.assertTrue(false, "PR06 sin hacer");
    }

    @Test
    @Order(7)
    public void PR07() {
        Assertions.assertTrue(false, "PR07 sin hacer");
    }

    @Test
    @Order(8)
    public void PR08() {
        Assertions.assertTrue(false, "PR08 sin hacer");
    }

    @Test
    @Order(9)
    public void PR09() {
        Assertions.assertTrue(false, "PR09 sin hacer");
    }

    @Test
    @Order(10)
    public void PR10() {
        Assertions.assertTrue(false, "PR10 sin hacer");
    }

    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(11)
    public void PR11() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }

    /**
     * PR16.Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
     */
    @Test
    @Order(16)
    public void PR16() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos validos
        PO_PrivateView.fillFormAddOffer(driver, "PruebaTitulo", "PruebaDescripcion", "0.21");
        // Vamos a la tercera pagina de la lista de ofertas propias del usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 2);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.checkElement(driver, "PruebaTitulo");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion");
        PO_PrivateView.checkElement(driver, "0.21 EUR");

        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR17. Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío y precio
     * en negativo) y pulsar el botón Submit
     */
    @Test
    @Order(17)
    public void PR17() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos inválidos(titulo vacío y precio en negativo)
        PO_PrivateView.fillFormAddOffer(driver, "", "PruebaDescripcion", "-0.21");

        // Comprobamos que se muestran los dos menasjes de error
        PO_PrivateView.checkElement(driver, "Titulo de la oferta no puede estar vacío o tener menos de 3 caracteres");
        PO_PrivateView.checkElement(driver, "Precio de la oferta no puede ser negativo");

        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR18. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
     * existen para este usuario (10 ofertas).
     */
    @Test
    @Order(18)
    public void PR18() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user04@email.com", "user04");

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta propias: //a[contains(@href, 'offer/ownedList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/ownedList')]", 0);

        //Guardamos el número de ofertas de la primera página
        List<WebElement> offerList = SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout());
        //Vamos a la segunda página
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 1);
        offerList.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout()));
        // Comprobamos que se encuentren todas las ofertas
        Assertions.assertEquals(10, offerList.size());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR19.  Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(19)
    public void PR19() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user04@email.com", "user04");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta propias: //a[contains(@href, 'offer/ownedList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/ownedList')]", 0);
        //Borramos la primera oferta de la pagina
        PO_PrivateView.checkViewAndClick(driver, "free",
                "//h4[contains(text(), 'Oferta 41')]/following-sibling::*/a[contains(@href, 'offer/delete')]", 0);
        //Comprobamos que ha desaparecido la oferta '41
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "41", PO_View.getTimeout());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR20. Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(20)
    public void PR20() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user04@email.com", "user04");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta propias: //a[contains(@href, 'offer/ownedList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/ownedList')]", 0);
        //Vamos a la última pagina
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 1);
        //Borramos la última oferta de la pagina
        PO_PrivateView.checkViewAndClick(driver, "free",
                "//h4[contains(text(), 'OFERTA 49')]/following-sibling::*/a[contains(@href, 'offer/delete')]", 0);
        //Comprobamos que ha desaparecido la oferta '49'
        SeleniumUtils.waitTextIsNotPresentOnPage(driver, "OFERTA 49", PO_View.getTimeout());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR21.  Ir a la lista de ofertas, borrar una oferta de otro usuario, comprobar que la oferta no se
     * borra. Id de la oferta de user02@email.com con titulo 117
     */
    @Test
    @Order(21)
    public void PR21() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user04@email.com", "user04");
        // Navegamos para borrar una oferta que no es nuestra oferta (Id oferta de user02@email.com, titulo 117)
        driver.navigate().to("http://localhost:8081/offer/delete/645684b8c6cb6031f07f3af2");
        // Comprobamos que se muestra el mensaje de error
        PO_PrivateView.checkElement(driver, "La oferta no existe o no se encuentra disponible");
    }

    /**
     * PR22. Ir a la lista de ofertas, borrar una oferta propia que ha sido vendida, comprobar que la
     * oferta no se borra.
     */
    @Test
    @Order(22)
    public void PR22() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user04@email.com", "user04");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta propias: //a[contains(@href, 'offer/ownedList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/ownedList')]", 0);
        //Borramos la oferta '44' que ya ha sido vendida
        PO_PrivateView.checkViewAndClick(driver, "free",
                "//h4[contains(text(), 'Oferta 44')]/following-sibling::*/a[contains(@href, 'offer/delete')]", 0);
        // Comprobamos que se muestran los dos menasjes de error
        PO_PrivateView.checkElement(driver, "La oferta no existe o no se encuentra disponible");
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR23.  Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
     * corresponde con el listado de las ofertas existentes en el sistema.
     */
    @Test
    @Order(23)
    public void PR23() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user10@email.com", "user10");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Dejamos el campo de busqueda vacio y buscamos
        PO_PrivateView.makeSearch(driver, "");
        //Guardamos los elemento de la primera pagina
        List<WebElement> offerList = SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout());
        //Vamos a la segunda página
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 1);
        offerList.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout()));
        //Vamos a la tercera página
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 2);
        offerList.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout()));
        //Recorremos las paginas guardando las ofertas
        for (int i = 0; i < 26; i++) {
            PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 3);
            offerList.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free",
                    "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout()));
        }
        // Comprobamos que se encuentren todas las ofertas
        Assertions.assertEquals(143, offerList.size());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR24. Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
     * muestra la página que corresponde, con la lista de ofertas vacía
     */
    @Test
    @Order(24)
    public void PR24() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user10@email.com", "user10");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Dejamos el campo de busqueda vacio y buscamos
        PO_PrivateView.makeSearch(driver, "SistemasDistribuidos");
        //Guardamos los elemento de la primera pagina
        List<WebElement> offerList = driver.findElements(By.xpath("//div[contains(@class, 'card border-dark mb-3')]"));
        // Comprobamos que se encuentren todas las ofertas
        Assertions.assertEquals(0, offerList.size());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR25.  Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
     * comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho
     * texto, independientemente que el título esté almacenado en minúsculas o mayúscula
     */
    @Test
    @Order(25)
    public void PR25() {
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user10@email.com", "user10");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Buscamos el texto 'oferta 4' en el campo de busqueda
        PO_PrivateView.makeSearch(driver, "oferta 4");
        //Guardamos los elemento de la primera pagina
        List<WebElement> offerList = SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout());
        //Vamos a la segunda página
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 1);
        offerList.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout()));
        // Comprobamos que se encuentren todas las ofertas
        Assertions.assertEquals(10, offerList.size());
        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /**
     * PR26. Sobre una búsqueda determinada (a elección del desarrollador),
     * comprar una oferta que deja un saldo positivo en el contador del comprador.
     * Comprobar que el contador se actualiza correctamente en la vista del comprador.
     * Realizada por: Álvaro
     */
    @Test
    @Order(26)
    public void PR26() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Hacemos una busqueda
        PO_PrivateView.makeSearch(driver, "117");
        //Compramos la oferta
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Comprar')]");
        elements.get(0).click();
        //Lo comparamos con el precio restado
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        double result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 95.0);

        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    /**
     * PR27. Sobre una búsqueda determinada (a elección del desarrollador),
     * comprar una oferta que deja un saldo 0 en el contador del comprador.
     * Comprobar que el contador se actualiza correctamente en la vista del comprador.
     * Realizada por: Álvaro
     */
    @Test
    @Order(27)
    public void PR27() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Hacemos una busqueda
        PO_PrivateView.makeSearch(driver, "118");
        //Compramos la oferta
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Comprar')]");
        elements.get(0).click();
        //Lo comparamos con el precio restado
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        double result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 0.00);

        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    /**
     * PR28. Sobre una búsqueda determinada (a elección del desarrollador),
     * intentar comprar una oferta que esté por encima de saldo disponible del comprador.
     * Y comprobar que se muestra el mensaje de saldo no suficiente
     * Realizada por: Álvaro
     */
    @Test
    @Order(28)
    public void PR28() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Hacemos una busqueda
        PO_PrivateView.makeSearch(driver, "119");
        //Compramos la oferta
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Comprar')]");
        elements.get(0).click();
        //Se muestra el mensaje de error
        elements = PO_View.checkElementBy(driver, "free", "//td[contains(text(), 'El saldo es insuficiente')]");
        Assertions.assertEquals("El saldo es insuficiente", elements.get(0).getText());
        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    /**
     * PR29. Ir a la opción de ofertas compradas del usuario y mostrar la lista.
     * Comprobar que aparecen las ofertas que deben aparecer
     * Realizada por: Álvaro
     */
    @Test
    @Order(29)
    public void PR29() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");
        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de mostrar oferta: //a[contains(@href, 'offer/searchList')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/searchList')]", 0);
        //Hacemos una busqueda
        PO_PrivateView.makeSearch(driver, "117");
        //Compramos la oferta
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Comprar')]");
        elements.get(0).click();
        //Vamos a la pestaña de ofertas compradas
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de ofertas compradas:
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/purchasedList')]", 0);
        List<WebElement> offerList = driver.findElements(By.xpath("//div[contains(@class, 'card border-dark mb-3')]"));
        // Comprobamos que se encuentren todas las ofertas
        Assertions.assertEquals(1, offerList.size());
        //Cierro sesion
        PO_PrivateView.logout(driver);
    }


    /**
     * PR30. Al crear una oferta, marcar dicha oferta como destacada y a continuación comprobar: i)
     * que aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se
     * actualiza adecuadamente en la vista del ofertante
     * Realizada por: Álvaro
     */
    @Test
    @Order(30)
    public void PR30() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");

        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        double result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 100.0);

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos validos
        PO_PrivateView.fillFormAddOfferFeatured(driver, "Prueba37", "PruebaDescripcion37", "0.37");

        // Vamos a la tercera pagina de la lista de ofertas propias del usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 2);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.checkElement(driver, "Prueba37");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion37");
        PO_PrivateView.checkElement(driver, "0.37 EUR");

        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'home')]", 0);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas destacadas
        // del usuario
        PO_PrivateView.checkElement(driver, "Prueba37");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion37");
        PO_PrivateView.checkElement(driver, "0.37 EUR");

        //Lo comparamos con el precio restado
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 80.00);

        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    /**
     * PR31. Sobre el listado de ofertas de un usuario con más de 20 euros de saldo, pinchar en el enlace
     * Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas para los
     * usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante
     * Realizada por: Álvaro
     */
    @Test
    @Order(31)
    public void PR31() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");

        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        double result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 100.0);

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos validos
        PO_PrivateView.fillFormAddOffer(driver, "Prueba37", "PruebaDescripcion37", "0.37");

        // Vamos a la tercera pagina de la lista de ofertas propias del usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 2);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.checkElement(driver, "Prueba37");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion37");
        PO_PrivateView.checkElement(driver, "0.37 EUR");

        //Pulsamos el botón de destacar
        elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Destacar')]");
        elements.get(0).click();

        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'home')]", 0);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas destacadas
        // del usuario
        PO_PrivateView.checkElement(driver, "Prueba37");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion37");
        PO_PrivateView.checkElement(driver, "0.37 EUR");

        //Lo comparamos con el precio restado
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"amount\"]");
        result = Double.parseDouble(elements.get(0).getText().split(" ")[0]);
        Assertions.assertEquals(result, 80.00);

        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    /**
     * PR32.  Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
     * enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente
     * Realizada por: Álvaro
     */
    @Test
    @Order(32)
    public void PR32() {
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user03@email.com", "user03");

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos validos
        PO_PrivateView.fillFormAddOffer(driver, "Prueba37", "PruebaDescripcion37", "0.37");

        // Vamos a la tercera pagina de la lista de ofertas propias del usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@class, 'page-link')]", 2);

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.checkElement(driver, "Prueba37");
        PO_PrivateView.checkElement(driver, "PruebaDescripcion37");
        PO_PrivateView.checkElement(driver, "0.37 EUR");

        //Pulsamos el botón de destacar
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//button[contains(text(), 'Destacar')]");
        elements.get(0).click();

        //Se muestra el mensaje de error
        elements = PO_View.checkElementBy(driver, "free", "//td[contains(text(), 'El saldo es insuficiente')]");
        Assertions.assertEquals("El saldo es insuficiente", elements.get(0).getText());

        //Cierro sesion
        PO_PrivateView.logout(driver);
    }

    @Test
    @Order(38)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8081/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "user01");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }

    /**
     * PR41. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
     * existen para este usuario.
     */
    @Test
    @Order(41)
    public void PR41() {
        final String RestAssuredURL = "http://localhost:8081/api";
        //Llamamos al servicio de login
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "user01");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        // Hacemos la petición
        Response response = request.post(RestAssuredURL+"/users/login");
        String token = response.jsonPath().getString("token");
        // Llamamos al servicio de ofertas
        RequestSpecification request2 = RestAssured.given();
        request2.header("Content-Type", "application/json");
        request2.header("token",token );
        // Hacemos la petición para obtener el listado
        Response response2 = request2.get(RestAssuredURL+"/offers");
        // Guardamos todas las ofertas
        List<Object> offers = response2.jsonPath().getList("offers");
        // Comprobamos que se muestran todas las ofertas
        Assertions.assertEquals(143, offers.size());
        Assertions.assertEquals(200, response2.getStatusCode());
    }

    /**
     * PR51.  Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
     * menos las del usuario identificado.
     */
    @Test
    @Order(51)
    public void PR51() {
        // Accedemos a la página de login
        driver.get(BASE_API_URL+"/client.html?w=login");
        // Rellenamos el formulario de login
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user01");
        //Guardamos todas las ofertas disponibles
        List<WebElement> offerList = SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout());
        // Comprobamos que se muestran todas las ofertas disponibles
        Assertions.assertTrue(offerList.size() > 0);
    }

}
