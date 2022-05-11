package grails.plugins.scim

import grails.util.Holders


class ScimControllerInterceptor {

    String apiToken = Holders.config.grails.scim.api_token
    
    ScimControllerInterceptor(){
        match(controller:~/(scim)/)
    }

    boolean before() {
        String[] bearer = request.getHeader("Authorization").split(" ")
        String token =  bearer[1]
        if (token == apiToken){
            true
        } else {
            false
        }

    }


    boolean after() { true }

    void afterView() {
        // no-op
    }
}
