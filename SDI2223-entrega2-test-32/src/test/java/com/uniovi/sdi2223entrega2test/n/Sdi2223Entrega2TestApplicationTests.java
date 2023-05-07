package com.uniovi.sdi2223entrega2test.n;

import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_HomeView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_PrivateView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_Properties;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_View;
import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.bson.Document;
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
    public void PR16(){
        // Iniciamos sesión como usuario estandar
        PO_PrivateView.login(driver, "user01@email.com", "user01");

        //Pinchamos en la opción de menú de ofertas: //li[contains(@id, 'offers-menu')]/a
        PO_PrivateView.checkViewAndClick(driver, "free", "//li[contains(@class, 'nav-item dropdown')]/a", 0);
        //Esperamos a que aparezca la opción de añadir oferta: //a[contains(@href, 'offer/add')]
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(@href, 'offer/add')]", 0);

        // Rellenamos el formulario de alta de oferta con datos validos
        PO_PrivateView.fillFormAddOffer(driver, "PruebaTitulo", "PruebaDescripcion", "0.21");

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
     *  en negativo) y pulsar el botón Submit
     */
    @Test
    @Order(17)
    public void PR17(){
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

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario

        PO_PrivateView.makeSearch(driver, "Prueba37");

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

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.makeSearch(driver, "Prueba37");

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

        // Comprobamos que la oferta recien añadida sale en la lista de ofertas propias
        // del usuario
        PO_PrivateView.makeSearch(driver, "Prueba37");

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
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
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
     * PR47. Marcar como leído un mensaje de ID conocido. Esta prueba consistirá en comprobar que
     * el mensaje marcado de ID conocido queda marcado correctamente a true como leído. Por lo
     * tanto, se tendrá primero que invocar al servicio de identificación (S1), solicitar el servicio de
     * marcado (S7), comprobando que el mensaje marcado ha quedado marcado a true como leído (S4)
     * Realizado por: Álvaro
     */
    @Test
    @Order(47)
    public void PR47() {
        final String RestAssuredURL = "http://localhost:8081/api";
        //Llamamos al servicio de login
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user05@email.com");
        requestParams.put("password", "user05");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        // Hacemos la petición
        Response response = request.post(RestAssuredURL+"/users/login");
        String token = response.jsonPath().getString("token");
        // Llamamos al servicio de mensajes
        RequestSpecification request2 = RestAssured.given();
        request2.header("Content-Type", "application/json");
        request2.header("token",token );
        // Hacemos la petición para marcar el mensaje como leído
        Document message=m.getMessage("user07@email.com", "user05@email.com","645692d93a07e85fc87fefa6");
        String id= message.getObjectId("_id").toString();
        Response response2 = request2.put(RestAssuredURL+"/messages/"+id);
        // Guardamos todas las ofertas
        String result = response2.jsonPath().getString("message");
        // Comprobamos que se muestran todas las ofertas
        Assertions.assertEquals("Mensaje modificado correctamente.",result);
        Assertions.assertEquals(200, response2.getStatusCode());
        message=m.getMessage("user07@email.com", "user05@email.com","645692d93a07e85fc87fefa6");
        Assertions.assertEquals(true, message.getBoolean("read"));
    }

    /**
     * PR57.  Identificarse en la aplicación y enviar un mensaje a una oferta, validar que el mensaje
     * enviado aparece en el chat. Identificarse después con el usuario propietario de la oferta y validar
     * que tiene un mensaje sin leer, entrar en el chat y comprobar que el mensaje pasa a tener el estado
     * leído.
     * Realizada por: Álvaro
     */
    @Test
    @Order(57)
    public void PR57() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.loginAPI(driver, "user01@email.com", "user01");

        //Creamos la conversación
        PO_PrivateView.checkViewAndClick(driver, "free", "//*[@id=\"createConversation\"]", 0);

        //Añadimos un mensaje con texto:Hola
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"text-message\"]");
        elements.get(0).click();
        elements.get(0).sendKeys("Hola");
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"button-addon2\"]");
        elements.get(0).click();

        //Comprobamos que aparece el mensaje por pantalla
        elements=PO_View.checkElementBy(driver, "text", "Hola");
        Assertions.assertEquals("Hola",elements.get(0).getText());


        //Logeamos con otro usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//*[@id=\"login\"]",0);
        SeleniumUtils.waitLoadElementsBy(driver,"text","Email:",3000);
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.loginAPI(driver, "user02@email.com", "user02");

        //Vamos al menu de conversaciones
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(text(), 'Conversaciones')]", 0);

        //Comprobamos que hay un mensaje sin leer
        elements=PO_View.checkElementBy(driver, "text", "1");
        Assertions.assertEquals("1",elements.get(2).getText());

        //Entramos a la conversación
        PO_PrivateView.checkViewAndClick(driver, "free", "//*[@id=\"openConversation\"]",0);


        //Comprobamos que aparece el mensaje por pantalla
        elements=PO_View.checkElementBy(driver, "text", "Hola");
        Assertions.assertEquals("Hola",elements.get(0).getText());

        //Comprobamos que el mensaje se ha marcado como leído
        Document message=m.getMessageFromUser("user01@email.com", "user01@email.com");
        Assertions.assertEquals(true,message.getBoolean("read"));


    }

    /**
     * PR58.  Identificarse en la aplicación y enviar tres mensajes a una oferta, validar que los mensajes
     * enviados aparecen en el chat. Identificarse después con el usuario propietario de la oferta y validar
     * que el número de mensajes sin leer aparece en su oferta.
     * Realizada por: Álvaro
     */
    @Test
    @Order(58)
    public void PR58() {
        driver.navigate().to("http://localhost:8081/apiclient/client.html?w=login");
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.loginAPI(driver, "user01@email.com", "user01");

        //Creamos la conversación
        PO_PrivateView.checkViewAndClick(driver, "free", "//*[@id=\"createConversation\"]", 0);

        //Añadimos un mensaje con texto:Hola
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"text-message\"]");
        elements.get(0).click();
        elements.get(0).sendKeys("Hola");
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"button-addon2\"]");
        elements.get(0).click();

        PO_View.checkElementBy(driver, "text", "Hola");


        //Añadimos un mensaje con texto:que
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"text-message\"]");
        elements.get(0).click();
        elements.get(0).sendKeys("que");
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"button-addon2\"]");
        elements.get(0).click();

        PO_View.checkElementBy(driver, "text", "que");

        //Añadimos un mensaje con texto:tal
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"text-message\"]");
        elements.get(0).click();
        elements.get(0).sendKeys("tal");
        elements = PO_View.checkElementBy(driver, "free", "//*[@id=\"button-addon2\"]");
        elements.get(0).click();

        PO_View.checkElementBy(driver, "text", "tal");

        //Logeamos con otro usuario
        PO_PrivateView.checkViewAndClick(driver, "free", "//*[@id=\"login\"]",0);
        SeleniumUtils.waitLoadElementsBy(driver,"text","Email:",3000);
        //Iniciamos sesión como usuario estandar
        PO_PrivateView.loginAPI(driver, "user02@email.com", "user02");

        //Vamos al menu de conversaciones
        PO_PrivateView.checkViewAndClick(driver, "free", "//a[contains(text(), 'Conversaciones')]", 0);

        //Comprobamos que hay 3 mensajes sin leer
        elements=PO_View.checkElementBy(driver, "text", "3");

        Assertions.assertEquals("3",elements.get(0).getText());
    }



}
