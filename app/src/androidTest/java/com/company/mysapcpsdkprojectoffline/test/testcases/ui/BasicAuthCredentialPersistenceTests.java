package com.company.mysapcpsdkprojectoffline.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication;
import com.company.mysapcpsdkprojectoffline.app.WelcomeActivity;
import com.company.mysapcpsdkprojectoffline.test.core.BaseTest;
import com.company.mysapcpsdkprojectoffline.test.core.Constants;
import com.company.mysapcpsdkprojectoffline.test.core.Utils;
import com.company.mysapcpsdkprojectoffline.test.core.factory.LoginPageFactory;
import com.company.mysapcpsdkprojectoffline.app.WizardFlowStateListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static com.company.mysapcpsdkprojectoffline.test.core.Constants.APPLICATION_AUTH_TYPE;
import com.company.mysapcpsdkprojectoffline.test.pages.WelcomePage;
import com.sap.cloud.mobile.flowv2.core.Flow;
import com.sap.cloud.mobile.flowv2.core.FlowContext;
import com.sap.cloud.mobile.flowv2.core.FlowContextBuilder;
import com.sap.cloud.mobile.flowv2.model.FlowType;

@RunWith(AndroidJUnit4.class)
public class BasicAuthCredentialPersistenceTests extends BaseTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> activityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void basicAuthCredentialsGetReused() throws InterruptedException {
        if (APPLICATION_AUTH_TYPE != Constants.AuthType.BASIC) {
            return;
        }
        Utils.doOnboarding(activityTestRule.getActivity());
        // Clear session cookies so the server will give a basic auth challenge
        Utils.clearSessionCookies();
    }

    @Test
    public void basicAuthCredentialsGetCleared() throws InterruptedException {
        if (APPLICATION_AUTH_TYPE != Constants.AuthType.BASIC) {
            return;
        }
        Utils.doOnboarding(activityTestRule.getActivity());

        // Clear session cookies so the server will give a basic auth challenge
        Utils.clearSessionCookies();

        // Clear basic auth credentials as well
        SAPWizardApplication application = (SAPWizardApplication) activityTestRule.getActivity().getApplication();
        FlowContext flowContext = new FlowContextBuilder()
                .setApplication(application.getAppConfig())
                .setFlowType(FlowType.RESET)
                .setFlowStateListener(new WizardFlowStateListener(application))
                .build();
        Flow.start(activityTestRule.getActivity(), flowContext);
        WelcomePage welcomePage = new WelcomePage();
        welcomePage.clickGetStarted();
        LoginPageFactory.getLoginPage().authenticate();
    }
}
