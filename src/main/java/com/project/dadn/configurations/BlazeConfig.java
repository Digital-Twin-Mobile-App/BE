package com.project.dadn.configurations;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BlazeConfig {

    @Getter(AccessLevel.NONE)
    @PersistenceUnit
    private EntityManagerFactory emf;

    private CriteriaBuilderFactory cbf;
    private EntityManager em;

    @PostConstruct
    void initialize() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        this.cbf = config.createCriteriaBuilderFactory(emf);
        this.em = emf.createEntityManager();
    }

    @Bean
    public CriteriaBuilderFactory criteriaBuilderFactory() {
        return cbf;
    }

    @Bean
    public EntityManager entityManager() {
        return em;
    }
}

