# Apache Shiro

Links:
* [http://shiro.apache.org](http://shiro.apache.org) 
* [https://github.com/apache/shiro](https://github.com/apache/shiro)

Shiro provides the application security API to perform the following aspects:
* Authentication - proving user identity, often called user ‘login’.
* Authorization - access control
* Cryptography - protecting or hiding data from prying eyes
* Session Management - per-user time-sensitive state


## Main concepts:
### Subject 
The 'Subject' is concept of 'User', 'Process', 'Task' and etc.
  
  In Artipie is a 'User'.

The Subject represents security operations for the current user.

  Code:
```
import org.apache.shiro.subject.Subject;
import org.apache.shiro.SecurityUtils;
...
Subject currentUser = SecurityUtils.getSubject();
```

### SecurityManager
   
The SecurityManager manages security operations for all users.

Configuring Shiro with INI:
```
[main]
cm = org.apache.shiro.authc.credential.HashedCredentialsMatcher
cm.hashAlgorithm = SHA-512
cm.hashIterations = 1024
# Base64 encoding (less text):
cm.storedCredentialsHexEncoded = false

iniRealm.credentialsMatcher = $cm

[users]
jdoe = TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJpcyByZWFzb2
asmith = IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbXNoZWQsIG5vdCB
```

Loading shiro.ini Configuration File:
```
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;

//1. Load the INI configuration
Factory<SecurityManager> factory =
new IniSecurityManagerFactory("classpath:shiro.ini");

//2. Create the SecurityManager
SecurityManager securityManager = factory.getInstance();

//3. Make it accessible
SecurityUtils.setSecurityManager(securityManager);
```


1. Load the INI configuration that will configure the SecurityManager and its constituent components.
2. Create the SecurityManager instance based on the configuration (using Shiro’s Factory concept that represents the Factory Method design pattern).
3. Make the SecurityManager singleton accessible to the application. In this simple example, we set it as a VM-static singleton, but this is usually not necessary - your application configuration mechanism can determine if you need to use static memory or not.

### Realms
A Realm is essentially a security-specific DAO: it encapsulates connection details for data sources.

Shiro provides out-of-the-box Realms to connect to a number of security data sources (aka directories) such as 
* LDAP, 
* relational databases (JDBC), 
* text configuration sources like INI and properties files, and more. 
* You can plug-in your own Realm implementations to represent custom data sources if the default Realms do not meet your needs.

Example realm configuration snippet to connect to LDAP user data store:
```
[main]
ldapRealm = org.apache.shiro.realm.ldap.JndiLdapRealm
ldapRealm.userDnTemplate = uid={0},ou=users,dc=mycompany,dc=com
ldapRealm.contextFactory.url = ldap://ldapHost:389
ldapRealm.contextFactory.authenticationMechanism = DIGEST-MD5 
```

## Authentication
Authentication is the process of verifying a user's identity. That is, when a user authenticates with an application, they are proving they actually are who they say they are. This is also sometimes referred to as 'login'. This is typically a three-step process.

Collect the user’s identifying information, called principals, and supporting proof of identity, called credentials.
Submit the principals and credentials to the system.
If the submitted credentials match what the system expects for that user identity (principal), the user is considered authenticated. If they don’t match, the user is not considered authenticated.

Subject Login:
```
//1. Acquire submitted principals and credentials:
AuthenticationToken token =
new UsernamePasswordToken(username, password);

//2. Get the current Subject:
Subject currentUser = SecurityUtils.getSubject();

//3. Login:
currentUser.login(token);
```

Handle Failed Login:
```
//3. Login:
try {
    currentUser.login(token);
} catch (IncorrectCredentialsException ice) { …
} catch (LockedAccountException lae) { …
}
…
catch (AuthenticationException ae) {…
} 
```

You can choose to catch one of the AuthenticationException subclasses and react specifically, 
or generically handle any AuthenticationException (for example, show the user a generic “Incorrect username or password” message). The choice is yours depending on your application requirements.

## Authorization
Role Check:
```
if ( subject.hasRole(“administrator”) ) {
  //show the ‘Create User’ button
} else {
  //grey-out the button?
} 
```

Permission Check:
```
if ( subject.isPermitted(“user:create”) ) {
  //show the ‘Create User’ button
} else {
  //grey-out the button?
} 
```

The “user:create” string is an example of a permission string that adheres to certain parsing conventions. 
Shiro supports this convention out of the box with its WildcardPermission. 
Although out of scope for this introduction article, you’ll see that the WildcardPermission can be extremely flexible when creating security policies, and even supports things like instance-level access control.

Instance-Level Permission Check:
```
if ( subject.isPermitted(“user:delete:jsmith”) ) {
  //delete the ‘jsmith’ user
} else {
  //don’t delete ‘jsmith’
}
```

## Session Management

Shiro enables a Session programming paradigm for any application - from small daemon standalone applications to the largest clustered web applications. This means that application developers who wish to use sessions are no longer forced to use Servlet or EJB containers if they don’t need them otherwise.

Shiro’s sessions is that they are container-independent.

Shiro’s architecture allows for pluggable Session data stores, such as enterprise caches, relational databases, NoSQL systems and more. This means that you can configure session clustering once and it will work the same way regardless of your deployment environment - Tomcat, Jetty, JEE Server or standalone application.

Subject’s Session:
```
Session session = subject.getSession();
Session session = subject.getSession(boolean create);
```

Session methods:
```
Session session = subject.getSession();

session.getAttribute(“key”, someValue);

Date start = session.getStartTimestamp();
Date timestamp = session.getLastAccessTime();
session.setTimeout(millis);
```


## More about Permissions
Shiro defines a Permission as a statement that defines an explicit behavior or action.

Some examples of permissions:
* Open a file
* View the '/user/list' web page
* Print documents
* Delete the 'jsmith' user

Permissions can be grouped in a Role and that Role could be associated with one or more User objects.

There are many variations for how permissions could be granted to users - the application determines how to model this based on the application requirements.

### Simple Usage

```
subject.isPermitted("queryPrinter")
```

This is (mostly) equivalent to:
```
subject.isPermitted( new WildcardPermission("queryPrinter") )
```

You can also grant a user "*" permissions using the wildcard character.

### Multiple Parts
```
printer:query
```

In this example, the first part is the domain that is being operated on (printer) and the second part is the action (query) being performed.

```
printer:print
printer:manage
```

### Multiple Values
So instead of granting the user both the "printer:print" and "printer:query" permissions, you could simply grant them one:
```
printer:print,query
```


which gives them the ability to print and query printers. And since they are granted both those actions, you could check to see if the user has the ability to query printers by calling:
```
subject.isPermitted("printer:query")
```

### All Values
Instead to list all permissions like this:
```
printer:query,print,manage
```
simply do:
```
printer:*
```

Finally, it is also possible to use the wildcard token in any part of a wildcard permission string. For example, if you wanted to grant a user the "view" action across all domains (not just printers), you could grant this:
```
*:view
```

### Instance-Level Access Control
```
printer:query:lp7200
printer:print:epsoncolor
```

The first defines the behavior to query the printer with the ID lp7200. The second permission defines the behavior to print to the printer with ID epsoncolor. If you grant these permissions to users, then they can perform specific behavior on specific instances. Then you can do a check in code:
```
if ( SecurityUtils.getSubject().isPermitted("printer:query:lp7200") {
    // Return the current jobs on printer lp7200 }
}
```

Permit 'print' action to all instances of printers:
```
printer:print:*
```

Permit all actions to all instances of printers:
```
printer:*:*
```

Permit all actions to specific printer:
```
printer:*:lp7200
```

or even specific actions:
```
printer:query,print:lp7200
```

### Missing Parts
Missing parts imply that the user has access to all values corresponding to that part. In other words,
```
printer:print
```
is equivalent to
```
printer:print:*
```

and
```
printer
```
is equivalent to
```
printer:*:*
```