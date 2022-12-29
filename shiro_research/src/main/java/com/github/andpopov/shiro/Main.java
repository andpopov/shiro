package com.github.andpopov.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.Factory;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Load the INI configuration
        final Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject currentUser = SecurityUtils.getSubject();
        if(!currentUser.isAuthenticated()) {
            currentUser.login(new UsernamePasswordToken("user1", "password"));
        }

        print(currentUser.isAuthenticated());
        print(currentUser.isPermitted("article:create"));
        print(currentUser.hasRole("editor"));

//        final IniRealm realm = ((IniRealm) (((DefaultSecurityManager) securityManager).getRealms().iterator().next()));
//        realm.
//            realm.getUser("user1").addRole("reader");
//        print(currentUser.hasRole("reader"));
    }

    public static void print(Object arg) {
        System.out.println(arg);
    }
}
