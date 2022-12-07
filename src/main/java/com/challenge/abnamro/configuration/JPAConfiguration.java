package com.challenge.abnamro.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class JPAConfiguration {

	/**
	 * Transaction Manager bean to be used by Spring's transaction management
	 *
	 * @param dataSource for database
	 * @return Transaction Manager of type {@link PlatformTransactionManager}
	 */
	@Bean
	public PlatformTransactionManager transactionManager(final DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
