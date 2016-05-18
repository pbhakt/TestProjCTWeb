package com.clicktable.config;


import org.apache.commons.beanutils.BeanUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.JtaTransactionManagerFactoryBean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.rest.SpringCypherRestGraphDatabase;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.clicktable.model.ByPassRequest;
import com.clicktable.model.Permission;
import com.clicktable.util.Constants;
import com.clicktable.util.UtilityMethods;
import com.typesafe.config.Config;

@EnableNeo4jRepositories(basePackages = { Constants.DAO_INTF_PCKG, Constants.NEO_4J_REPO_PCKG, Constants.DAO_IMPL_PCKG })
@ComponentScan({ Constants.CONTROLLERS_PCKG, Constants.SERVICE_INTF_PCKG, Constants.SERVICE_IMPL_PCKG, Constants.DAO_INTF_PCKG, Constants.DAO_IMPL_PCKG, Constants.VALIDATE_PCKG,
		Constants.NEO_4J_REPO_PCKG, Constants.SCHEDULER_PCKG})
@Configuration
@EnableTransactionManagement
public class Neo4jConfig extends Neo4jConfiguration {

	public Neo4jConfig() {
		setBasePackage(Constants.MODEL_PCKG, Constants.REL_MODEL_PCKG);
	}

	/*
	 * @Bean public EntityManagerFactory entityManagerFactory() { return
	 * Persistence.createEntityManagerFactory(Constants.DEFAULT); }
	 */

	@Bean
	public GraphDatabaseService graphDatabaseService() {

		Config set = UtilityMethods.getNeo4jConfig();
		SpringCypherRestGraphDatabase dbService;
		if (set.hasPath(Constants.USER) && set.hasPath(Constants.PWD))
			dbService= new SpringCypherRestGraphDatabase(set.getString(Constants.PATH), set.getString(Constants.USER), set.getString(Constants.PWD));
		else
			dbService= new SpringCypherRestGraphDatabase(set.getString(Constants.PATH));
		
		return dbService;

	}

	@Override
	@Bean(name = Constants.TRANSACTION_MANAGER)
	public PlatformTransactionManager neo4jTransactionManager(GraphDatabaseService service) {
		return new ChainedTransactionManager(/*
											 * new JpaTransactionManager(
											 * entityManagerFactory()),
											 */new JtaTransactionManagerFactoryBean(service).getObject());// getGraphDatabaseService()).getObject());
	}

	@Bean
	public Permission permission() {
		return new Permission();
	}
	@Bean
	public ByPassRequest byPassRequest() {
		return new ByPassRequest();
	}


	@Bean
	public BeanUtils getBeans() {
		return new BeanUtils();
	}

}
