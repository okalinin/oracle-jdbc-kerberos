package okalinin.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.lang.IllegalArgumentException;
import java.util.logging.Logger;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.net.ano.AnoServices;

/**
 * Example of JDBC driver wrapper for connection to Oracle DB with Kerberos authentication.
 *
 * Usage:
 *
 * String cachefile = new String("/tmp/krbcc_1234");
 * DriverManager.registerDriver(new KrbOracleDriverWrapper(cachefile));
 * Connection connection = DriverManager.getConnection(url);
 * ...
 */
public class KrbOracleDriverWrapper implements Driver {
  OracleDriver oraDriver;
  String krbCacheFile;
  String krbConfFile = new String("/etc/krb5.conf");

  /**
   * default constructor is disabled in this driver as Kerberos cache file has to be set via parameter
   */
  KrbOracleDriverWrapper() throws IllegalArgumentException {
    throw new IllegalArgumentException("Constructor must be called with parameters");
  }

  /**
   * constructor with default krb5 configuration file
   * Kerberos cache file set explicitly
   *
   * @param cacheFile local filesystem path to Kerberos cache file
   */
  KrbOracleDriverWrapper(String cacheFile) {
    oraDriver = new OracleDriver();
    krbCacheFile = cacheFile;
  }

  /**
   * constructor with explicit setting of both Kerberos cache and configuration files
   *
   * @param cacheFile local filesystem path to Kerberos cache file
   * @param confFile local filesystem path to Kerberos configuration file
   */
  KrbOracleDriverWrapper(String cacheFile, String confFile) {
    oraDriver = new OracleDriver();
    krbCacheFile = cacheFile;
    krbConfFile = confFile;
  }

  @Override
  public boolean acceptsURL(String url) {
    return oraDriver.acceptsURL(url);
  }

  /**
   * triggers connection with Kerberos authentication by modifying JDBC properties
   *
   * @param url URL to connect to
   * @param properties properties to pass to JDBC driver
   */
  @Override
  public Connection connect(String url, Properties properties) throws SQLException {
    properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_AUTHENTICATION_SERVICES,
        AnoServices.AUTHENTICATION_KERBEROS5);
    properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_AUTHENTICATION_KRB5_MUTUAL, "true");
    properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_AUTHENTICATION_KRB5_CC_NAME, krbCacheFile);
    System.setProperty("java.security.krb5.conf", krbConfFile);
    return oraDriver.connect(url, properties);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public boolean jdbcCompliant() {
    return oraDriver.jdbcCompliant();
  }

  @Override
  public int getMajorVersion() {
    return oraDriver.getMajorVersion();
  }

  @Override
  public int getMinorVersion() {
    return oraDriver.getMinorVersion();
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties)
      throws SQLException {
    return oraDriver.getPropertyInfo(url, properties);
  }
}
