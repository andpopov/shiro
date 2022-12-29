package com.github.andpopov.shiro.editor;

import junit.framework.Assert;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EditorTest extends BaseTest {
    @BeforeAll
    static void init() {
        loadIniFactory();
        userLogin("user1", "password");
    }

    @Test
    void userAuthenticated() {
        Assert.assertTrue(user().isAuthenticated());
    }

    @Test
    void userHasArticleCreatePermission() {
        Assert.assertTrue(user().isPermitted("article:create"));
    }

    @Test
    void userHasEditorRole() {
        Assert.assertTrue(user().hasRole("editor"));
    }
}
