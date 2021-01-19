package com.company.mysapcpsdkprojectoffline.test.core.factory;

import androidx.annotation.NonNull;

import com.company.mysapcpsdkprojectoffline.test.core.AbstractLoginPage;
import com.company.mysapcpsdkprojectoffline.test.pages.LoginPage;

import static com.company.mysapcpsdkprojectoffline.test.core.Constants.APPLICATION_AUTH_TYPE;

public class LoginPageFactory {

    @NonNull
    public static AbstractLoginPage getLoginPage() {

        switch (APPLICATION_AUTH_TYPE) {
            case BASIC:
                return new LoginPage.BasicAuthPage();
            case OAUTH:
                return new LoginPage.WebviewPage();
            case SAML:
                return new LoginPage.WebviewPage();
            case NOAUTH:
                return new LoginPage.NoAuthPage();
            default:
                return new LoginPage.NoAuthPage();
        }
    }
}
