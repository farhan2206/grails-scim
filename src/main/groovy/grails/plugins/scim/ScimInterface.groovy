package grails.plugins.scim

import groovy.transform.CompileStatic

@CompileStatic
interface ScimInterface {

    def searchUserByUsernameOrEmail(String username);

    def getUserBySCIMId(Long id);

    def saveSCIMUser(Map scimUser);

    def updateSCIMUser(Map scimUser,Long id);

    void deleteSCIMUser(Long id)

    def getUsersList(Integer count , Integer startIndex )

}