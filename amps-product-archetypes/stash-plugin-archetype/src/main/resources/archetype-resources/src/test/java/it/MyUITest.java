package it;

import static org.junit.Assert.assertTrue;

import ${package}.pageobjects.MyServletPage;
import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.pageobjects.TestedProductFactory;
import com.atlassian.webdriver.stash.StashTestedProduct;
import com.atlassian.webdriver.stash.page.StashHomePage;
import com.atlassian.webdriver.stash.page.StashLoginPage;

public class MyUITest
{
    private final static StashTestedProduct STASH = TestedProductFactory.create(StashTestedProduct.class);

    @BeforeClass
    public static void setUp()
    {
        StashLoginPage loginPage = STASH.visit(StashLoginPage.class);
        loginPage.login("user", "user", StashHomePage.class);
    }

    @Test
    public void testLogin()
    {
        MyServletPage myServlet = STASH.visit(MyServletPage.class);
        assertTrue(myServlet.isWelcome());
    }
}
