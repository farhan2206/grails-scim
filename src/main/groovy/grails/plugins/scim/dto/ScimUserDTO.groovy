package grails.plugins.scim.dto

import grails.web.servlet.mvc.GrailsParameterMap

class ScimUserDTO {

    Long id
    String username
//    String password = ""
    String fullName
    String email


//    Map toMap() {
//        this.username = params.userName
//        this.fullName = params.name.givenName + " " + params.name.familyName
//        this.email = params.email
//    }

}
