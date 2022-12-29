package com.github.andpopov.shiro.editor;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class BaseTest {
    static final String PACKAGE_DIR = BaseTest.class.getPackageName().replace('.', '/');

    protected static String resource(final String path) {
        return String.format("classpath:%s/%s", PACKAGE_DIR, path);
    }

    static void loadIniFactory() {
        // Load the INI configuration
        final Factory<SecurityManager> factory = new IniSecurityManagerFactory(resource("shiro.ini"));
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    static void userLogin(final String username, final String password) {
        user().login(new UsernamePasswordToken(username, password));
    }

    static Subject user() {
        return SecurityUtils.getSubject();
    }
}
