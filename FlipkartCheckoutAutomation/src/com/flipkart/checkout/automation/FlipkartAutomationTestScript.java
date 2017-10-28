package com.flipkart.checkout.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * This call tests verifies search, login and checkout
 * features of flipkart website.
 */
public class FlipkartAutomationTestScript {
	// Declaring static variable
	static ChromeDriver driver = null;
	static Properties orProperties = null;
	static Properties properties = null;

	/*
	 * This is the main function use to start the webdriver and call the functions
	 * to test various modules of the ecommerce application.
	 * 
	 * @param args arguments to be used in the main function
	 */
	public static void main(String[] args) {

		/*
		 * Set this property to read firefox driver from a given path of your local
		 * drive or import driver directly under created project in eclipse
		 */
		// System.setProperty("webdriver.gecko.driver", "/path/to/firefoxdriver");

		/*
		 * Set this property to read chrome driver from a given path of your local drive
		 * or import driver directly under created project in eclipse
		 */
		// System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

		// Launching Chrome Browser
		driver = new ChromeDriver();

		// Maximize browser window size
		driver.manage().window().maximize();
		addDelay(1);

		// Getting Flipkart URL in browser
		driver.get("https://www.flipkart.com/");
		addDelay(5);

		try {

			// Reading test data from "dataFile.properties" file
			FileReader reader = new FileReader("D:\\ToolsQA\\FlipkartCheckoutAutomation\\src\\com\\flipkart\\checkout\\automation\\dataFile.properties");
			properties = new Properties();
			properties.load(reader);

			// Reading locators from "OR.properties" file
			FileReader orReader = new FileReader("D:\\ToolsQA\\FlipkartCheckoutAutomation\\src\\com\\flipkart\\checkout\\automation\\OR.properties");
			orProperties = new Properties();
			orProperties.load(orReader);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Calling function for passing wrong credentials in login popup and verify
		// unsuccessful login
		attemptLoginWithWrongCredentails();

		// Calling function for passing right credentials in login popup and getting
		// signed in
		attemptLoginWithRightCredentails();

		// Calling function for verification of noSearchResult found functionality
		searchNoResultProduct();

		// Calling function for verification of any search result
		searchProduct();

		// Calling function for searching a valid product and adding it to cart
		addToCart();

		// Calling function for continuing from Cart Page
		continueFromCartPage();

		// Calling function for continuing from Shipping Page
		continueFromShippingPage();

		// Close the webdriver current instance after testing is complete
		driver.close();
	}

	/*
	 * This function is used to login with wrong credentials and verifies that error
	 * message for unsuccessful login.
	 */
	static void attemptLoginWithWrongCredentails() {

		// Checking if login window is already open or not
		boolean isDisplayed = isElementDisplayed(By.className(orProperties.getProperty("login.username.className")));

		// Open the login window, if not already open
		if (!isDisplayed) {
			driver.findElement(By.linkText("Log In")).click();
		}

		// Entering wrong credentials
		driver.findElement(By.className(orProperties.getProperty("login.username.className")))
				.sendKeys(properties.getProperty("wrongUsername"));
		driver.findElement(By.xpath(orProperties.getProperty("login.password.xpath")))
				.sendKeys(properties.getProperty("wrongPassword"));
		driver.findElement(By.xpath(orProperties.getProperty("login.Signup.xpath"))).submit();

		// Check if error message displayed
		String errorMessage = driver.findElement(By.xpath(orProperties.getProperty("login.errormsg.onPopUp.xpath")))
				.getText();
		if (errorMessage.equals("Please enter valid Email ID/Mobile number")) {
			System.out.println("Login error displayed");
		} else {
			System.out.println("Login error not displayed");
		}

		// Clicking on 'X' icon/button to close the login pop up
		addDelay(3);
		driver.findElement(By.xpath(orProperties.getProperty("login.cross.icon.xpath"))).click();
	}

	/*
	 * This function is used to login with right credentials and verifies successful
	 * login
	 */
	static void attemptLoginWithRightCredentails() {

		// Clicking "Login" Link
		driver.findElement(By.linkText("Log In")).click();

		// Entering right credentials
		driver.findElement(By.className(orProperties.getProperty("login.username.className")))
				.sendKeys(properties.getProperty("rightUsername"));
		driver.findElement(By.xpath(orProperties.getProperty("login.password.xpath")))
				.sendKeys(properties.getProperty("rightPassword"));
		addDelay(1);
		driver.findElement(By.xpath(orProperties.getProperty("login.Signup.xpath"))).submit();
		addDelay(2);

		// Checking log in link condition
		boolean isDisplayed = isElementDisplayed(By.linkText("Log In"));

		// Checks if successfully logged in
		if (isDisplayed) {
			System.out.println("Login failed");
		} else {
			System.out.println("Login passed");
		}

	}

	/*
	 * This function searches keyword that does not returns any search result.
	 */
	static void searchNoResultProduct() {

		// search a product that has no result in database
		driver.findElement(By.className(orProperties.getProperty("NSR.enterKeyword.className")))
				.sendKeys(properties.getProperty("noSearchResultProduct"));

		// Click on search icon
		driver.findElement(By.className(orProperties.getProperty("NSR.clickSearchIcon.className"))).submit();
		addDelay(3);

		// Verifies the condition of No search result text
		boolean isErrorMsgDisplayed = isElementDisplayed(
				By.className(orProperties.getProperty("NSR.noSerachResult.Text.className")));

		// Verifies noSearch result
		if (isErrorMsgDisplayed) {
			System.out.println("No search result found");
		} else {
			System.out.println("Test case failed");
		}

		// Clearing search field for next test case
		driver.findElement(By.className(orProperties.getProperty("NSR.enterKeyword.className"))).clear();
	}

	/*
	 * This function searches a keyword that returns some products from database.
	 */
	static void searchProduct() {

		// searching a product that has result in database
		driver.findElement(By.className(orProperties.getProperty("NSR.enterKeyword.className")))
				.sendKeys(properties.getProperty("searchedProduct"));

		// Clicking on search icon
		driver.findElement(By.className(orProperties.getProperty("NSR.clickSearchIcon.className"))).submit();
		addDelay(3);

		// Fetching integer value of result
		String searchResult = driver.findElement(By.xpath(orProperties.getProperty("SR.ResultValue.xpath"))).getText();
		String resultCount = null;
		StringTokenizer strTokenizer = new StringTokenizer(searchResult, "results");
		if (strTokenizer.hasMoreTokens()) {
			String strBeforeResults = strTokenizer.nextToken();
			resultCount = strBeforeResults.substring(strBeforeResults.indexOf("of") + 2, strBeforeResults.length() - 1);
			resultCount = resultCount.trim();
			System.out.println(resultCount);
		}

		// Verifying search result count
		if (resultCount != null && (Integer.valueOf(resultCount) > 0)) {
			System.out.println("Search result found");
		} else {
			System.out.println("Search test case failed");
		}

		// Clearing search field for next test case
		driver.findElement(By.className(orProperties.getProperty("NSR.enterKeyword.className"))).clear();
	}

	/*
	 * This function is used to search a product that has search result in database
	 * and adds it to cart.
	 */
	static void addToCart() {

		// Searching a product
		driver.findElement(By.className(orProperties.getProperty("NSR.enterKeyword.className")))
				.sendKeys(properties.getProperty("AddProduct"));
		driver.findElement(By.className(orProperties.getProperty("NSR.clickSearchIcon.className"))).submit();
		addDelay(3);

		// Filtering by brand Printland
		driver.findElement(By.xpath(orProperties.getProperty("ATC.Brand.select.xpath"))).click();
		addDelay(3);

		// Clicking first product tile
		driver.findElement(By.xpath(orProperties.getProperty("ATC.productTile.click.xpath"))).click();
		addDelay(3);

		// Adding product to cart from product details page
		driver.findElement(By.xpath(orProperties.getProperty("ATC.AddtoCart.click.xpath"))).click();
		addDelay(3);

	}

	/*
	 * This function is used to continue from cart page to shipping page.
	 */
	static void continueFromCartPage() {

		// Clicking the Place Order button on cart page
		driver.findElement(By.xpath(orProperties.getProperty("PlaceOrder.button.click.xpath"))).click();
		addDelay(4);

	}

	/*
	 * This function is used to continue from shipping page to payment page.
	 */
	static void continueFromShippingPage() {

		// Clicking the Continue button on shipping page
		driver.findElement(By.xpath(orProperties.getProperty("PlaceOrder.Continue.button.click.xpath"))).click();
		addDelay(3);

	}

	/*
	 * This function provides delay in execution
	 * 
	 * @param delayTimeInSecs Time to delay the execution in seconds
	 */
	static void addDelay(int delayTimeInSecs) {
		try {
			Thread.sleep(delayTimeInSecs * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This function checks the display of given element.
	 *
	 * @param by By selector to locate an element.
	 */
	static boolean isElementDisplayed(By by) {
		boolean isDisplayed = false;
		try {
			addDelay(2);
			isDisplayed = driver.findElement(by).isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			e.printStackTrace();
			isDisplayed = false;
		}

		return isDisplayed;
	}
}
