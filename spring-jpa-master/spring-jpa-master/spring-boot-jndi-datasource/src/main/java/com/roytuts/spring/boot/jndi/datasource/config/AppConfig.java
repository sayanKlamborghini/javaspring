package com.roytuts.spring.boot.jndi.datasource.config;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
//@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = "com.roytuts.spring.boot.jndi.datasource.repository")
public class AppConfig {

	@Autowired
	private Environment env;

	@Bean
	public DatabaseProperties databaseProperties() {
		return new DatabaseProperties();
	}

	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		return new TomcatServletWebServerFactory() {
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				ContextResource resource = new ContextResource();

				// resource.setType(DataSource.class.getName());
				resource.setType("org.apache.tomcat.jdbc.pool.DataSource");
				// resource.setName(env.getProperty("jndiName"));
				resource.setName(databaseProperties().getJndiName());
				resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
				// resource.setProperty("driverClassName", env.getProperty("driverClassName"));
				// resource.setProperty("url", env.getProperty("jdbcUrl"));
				// resource.setProperty("password", env.getProperty("username"));
				// resource.setProperty("username", env.getProperty("password"));
				resource.setProperty("driverClassName", databaseProperties().getDriverClassName());
				resource.setProperty("url", databaseProperties().getUrl());
				resource.setProperty("password", databaseProperties().getUsername());
				resource.setProperty("username", databaseProperties().getPassword());

				context.getNamingResources().addResource(resource);
			}
		};
	}

	@Bean(destroyMethod = "")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		// bean.setJndiName("java:comp/env/" + env.getProperty("jndiName"));
		bean.setJndiName("java:comp/env/" + databaseProperties().getJndiName());
		bean.setProxyInterface(DataSource.class);
		// bean.setResourceRef(true);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource) bean.getObject();
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() throws SQLException, IllegalArgumentException, NamingException {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);
		vendorAdapter.setShowSql(true);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.roytuts.spring.boot.jndi.datasource.entity");
		factory.setDataSource(jndiDataSource());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager()
			throws SQLException, IllegalArgumentException, NamingException {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

}
