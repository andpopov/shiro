package com.github.andpopov.shiro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class MyRealm extends AuthorizingRealm {
    private UserDao ud;

    private RoleService rs;

    private PermissionService ps;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<Role> roles = rs.findByUser(user);
        if(roles != null && roles.size() > 0){
            for (Role role : roles) {
                info.addRole(role.getKeyword());
            }
        }
        List<Permission> permissions = ps.findByUser(user);
        if(permissions != null && permissions.size() > 0) {
            for (Permission permission : permissions) {
//                info.addStringPermission(permission.getKeyword());
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken t) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) t;
        String username = token.getUsername();
        User user = ud.findByUsername(username);
        if(user != null){
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), this.getClass().getName());
            //ActionContext.getContext().getSession().put("loginUser", user);
            return info;
        } else {
            return null;
        }
    }
}

class UserDao {
    Map<String, User> users = new HashMap<>();

    public UserDao() {
        this.users.put("user1", new User("user1", "password1"));
    }

    public User findByUsername(final String username) {
        return users.get(username);
    }
}

class RoleService {
    Map<String, List<Role>> userRoles = new HashMap<>();

    public RoleService() {
        userRoles.put("user1", List.of(new Role("editor")));
    }

    public List<Role> findByUser(final User user) {
        return userRoles.get(user.getName());
    }
}

class PermissionService {

    public List<Permission> findByUser(final User user) {
        return null;
    }
}

class User {
    private final String name;
    private final String password;

    public User(final String name, final String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}

class Role {
    private final String name;

    public Role(final String name) {
        this.name = name;
    }

    public String getKeyword() {
        return name;
    }
}