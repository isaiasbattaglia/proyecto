package trivia;
import spark.template.mustache.MustacheTemplateEngine;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.After;
import org.junit.Before;
import static junit.framework.TestCase.assertEquals;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;
import static org.junit.Assert.assertEquals;
import com.google.gson.Gson;
import static org.junit.Assert.*;
import spark.utils.IOUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import static spark.Spark.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.javalite.activejdbc.Base;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{

    //Class for simplify tests and connections.
   final static TestUtil testUtil = new TestUtil();

    @Before
    public void setUp() throws Exception {
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
        Base.openTransaction();
        testUtil.deploy();
        awaitInitialization();
    }

    @After
    public void tearDown() throws Exception {
        Base.rollbackTransaction();
        Base.close();
        stop();
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

   /**
   * test Sign Up
   */
    public void testSignUp() throws Exception{
      URL url = new URL("http://localhost:4567/registrar?password=hola&password2=hola&Email=chau@asd.com&nickname=hola");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 200);
    }
   
   /**
   * test Sign Up
   */
    public void testSignUp2() throws Exception{
      URL url = new URL("http://localhost:4567/registrar");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 200);
    }

    /**
     * test registering User, responseCode must be 500 because request not contains params.
     */
    public void testRegisteringUser() throws Exception{
      testUtil.validateData();
      URL url = new URL("http://localhost:4567/verificar");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      assertEquals(connection.getResponseCode(), 500);
    }

    /**
     * test registering User, responseCode must be 200.
     */
    public void testLogout() throws Exception{
      testUtil.logout();
      URL url = new URL("http://localhost:4567/logout");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 200);
    }

    /**
     * test registering User, responseCode must be 200.
     */
    public void testfightLobby() throws Exception{
      testUtil.fightLobby();
      URL url = new URL("http://localhost:4567/fightLobby");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 200);
    }

     /**
     * test Access game's home. responseCode must be 500 becasuse the HTTP Session isn't open.
     */
    public void testAccessToHomeOfGame() throws Exception{
      testUtil.games();
      URL url = new URL("http://localhost:4567/games");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }

     /**
     * test login without params. responseCode must be 500 becasuse, request not contains params.
     */
    public void testLoginWithoutParams() throws Exception{
      testUtil.loginWOutParams();
      URL url = new URL("http://localhost:4567/login");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }

     /**
     * test login with params. responseCode must be 500 becasuse, HTTP Session is not open.
     */
    public void testLoginWithParams() throws Exception{
      testUtil.loginWParams();
      URL url = new URL("http://localhost:4567/login?nickname=test&password=pass");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }

    /**
     * test delete Game, responseCode must be 500 because request not contains params.
     */
    public void testDeleteGame() throws Exception{
      testUtil.deleteGame();
      URL url = new URL("http://localhost:4567/deleteGame");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }
    /**
     * test create Admin
     */
    public void testCreateAdmin() throws Exception{
      testUtil.createAdmin();
      URL url = new URL("http://localhost:4567/createAdmin");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 200);
    }
    /**
     * test profile  responseCode must be 500 becasuse, HTTP Session is not open.
     */
    public void testProfile() throws Exception{
      testUtil.profile();
      URL url = new URL("http://localhost:4567/profile");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }
    /**
     * test finalized game,  responseCode must be 500 becasuse, HTTP Session is not open and request not contains params.
     */
    public void testFinalizedGame() throws Exception{
      testUtil.finalizedGame();
      URL url = new URL("http://localhost:4567/finalizedGame");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      assertEquals(connection.getResponseCode(), 500);
    }
}
