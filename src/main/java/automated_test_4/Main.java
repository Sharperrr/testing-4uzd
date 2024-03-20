package automated_test_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {
	public static void main(String[] args) throws InterruptedException
	{
		System.setProperty("webdriver.edge.driver", "C:\\Users\\simas\\msedgedriver.exe");
		EdgeDriver driver = new EdgeDriver();
		WebDriverWait wait = new WebDriverWait(driver, 3);
		
		driver.manage().window().maximize();
		CreateUser(driver, wait);
		
		for(int t = 1; t < 3; ++t)
		{
			driver = new EdgeDriver();
			wait = new WebDriverWait(driver, 5);
			driver.manage().window().maximize();
			Testing(driver, wait, t);
		}
		
		//driver = new EdgeDriver();
		//wait = new WebDriverWait(driver, 5);
		//driver.manage().window().maximize();
		//Testing(driver, wait, 2);
		
	}
	
	static void CreateUser(EdgeDriver driver, WebDriverWait wait)
	{
		Wait<EdgeDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(org.openqa.selenium.TimeoutException.class);
		
		driver.get("https://demowebshop.tricentis.com/");
		
		driver.findElement(By.xpath("//a[text()='Log in']")).click();
		driver.findElement(By.xpath("//input[@value='Register']")).click();
		
		driver.findElement(By.xpath("//input[@id='gender-male']")).click();
		driver.findElement(By.xpath("//input[@id='FirstName']")).sendKeys("John");
		driver.findElement(By.xpath("//input[@id='LastName']")).sendKeys("Doe");
		driver.findElement(By.xpath("//input[@id='Password']")).sendKeys("123456");
		driver.findElement(By.xpath("//input[@id='ConfirmPassword']")).sendKeys("123456");

		String filePath = "user.txt";
		//int id = getUserID(filePath);
		fluentWait.until((EdgeDriver edgeDriver) -> {
			int id = getUserID(filePath);
			String dummyEmail = "automatedtestingdummyemail" + id + "@gmail.com";
			iterateUserID(filePath, ++id);
			//System.out.println(dummyEmail);
			WebElement emailField = driver.findElement(By.xpath("//input[@id='Email']"));
			emailField.clear();
			emailField.sendKeys(dummyEmail);
			driver.findElement(By.xpath("//input[@id='register-button']")).click();
			try {
				//WebElement email = driver.findElement(By.xpath("//li[text()='The specified email already exists']"));
	            WebElement email = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[text()='The specified email already exists']")));

	            //System.out.println("Element found!");

	            return false;
	        } catch (Exception e) {
	            //System.out.println("Element not found!");
	            return true;
	        }

		});
		
		driver.findElement(By.xpath("//input[@value='Continue']")).click();
		driver.close();
		
	}
	
	static void Testing(EdgeDriver driver, WebDriverWait wait, int dataId)
	{
		Wait<EdgeDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(1000))
                .ignoring(org.openqa.selenium.TimeoutException.class);
		
		driver.get("https://demowebshop.tricentis.com/");
		
		driver.findElement(By.xpath("//a[text()='Log in']")).click();
		
		String filePath = "user.txt";
		int id = getUserID(filePath);
		String email = "automatedtestingdummyemail" + --id + "@gmail.com";
		String password = "123456";

		driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(email);
		driver.findElement(By.xpath("//input[@id='Password']")).sendKeys(password);
		
		driver.findElement(By.xpath("//div[@class='buttons']/input[@value='Log in']")).click();
		
		driver.findElement(By.xpath("//div[@class='master-wrapper-main']//a[@href='/digital-downloads']")).click();
		
		String dataFilePath = "data" + dataId + ".txt";
		try (BufferedReader reader = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
            	++i;
            	int j = i;
            	String line1 = line;
            	fluentWait.until((EdgeDriver edgeDriver) -> {
            		driver.findElement(By.xpath("//a[text()='"+ line1 +"']/../..//input[@value='Add to cart']")).click();
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		String expectedQty = '(' + String.valueOf(j) + ')';
            		
            		if(driver.findElement(By.xpath("//a[@class='ico-cart']/span[@class='cart-qty']")).getText().equals(expectedQty))
            		{
            			return true;
            		}
            		else
            		{
            			return false;
            		}
            	});
            	
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		driver.findElement(By.xpath("//a[@href='/cart']")).click();
		driver.findElement(By.xpath("//input[@id='termsofservice']")).click();
		driver.findElement(By.xpath("//button[@id='checkout']")).click();
		
		if(dataId == 1)
		{
			driver.findElement(By.xpath("//select[@id='BillingNewAddress_CountryId']")).click();
			driver.findElement(By.xpath("//option[@value='156']")).click();
			driver.findElement(By.xpath("//input[@id='BillingNewAddress_City']")).sendKeys("Vilnius");
			driver.findElement(By.xpath("//input[@id='BillingNewAddress_Address1']")).sendKeys("Didlaukio g. 47");
			driver.findElement(By.xpath("//input[@id='BillingNewAddress_ZipPostalCode']")).sendKeys("LT-08303");
			driver.findElement(By.xpath("//input[@id='BillingNewAddress_PhoneNumber']")).sendKeys("+37063564966");
		}
		
		driver.findElement(By.xpath("//input[@onclick='Billing.save()']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@onclick='PaymentMethod.save()']")));
		driver.findElement(By.xpath("//input[@onclick='PaymentMethod.save()']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@onclick='PaymentInfo.save()']")));
		driver.findElement(By.xpath("//input[@onclick='PaymentInfo.save()']")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@onclick='ConfirmOrder.save()']")));
		driver.findElement(By.xpath("//input[@onclick='ConfirmOrder.save()']")).click();
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Click here for order details.']")));
		driver.findElement(By.xpath("//a[text()='Click here for order details.']")).click();
		
		driver.close();
	}
	
	static int getUserID(String filePath)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = reader.readLine()) != null) {
            	int id = Integer.parseInt(line);
                return id;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return 0;
	}
	
	static void iterateUserID(String filePath, int newId)
	{
		try (BufferedWriter clearWriter = new BufferedWriter(new FileWriter(filePath, false))) {
            clearWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.valueOf(newId));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
