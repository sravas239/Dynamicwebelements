package com.test;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.NoSuchElementException;

public class saucedemotest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        
        // Initialize WebDriverWait for explicit waits
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test(priority = 1)
    public void loginTest() {
        // Use CSS Selector to locate username and password fields
        WebElement usernameField = driver.findElement(By.cssSelector("input#user-name"));
        WebElement passwordField = driver.findElement(By.cssSelector("input#password"));
        WebElement loginButton = driver.findElement(By.cssSelector("input#login-button"));

        // Enter credentials and login
        usernameField.sendKeys("standard_user");
        passwordField.sendKeys("secret_sauce");
        loginButton.click();

        // Wait for the products page to load using Explicit Wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".title")));
        System.out.println("Login successful!");
    }

    @Test(priority = 2, dependsOnMethods = "loginTest")
    public void addToCartTest() throws Exception {
        // Use XPath with contains() to locate a dynamic product add-to-cart button
        WebElement addToCartButton = driver.findElement(By.xpath("//button[contains(@id, 'add-to-cart-sauce-labs-backpack')]"));
        addToCartButton.click();

        // Wait for the cart icon badge to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".shopping_cart_badge")));
        System.out.println("Product added to cart!");
        Thread.sleep(2000);
    }
    
    @Test(priority = 3, dependsOnMethods = "addToCartTest")
    public void checkoutTest() throws Exception {
        try {
            // Click on the cart icon
            WebElement cartIcon = driver.findElement(By.cssSelector(".shopping_cart_link"));
            cartIcon.click();

            // Use Fluent Wait to handle dynamic checkout button appearance
            FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofSeconds(2))
                    .ignoring(NoSuchElementException.class);

            WebElement checkoutButton = fluentWait.until(driver -> driver.findElement(By.id("checkout")));
            checkoutButton.click();

            // Verify checkout page loaded with case-insensitive comparison
            WebElement checkoutInfoTitle = driver.findElement(By.cssSelector(".title"));
            Assert.assertTrue(checkoutInfoTitle.getText().equalsIgnoreCase("CHECKOUT: YOUR INFORMATION"), 
                    "Checkout page title mismatch!");

            System.out.println("Navigated to checkout page!");
        } catch (Exception e) {
            // Log the exception and fail the test
            System.err.println("Checkout test failed due to: " + e.getMessage());
            Assert.fail("Checkout test encountered an exception!");
            Thread.sleep(2000);
        }
    }



    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
