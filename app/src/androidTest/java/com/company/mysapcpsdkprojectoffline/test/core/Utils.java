package com.company.mysapcpsdkprojectoffline.test.core;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Looper;
import android.os.SystemClock;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.uiautomator.UiDevice;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication;
import com.company.mysapcpsdkprojectoffline.test.core.ClientPolicy;
import com.company.mysapcpsdkprojectoffline.test.core.ClientPolicyManager;
import com.company.mysapcpsdkprojectoffline.test.core.factory.LoginPageFactory;
import com.company.mysapcpsdkprojectoffline.test.core.factory.PasscodePageFactory;
import com.company.mysapcpsdkprojectoffline.test.pages.ActivationPage;
import com.company.mysapcpsdkprojectoffline.test.pages.EulaScreenPage;
import com.company.mysapcpsdkprojectoffline.test.pages.PasscodePage;
import com.company.mysapcpsdkprojectoffline.test.pages.WelcomePage;
import com.company.mysapcpsdkprojectoffline.test.pages.OfflineScreen;

import org.hamcrest.Matcher;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import android.app.Activity;


public class Utils {
    public static void doOnboarding(Activity context) {
        checkCredentials();
       WelcomePage welcomePage = new WelcomePage();
       welcomePage.clickGetStarted();

        SystemClock.sleep(2000);
        AbstractLoginPage loginPage = LoginPageFactory.getLoginPage();
        loginPage.authenticate();
        PasscodePageFactory.PasscodeFlow();
        
        
        EulaScreenPage eulaScreenPage = new EulaScreenPage();
        if(Constants.EULASCREEN == Constants.EulaScreen.DENY) {
            eulaScreenPage.clickDeny();
        } else {
            eulaScreenPage.clickAllow();
        }

        OfflineScreen offlineScreen = new OfflineScreen();
        offlineScreen.waitUntilPageIsOver();
        offlineScreen.leavePage();
    }

    public static void doOnboardingBack(Activity context) {
        checkCredentials();
           WelcomePage welcomePage = new WelcomePage();
           welcomePage.clickGetStarted();
        SystemClock.sleep(2000);
        AbstractLoginPage loginPage = LoginPageFactory.getLoginPage();
        loginPage.authenticate();
        PasscodePageFactory.PasscodeFlowBack();

        EulaScreenPage eulaScreenPage = new EulaScreenPage();
        if(Constants.EULASCREEN == Constants.EulaScreen.ALLOW){
            eulaScreenPage.clickAllow();
        }else{
            eulaScreenPage.clickDeny();
        }


        OfflineScreen offlineScreen = new OfflineScreen();
        offlineScreen.waitUntilPageIsOver();
        offlineScreen.leavePage();
    }

    public static void clearSessionCookies() throws InterruptedException {
        CountDownLatch clearSessionCookiesLatch = new CountDownLatch(1);
        Looper.prepare();
        CookieManager.getInstance().removeSessionCookies(value -> clearSessionCookiesLatch.countDown());
        clearSessionCookiesLatch.await(1000, TimeUnit.MILLISECONDS);
    }

    public static String getStringFromUiWithId(int resourceId) throws InterruptedException {
        final String[] uiString = new String[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        onView(withId(resourceId)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Getting description.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView textView = (TextView) view;
                synchronized (uiString) {
                    uiString[0] = textView.getText().toString();
                }
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        synchronized (uiString) {
            return uiString[0];
        }
    }

    public static String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getString(id);
    }

    public static void skipFingerprint() {
        // Get application context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();

        // Get the current clientpolicy
         ClientPolicy clientPolicy = ClientPolicyManager.getInstance().getClientPolicy();

        boolean isFingerprintAllowed = clientPolicy.getPasscodePolicy().allowsFingerprint();

        //is Device supports Fingerprint
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
            //skip fingerprint
            if (isFingerprintAllowed == true) {
                PasscodePage.SetFingerprintPage setFingerprintPage = new PasscodePage().new SetFingerprintPage();
                setFingerprintPage.skipFingerprint();
                setFingerprintPage.leavePage();
            }
        }
    }


    public static void checkCredentials() {
        Assert.assertThat("Credentials are not defined in the Credentials class!", Credentials.USERNAME.trim(), not(isEmptyString()));
    }

    public static void pressBack() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
        SystemClock.sleep(500);
    }
}
