# Proposal of Artipie access control

Example demonstrates access control based on Shiro INI configuration:
```
[users]
admin = 2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b, admin
user1 = password1, repos_reader
user2 = password2, repos_writer
user3 = password3, repomaven_reader 
user4 = password4, repomaven_reader
repomaven_admin = password5, repomaven_admin

[roles]
admin = *
repos_reader = repo1:read,repo2:read 
repos_writer = repo1:writer,repo2:writer
repomaven_reader = repomaven:read
repomaven_writer = repomaven:writer
repomaven_admin = repomaven:*
```

Methods of Artipie can be annotated to require specific permissions, something like these:
```
@Permission("$currentRepo:read")
public void listOfMavenFiles(...)

@Permission("$currentRepo:write")
public void updateMavenArtifact(...)
```

Or code can explicitly check permission, something like this:
```
if ( SecurityUtils.getSubject().isPermitted(currentRepo() + ":read") {
    // do something
}
```

