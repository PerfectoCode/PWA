package io.perfecto.pwa.tests;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.*;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import com.perfecto.utilities.others.PerfectoLabUtils;
import com.perfecto.utilities.uiObjects.UIObjectsUtilities;
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class PWATest {

	AppiumDriver driver;
	ReportiumClient reportiumClient;

	String device;
	String osname;
	public TouchAction _touchAction;

	@Parameters({ "deviceName", "OS" })
	@BeforeTest
	public void setUp(String deviceName, String OS) throws Exception {
		String browserName = null;
		String host = "demo.perfectomobile.com";
		String securityToken = "";
		osname = OS;
		// driver for launching Web
		if (OS.equalsIgnoreCase("ios")) {
			DesiredCapabilities capabilitiesMobile = new DesiredCapabilities(browserName, "", Platform.ANY);
			browserName = "mobileOS";
			capabilitiesMobile.setCapability("securityToken", securityToken);
			capabilitiesMobile.setCapability("deviceName", deviceName);
			// capabilitiesMobile.setCapability("bundleId", "com.mobily.mobilyapp");
			PerfectoLabUtils.setExecutionIdCapability(capabilitiesMobile, host);

			driver = new IOSDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub/fast"),
					capabilitiesMobile);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		} else {
			DesiredCapabilities capabilitiesMobile = new DesiredCapabilities(browserName, "", Platform.ANY);
			browserName = "mobileOS";
			capabilitiesMobile.setCapability("securityToken", securityToken);
			capabilitiesMobile.setCapability("deviceName", deviceName);
			capabilitiesMobile.setCapability("appPackage", "com.mobily.activity");
			//capabilitiesMobile.setCapability("unicodeKeyboard", "true");
			// capabilitiesMobile.setCapability("baseAppiumBehavior", true);
			capabilitiesMobile.setCapability("enableAppiumBehavior", true);
			PerfectoLabUtils.setExecutionIdCapability(capabilitiesMobile, host);

			driver = new AndroidDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub/fast"),
					capabilitiesMobile);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}

		String JOB_NAME = System.getProperty("reportium-job-name", "PWA Demo");
		String JOB_NUMBER = System.getProperty("reportium-job-number", "1");

		// Reporting client. For more details, see
		// http://developers.perfectomobile.com/display/PD/Reporting
		PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
				.withProject(new Project("PWA demo", "1.0")).withJob(new Job(JOB_NAME, Integer.parseInt(JOB_NUMBER)))
				.withContextTags("PWA").withWebDriver(driver).build();
		reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);

	}

	@Test
	public void test() throws InterruptedException {
		try {
			// Launch URL
			reportiumClient.testStart("PWA", new TestContext("PWA", "iOS"));

			reportiumClient.stepStart("Launch Site and Add PWA to home screen");
			driver.get("https://www.trivago.com");

			driver.context("NATIVE_APP");
			driver.findElementByXPath("//*[@label=\"Share\"]").click();

			Thread.sleep(2000);

			Map<String, Object> params = new HashMap<>();
			params.put("start", "70%,90%");
			params.put("end", "70%,20%");
			params.put("duration", "2");
			Object res = driver.executeScript("mobile:touch:swipe", params);

			driver.context("NATIVE_APP");
			driver.findElementByXPath("//*[@label=\"Add to Home Screen\"]").click();

			Thread.sleep(2000);
			Map<String, Object> params16 = new HashMap<>();
			params16.put("content", "Add");
			params16.put("index", "2");
			Object result16 = driver.executeScript("mobile:text:select", params16);

			Thread.sleep(3000);
//
//			HashMap<String, String> pwaParams = new HashMap<String, String>();
//			pwaParams.put("displayName", "trivago");
//			driver.executeScript("mobile:pwa:start", pwaParams);

			reportiumClient.stepStart("Launch PWA App");
			driver.context("NATIVE_APP");
			int scrolls = 5;
			do {
				if (!driver.findElementByXPath("//XCUIElementTypeIcon[contains(@label,'trivago')]").isDisplayed()) {
					--scrolls;
					Map<String, Object> params3 = new HashMap<>();
					params3.put("start", "80%,80%");
					params3.put("end", "20%,80%");
					params3.put("duration", "1");
					driver.executeScript("mobile:touch:swipe", params3);
				} else
					break;
			} while (scrolls > 0);

			WebElement shortcutIcon = driver.findElementByXPath("//XCUIElementTypeIcon[contains(@label,'trivago')]");
			shortcutIcon.click();
			
			reportiumClient.stepStart("Perform some operations");
			driver.context("WEBVIEW");

			try {
				driver.findElementByXPath("//*[@class=\"dealform-clear-button js-query-clear\"]").click();
			} catch (Exception NoSuchElementException) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			driver.findElementByXPath("//*[@id=\"querytext\"]").clear();
			driver.findElementByXPath("//*[@id=\"querytext\"]").sendKeys("Bengaluru");
			driver.findElementByXPath("//*[@id=\"suggestion-64975/200\"]//*[@class=\"is-emphasized\"]").click();

			driver.findElementByXPath("(//span[@class='cal-day-value' and text()='29'])[1]").click();
			driver.findElementByXPath("(//span[@class='cal-day-value' and text()='30'])[1]").click();
			driver.findElementByXPath(
					"//*[@class=\"icon-ic icon-contain siteheader__nav-icon siteheader__nav-icon--login\"]").click();
			driver.findElementByXPath("//*[@id=\"check_email\"]").sendKeys("test2@test.com");
			driver.findElementByXPath("//*[text()=\"Next\"]").click();

			driver.context("NATIVE_APP");
			driver.findElementByXPath("//*[@label=\"Done\"]").click();

			driver.context("WEBVIEW");
			
			driver.findElementByXPath("//*[@class=\"siteheader__nav-list\"]/li[2]/button[1]").click();
			
			
			driver.findElementByXPath("//*[@class=\"fs-menu__text\" and text()=\"Payment methods\"]").click();

			reportiumClient.stepStart("Stop PWA");
			driver.executeScript("mobile:pwa:stop");

			Thread.sleep(3000);

			// go to Home screen
			params = new HashMap<>();
			driver.executeScript("mobile:handset:ready", params);

			reportiumClient.stepStart("Remove PWA from device");
			// scroll until icon of interest is displayed
			driver.context("NATIVE_APP");
			 scrolls = 5;
			do {
				if (!driver.findElementByXPath("//XCUIElementTypeIcon[contains(@label,'trivago')]").isDisplayed()) {
					--scrolls;
					Map<String, Object> params3 = new HashMap<>();
					params3.put("start", "80%,80%");
					params3.put("end", "20%,80%");
					params3.put("duration", "1");
					driver.executeScript("mobile:touch:swipe", params3);
				} else
					break;
			} while (scrolls > 0);

			 shortcutIcon = driver.findElementByXPath("//XCUIElementTypeIcon[contains(@label,'trivago')]");

			Rectangle iconRect = shortcutIcon.getRect();

			// get center point of the icon
			int touchX = iconRect.getX() + (iconRect.getWidth() / 2);
			int touchY = iconRect.getY() + (iconRect.getHeight() / 2);

			// for iOS devices: multiply coordinates by 2 for smaller screens (like iPhone
			// 11), and by 3 for larger screens (like iPhone 11 Pro)
			touchX *= 2;
			touchY *= 2;

			// long press on the icon to display delete option
			Map<String, Object> params4 = new HashMap<>();
			params4.put("location", touchX + "," + touchY);
			params4.put("duration", "5");
			driver.executeScript("mobile:touch:tap", params4);

			// delete ..
			shortcutIcon.findElement(By.xpath("//*[@label=\"trivago\"]//*[@name=\"DeleteButton\"]")).click();
			driver.findElementByXPath("//*[@label='Delete']").click();

			driver.executeScript("mobile:handset:ready", params);

			reportiumClient.testStop(TestResultFactory.createSuccess());
		} catch (Exception e) {
			// driver.executeScript("mobile:pwa:stop");
			reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
			e.printStackTrace();
		}
	}

	@AfterTest
	public void tearDown() throws Exception {

		// Close driver
		// driver.closeApp();

		// Quit Instance
		driver.quit();

	}

	public void swipeOnScreen(Point fromPoint, Point toPoint) {
		// TouchAction touchAction;
		WaitOptions waitOptions = new WaitOptions().withDuration(Duration.ofMillis(300));
		touchAction().press(new PointOption().withCoordinates(fromPoint)).waitAction(waitOptions)
				.moveTo(new PointOption().withCoordinates(toPoint)).waitAction().release().perform();
	}

	private TouchAction touchAction() {
		if (_touchAction == null) {
			_touchAction = new TouchAction(driver);
		}
		return _touchAction;
	}
}
