package com.malaka.aat.internal.config;

import com.malaka.aat.internal.model.BaseEntity;
import com.malaka.aat.internal.model.spr.LangSpr;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DefaultLangEntityListener implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @PrePersist
    public void setDefaultLang(BaseEntity entity) {
        // Skip if entity is LangSpr itself to avoid circular dependency
        if (entity instanceof LangSpr) {
            return;
        }

        // Only set default lang if it's null
        if (entity.getLang() == null && context != null) {
            try {
                EntityManager entityManager = context.getBean(EntityManager.class);
                // Use getReference to get a lazy proxy without hitting the database
                LangSpr defaultLang = entityManager.getReference(LangSpr.class, 0L);
                entity.setLang(defaultLang);
            } catch (Exception e) {
                // If EntityManager is not available or LangSpr doesn't exist yet,
                // skip setting default lang (this can happen during initial data loading)
            }
        }
    }
}
