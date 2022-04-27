package grails.plugins.scim

import grails.util.Holders
import org.grails.datastore.mapping.model.PersistentEntity

class ScimUtil {

    /**
     * Return the configured Grails Domain Class.
     *
     * @param domain the domain instance
     */
    static Class<?> getDomainClass(String domainClassName) {
       // def conf = AuditLoggingConfigUtils.auditConfig
        String auditLogClassName = domainClassName
        if (auditLogClassName == null){
            throw new IllegalArgumentException("grails.plugin.auditLog.auditDomainClassName could not be found in application.groovy. Have you performed 'grails audit-quickstart' after installation?")
        }
        def dc = Holders.applicationContext.getBean('grailsDomainClassMappingContext').getPersistentEntity(auditLogClassName)
        if (!dc) {
            throw new IllegalArgumentException("The specified user domain class '$auditLogClassName' is not a domain class")
        }
        dc.javaClass
    }


    /**
     * Return Instance of the configured Grails Domain Class.
     *
     * @param domain the domain instance
     */
    static def getDomainInstance(params) {
        Class<?> dc = getDomainClass()
        dc.newInstance(params)
    }

    /**
     * Return the grails domain class for the given domain object.
     *
     * @param domain the domain instance
     */
    static PersistentEntity getPersistentEntity(domain) {
        if (domain && Holders.grailsApplication.isDomainClass(domain.class)) {
            Holders.applicationContext.getBean('grailsDomainClassMappingContext').getPersistentEntity(domain.class.name)
        } else {
            null
        }
    }


}
