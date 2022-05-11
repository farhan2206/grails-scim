package grails.plugins.scim

import groovy.transform.CompileStatic

@CompileStatic
interface ScimInterface {

    def searchUserByUsername(String username)

    def getUserBySCIMId(String id);

    def saveSCIMUser(Map scimUser);

    def updateSCIMUser(Map scimUser,String id);

    void deleteSCIMUser(String id)

    def getUsersList(Integer count , Integer startIndex )

}