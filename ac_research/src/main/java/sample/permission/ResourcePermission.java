package sample.permission;

import java.security.BasicPermission;

public final class ResourcePermission extends BasicPermission {
    public ResourcePermission(String name) {
        super(name);
    }
}