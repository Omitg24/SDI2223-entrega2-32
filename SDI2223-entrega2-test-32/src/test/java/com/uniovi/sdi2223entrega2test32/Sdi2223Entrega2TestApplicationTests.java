package com.uniovi.sdi2223entrega2test32;

import com.uniovi.sdi2223entrega2test32.pageobjects.*;
import com.uniovi.sdi2223entrega2test32.util.SeleniumUtils;
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
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "geckodriver-v0.30.0-win64.exe";
    static String URL = "http://localhost:8081";
    private MongoDB m;
    private String BASE_API_URL = "http://localhost:8081/apiclient";    static WebDriver driver = getDriver(PathFirefox, Geckodriver);

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
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        m.resetMongo();
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    /**
     * PR01. Registro de Usuario con datos válidos.
     * Realizada por: Omar
     */
    @Test
    @Order(1)
    public void PR01() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "uo123456@uniovi.es", "Adrián", "García Fernández", "123456", "123456", "2000-01-01");
        //Comprobamos que entramos en la sección privada y nos muestra el texto a buscar
        String checkText = "Listado Ofertas Propias";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * PR02. Registro de Usuario con datos inválidos (email, nombre, apellidos y fecha de nacimiento vacíos).
     * Realizada por: Omar
     */
    @Test
    @Order(2)
    public void PR02() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "", "", "", "123456", "123456", "");
        //Comprobamos los errores
        String emailCheck = "El email no puede ser vacío.";
        String nameCheck = "El nombre no puede ser vacío.";
        String lastNameCheck = "El apellido no puede ser vacío.";
        String dateCheck = "La fecha de nacimiento no puede ser vacía.";
        List<WebElement> emailResult = PO_View.checkElementBy(driver, "text", emailCheck);
        List<WebElement> nameResult = PO_View.checkElementBy(driver, "text", nameCheck);
        List<WebElement> lastNameResult = PO_View.checkElementBy(driver, "text", lastNameCheck);
        List<WebElement> dateResult = PO_View.checkElementBy(driver, "text", dateCheck);
        Assertions.assertEquals(emailCheck, emailResult.get(0).getText());
        Assertions.assertEquals(nameCheck, nameResult.get(0).getText());
        Assertions.assertEquals(lastNameCheck, lastNameResult.get(0).getText());
        Assertions.assertEquals(dateCheck, dateResult.get(0).getText());
    }

    /**
     * PR03. Registro de Usuario con datos inválidos (repetición de contraseña inválida).
     * Realizada por: Omar
     */
    @Test
    @Order(3)
    public void PR03() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "uo123456@uniovi.es", "Adrián", "García Fernández", "123456", "654321", "2000-01-01");
        //Comprobamos los errores
        String checkText = "Las contraseñas deben coincidir.";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    // 1. Público: Registrarse como usuario

    /**
     * PR04. Registro de Usuario con datos inválidos (email existente).
     * Realizada por: Omar
     */
    @Test
    @Order(4)
    public void PR04() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "user01@email.com", "Adrián", "García Fernández", "123456", "123456", "2000-01-01");
        //Comprobamos los errores
        String checkText = "Este email ya pertenece a otro usuario, user01@email.com.";
        List<WebElement> result = PO_SignUpView.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * PR05. Inicio de sesión con datos válidos (administrador).
     * Realizada por: Omar
     */
    @Test
    @Order(5)
    public void PR05() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "admin");
        //Comprobamos que entramos en la sección de listar usuarios
        String checkText = "Listado de usuarios";
        WebElement title = driver.findElement(By.xpath("//h2[contains(text(), 'Listado de usuarios')]"));
        Assertions.assertEquals(checkText, title.getText());
    }

    /**
     * PR06. Inicio de sesión con datos válidos (usuario estándar).
     * Realizada por: Omar
     */
    @Test
    @Order(6)
    public void PR06() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user01");
        //Comprobamos que entramos en la sección de listado de ofertas propias
        String checkText = "Listado Ofertas Propias";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    // 2. Usuario Registrado: Iniciar sesión

    /**
     * PR07. Inicio de sesión con datos inválidos (usuario estándar, email existente, pero contraseña
     * incorrecta).
     * Realizada por: Omar
     */
    @Test
    @Order(7)
    public void PR07() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user");
        //Comprobamos los errores
        String checkText = "Email o password incorrecto";
        List<WebElement> result = PO_SignUpView.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * PR08. Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacíos).
     * Realizada por: Omar
     */
    @Test
    @Order(8)
    public void PR08() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "", "");
        //Comprobamos los errores
        String emailCheck = "El email no puede ser vacío.";
        String passwordCheck = "La contraseña no puede ser vacía.";
        List<WebElement> emailResult = PO_View.checkElementBy(driver, "text", emailCheck);
        List<WebElement> passwordResult = PO_View.checkElementBy(driver, "text", passwordCheck);
        Assertions.assertEquals(emailCheck, emailResult.get(0).getText());
        Assertions.assertEquals(passwordCheck, passwordResult.get(0).getText());
    }

    /**
     * PR09. Hacer clic en la opción de salir de sesión y comprobar que se redirige a la página de inicio de
     * sesión (Login).
     * Realizada por: Omar
     */
    @Test
    @Order(9)
    public void PR09() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "admin");
        //Nos desconectamos
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-dark");
        //Comprobamos que hemos cerrado la sesión
        String checkText = "Identificación de usuario";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * PR10. Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
     * Realizada por: Omar
     */
    @Test
    @Order(10)
    public void PR10() {
        //Comprobamos que el elemento no está
        List<WebElement> result = driver.findElements(By.xpath("//a[contains(@href, '/logout')]"));
        Assertions.assertTrue(result.isEmpty());
    }

    /**
     * PR11. Mostrar el listado de usuarios y comprobar que se muestran todos los que existen en el
     * sistema.
     * Realizada por: Omar
     */
    @Test
    @Order(11)
    public void PR11() {
        //Vamos al formulario de logueo
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-dark");
        //Rellenamos el formulario.
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "admin");
        //Comprobamos que entramos en la sección privada y nos nuestra el texto a buscar
        PO_View.checkElementBy(driver, "free", "//a[contains(@class, 'page-link')]");
        List<WebElement> elements;
        //Añadimos los elementos en la primera página
        List<WebElement> result = new ArrayList<>(SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr", PO_View.getTimeout()));

        //Vamos a la segunda pagina y añadimos los elementos
        elements = PO_View.checkElementBy(driver, "free", "//a[contains(@class, 'page-link')]");
        elements.get(1).click();
        result.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr", PO_View.getTimeout()));

        //Vamos a la tercera pagina y añadimos los elementos
        elements = PO_View.checkElementBy(driver, "free", "//a[contains(@class, 'page-link')]");
        elements.get(2).click();
        result.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr", PO_View.getTimeout()));

        //Vamos a la cuarta pagina y añadimos los elementos
        elements = PO_View.checkElementBy(driver, "free", "//a[contains(@class, 'page-link')]");
        elements.get(3).click();
        result.addAll(SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr", PO_View.getTimeout()));

        Assertions.assertEquals(m.getUsers(), result.size());
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

        // Rellenamos el formulario de alta de oferta con datos inválidos(titulo vacío y precio en negativo)
        PO_PrivateView.fillFormAddOffer(driver, "", "PruebaDescripcion", "-0.21");

        // Comprobamos que se muestran los dos menasjes de error
        PO_PrivateView.checkElement(driver, "Titulo de la oferta no puede estar vacío o tener menos de 3 caracteres");
        PO_PrivateView.checkElement(driver, "Precio de la oferta no puede ser negativo");

        // Hacemos logout
        PO_PrivateView.logout(driver);
    }

    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
//        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
//        Response response = RestAssured.get(RestAssuredURL);
//        Assertions.assertEquals(403, response.getStatusCode());

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
        Assertions.assertEquals(m.getUsersOffersCount("user04@email.com"), offerList.size());
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
        Assertions.assertEquals(m.getTotalOffersCount() - m.getUsersOffersCount("user10@email.com"),
                offerList.size());
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
        Assertions.assertEquals(m.getOffersByTitleCount("oferta 4"), offerList.size());
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

    /**
     * PR38. Inicio de sesión con datos válidos.
     * Realizada por: Omar
     */
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
     * PR39. Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
     * Realizada por: Omar
     */
    @Test
    @Order(39)
    public void PR39() {
        final String RestAssuredURL = "http://localhost:8081/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "user");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(401, response.getStatusCode());
    }

    /**
     * PR40. Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
     * Realizada por: Omar
     */
    @Test
    @Order(40)
    public void PR40() {
        final String RestAssuredURL = "http://localhost:8081/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "");
        requestParams.put("password", "");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(403, response.getStatusCode());
    }

    /**
     * PR41. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
     * existen para este usuario.
     * Realizada por: David
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
        Response response = request.post(RestAssuredURL + "/users/login");
        String token = response.jsonPath().getString("token");
        // Llamamos al servicio de ofertas
        RequestSpecification request2 = RestAssured.given();
        request2.header("Content-Type", "application/json");
        request2.header("token", token);
        // Hacemos la petición para obtener el listado
        Response response2 = request2.get(RestAssuredURL + "/offers");
        // Guardamos todas las ofertas
        List<Object> offers = response2.jsonPath().getList("offers");
        // Comprobamos que se muestran todas las ofertas
        Assertions.assertEquals(m.getTotalOffersCount() - m.getUsersOffersCount("user01@email.com"),
                offers.size());
        Assertions.assertEquals(200, response2.getStatusCode());
    }

    /**
     * PR48. Inicio de sesión con datos válidos.
     * Realizada por: Omar
     */
    @Test
    @Order(48)
    public void PR48() {
        // Accedemos a la página de login
        driver.get(BASE_API_URL + "/client.html?w=login");
        // Rellenamos el formulario de login
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user01");

        // Comprobamos el redireccionamiento a la lista de ofertas
        Assertions.assertEquals(BASE_API_URL + "/client.html?w=offer", driver.getCurrentUrl());
    }

    /**
     * PR49. Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
     * Realizada por: Omar
     */
    @Test
    @Order(49)
    public void PR49() {
        // Accedemos a la página de login
        driver.get(BASE_API_URL + "/client.html?w=login");
        // Rellenamos el formulario de login
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user");
        // Comprobamos que se muestra el error
        String checkText = "Inicio de sesión incorrecto";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * PR50. Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
     * Realizada por: Omar
     */
    @Test
    @Order(50)
    public void PR50() {
        // Accedemos a la página de login
        driver.get(BASE_API_URL + "/client.html?w=login");
        // Rellenamos el formulario de login
        PO_LoginView.fillLoginForm(driver, "", "");
        // Comprobamos que se muestra el error
        String emailCheck = "El email no puede ser vacío";
        String passwordCheck = "La contraseña no puede ser vacía";
        WebElement email = driver.findElement(By.xpath("//div[@id='div-errors']/ul/li[contains(text(), 'El email no puede ser vacío')]"));
        WebElement password = driver.findElement(By.xpath("//div[@id='div-errors']/ul/li[contains(text(), 'La contraseña no puede ser vacía')]"));
        Assertions.assertEquals(emailCheck, email.getText());
        Assertions.assertEquals(passwordCheck, password.getText());
    }

    /**
     * PR51.  Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
     * menos las del usuario identificado.
     * Realizada por: David
     */
    @Test
    @Order(51)
    public void PR51() {
        // Accedemos a la página de login
        driver.get(BASE_API_URL + "/client.html?w=login");
        // Rellenamos el formulario de login
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user01");
        //Guardamos todas las ofertas disponibles
        List<WebElement> offerList = SeleniumUtils.waitLoadElementsBy(driver, "free",
                "//div[contains(@class, 'card border-dark mb-3')]", PO_View.getTimeout());
        // Comprobamos que se muestran todas las ofertas disponibles
        Assertions.assertEquals(m.getTotalOffersCount() - m.getUsersOffersCount("user01@email.com"),
                offerList.size());
    }




}
