# oracle-jdbc-kerberos
Example of Oracle JDBC Driver wrapper with Kerberos authentication

Usage:

```java
String url = new String("jdbc:oracle:thin@//1.2.3.4:555/DB");
String cacheFile = new String("/tmp/krbcc_1234");
DriverManager.registerDriver(new KrbOracleDriverWrapper(cacheFile));
Connection conn = DriverManager.getConnection(url);
```
