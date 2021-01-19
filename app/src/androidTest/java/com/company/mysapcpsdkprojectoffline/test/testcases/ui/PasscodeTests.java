package com.company.mysapcpsdkprojectoffline.test.testcases.ui;

import android.os.SystemClock;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import static androidx.test.InstrumentationRegistry.getInstrumentation;

import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication;
import com.company.mysapcpsdkprojectoffline.app.WelcomeActivity;
import com.company.mysapcpsdkprojectoffline.test.core.ClientPolicyManager;
import com.company.mysapcpsdkprojectoffline.test.core.BaseTest;
import com.company.mysapcpsdkprojectoffline.test.core.Credentials;
import com.company.mysapcpsdkprojectoffline.test.core.UIElements;
import com.company.mysapcpsdkprojectoffline.test.core.Utils;
import com.company.mysapcpsdkprojectoffline.test.core.WizardDevice;
import com.company.mysapcpsdkprojectoffline.test.core.factory.PasscodePageFactory;
import com.company.mysapcpsdkprojectoffline.test.pages.EntityListPage;
import com.company.mysapcpsdkprojectoffline.test.pages.MasterPage;
import com.company.mysapcpsdkprojectoffline.test.pages.PasscodePage;
import com.company.mysapcpsdkprojectoffline.test.pages.SettingsListPage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PasscodeTests extends BaseTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> activityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void testPasscodeLockTimeOut() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);
        // entityListPage.clickFirstElement();
        // entityListPage.leavePage();

        MasterPage masterPage = new MasterPage();

        // Get the lockTimeOut (in seconds) from the SecureStoreManager
        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        // We put the app into background
        WizardDevice.putApplicationBackground(3000, activityTestRule);

        // We reopen the app
        WizardDevice.reopenApplication();
        SystemClock.sleep(1000);

        // Put and reopen the app
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        // We should arrive in the Enter Passcode Page
        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        // Go Back from the Master page
        masterPage.clickBack();
        masterPage.leavePage();

        // We should arrive in the EntityListPage
        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickResetApp();
        settingsListPage.checkConfirmationDialog();
        settingsListPage.leavePage();
    }

    @Test
    public void testManagePasscodeBackground() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);

        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        MasterPage masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickManagePasscode();
        settingsListPage.leavePage();

        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();

        PasscodePageFactory.NewPasscodeFlow();

        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        enterPasscodePage.enterPasscode(Credentials.NEWPASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();
        SystemClock.sleep(2000);
        settingsListPage.clickResetApp();
        settingsListPage.checkConfirmationDialog();
        settingsListPage.clickYes();
    }

    @Test
    public void testManagePasscodeCancelBackground() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);
        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickManagePasscode();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
//        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
////        enterPasscodePage.clickCancel();

        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();

        PasscodePageFactory.NewPasscodeFlow();

        SystemClock.sleep(1000);
        settingsListPage.clickResetApp();
        settingsListPage.checkConfirmationDialog();
        settingsListPage.clickYes();
    }

    @Test
    public void testManagePasscodeDefaultBackground() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);
        // entityListPage.clickFirstElement();

        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        MasterPage masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickManagePasscode();
        settingsListPage.leavePage();

        enterPasscodePage.enterPasscode(Credentials.PASSCODE);

        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        PasscodePageFactory.NewPasscodeFlow();

        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        WizardDevice.reopenApplication();

        enterPasscodePage.enterPasscode(Credentials.NEWPASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();
        SystemClock.sleep(2000);
        settingsListPage.clickResetApp();
        settingsListPage.clickYes();
    }


    @Test
    public void testPasscodeRetryLimitBackground() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        // Get the lockTimeOut (in seconds) from the SecureStoreManager
        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        // We put the app into background
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);

        // We reopen the app
        WizardDevice.reopenApplication();

        // Try the retry limit flow
        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();

        for (int i = 0; i < ClientPolicyManager.getInstance().getClientPolicy().getPasscodePolicy().getRetryLimit(); i++) {
            enterPasscodePage.enterPasscode(Credentials.WRONGPASSCODE);
            enterPasscodePage.clickSignIn();
        }
        enterPasscodePage.clickResetDialogWithText();
    }

    @Test
    public void testSetPasscodeBack() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboardingBack(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);
        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickManagePasscode();
        settingsListPage.leavePage();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();

        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        PasscodePageFactory.NewPasscodeFlowBack();
    }

    @Test
    public void testEnterPasscodeBack() {
        // Take care of welcome screen, authentication, and passcode flow.
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage(UIElements.EntityListScreen.entityList);

        // Get the lockTimeOut (in seconds) from the SecureStoreManager
        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();

        // We put the app into background
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000,activityTestRule);

        // We reopen the app
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        Utils.pressBack();
        enterPasscodePage.leavePage();
        SystemClock.sleep(500);
        // We reopen the app
        WizardDevice.reopenApplication();
        SystemClock.sleep(500);
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        SystemClock.sleep(500);
        enterPasscodePage.leavePage();

        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage(UIElements.SettingsScreen.settingsList);
        settingsListPage.clickResetApp();
        settingsListPage.checkConfirmationDialog();
        settingsListPage.clickYes();
    }
}
