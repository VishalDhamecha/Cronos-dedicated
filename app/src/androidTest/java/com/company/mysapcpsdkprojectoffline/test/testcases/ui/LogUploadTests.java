package com.company.mysapcpsdkprojectoffline.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import static androidx.test.InstrumentationRegistry.getInstrumentation;

import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication;
import com.company.mysapcpsdkprojectoffline.app.WelcomeActivity;
import com.company.mysapcpsdkprojectoffline.test.core.BaseTest;
import com.company.mysapcpsdkprojectoffline.test.core.Credentials;
import com.company.mysapcpsdkprojectoffline.test.core.Utils;
import com.company.mysapcpsdkprojectoffline.test.core.WizardDevice;
import com.company.mysapcpsdkprojectoffline.test.pages.DetailPage;
import com.company.mysapcpsdkprojectoffline.test.pages.EntityListPage;
import com.company.mysapcpsdkprojectoffline.test.pages.MasterPage;
import com.company.mysapcpsdkprojectoffline.test.pages.PasscodePage;
import com.company.mysapcpsdkprojectoffline.test.pages.SettingsListPage;
import com.company.mysapcpsdkprojectoffline.test.core.ClientPolicyManager;

import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUploadTests extends BaseTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> activityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void testLogUpload() {
        // This test just tests whether the buttons works as expected
        // no crash and the toast appears or not
        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();

        masterPage = new MasterPage();
        masterPage.clickBack();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        SettingsListPage settingsListPage = new SettingsListPage();
        setUpLogs();
        settingsListPage.clickUploadLog();
        settingsListPage.checkLogUploadToast();
    }


    @Test
    public void testLogUploadBackgroundLocked() {

        Utils.doOnboarding(activityTestRule.getActivity());

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();

        masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

        // Put the application into background and wait until the app is locked
        int lockTimeOut = ClientPolicyManager.getInstance().getPasscodeLockTimeout();
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        // Reopen app
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        // Logupload flow
        entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        masterPage = new MasterPage();
        masterPage.clickBack();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        SettingsListPage settingsListPage = new SettingsListPage();
        setUpLogs();
        settingsListPage.clickUploadLog();
        settingsListPage.checkLogUploadToast();
    }

    private void setUpLogs() {
        Logger LOGGER = LoggerFactory.getLogger(WelcomeActivity.class);
        LOGGER.error("first error message");
        LOGGER.error("second error message");
        LOGGER.error("third error message");
    }


}
