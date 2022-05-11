package grails.plugins.scim

import grails.converters.JSON

class ScimController {

    def scimService

    static allowedMethods = [findUsers: 'GET',createUser:'POST',getUser:'GET',deleteUser:'DELETE',updateUser:'PUT']

    def findUsers(String filter,Integer count, Integer startIndex) {
        try{
            if(filter){
                String userName = ScimUtil.getuserNamefromFilter(filter)
                log.info('Username :' +userName)
                if (userName) {
                    def responseData = scimService.checkUser(userName)
                    if(responseData?.resources){
                        response.status = 200
                    } else {
                        response.status = 200
                    }
                    respond responseData, [formats: ['json']]
                } else {
                    def data = scimService.getUsers(count,startIndex)
                    response.status = 200
                    render (data as JSON)
                }
            }

        }catch(Exception ex){
            log.info('Exception :'+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

    def createUser() {
        try{
            if(request?.JSON){
                def data = scimService.createSCIMUser(request?.JSON)
                response.status = 201
                response.setHeader('Content-Type','application/json')
                render (data as JSON)
            } else {
                //TODO Return list of all users
                response.status = 404
            }
        }catch (Exception ex){
            log.info("Exception :"+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

    def getUser() {
        try {
            def responseData = scimService.getUser(params.id)
            if (responseData?.resources) {
                response.status = 200
            } else {
                response.status = 404
            }
            respond responseData, [formats: ['json']]
        } catch (Exception ex) {
            log.info('Exception : ' + ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

    def deleteUser(){
        try{
            scimService.deleteUserById(params.id)
            response.status = 204
        }catch(Exception ex){
            log.info('Exception : '+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

    def updateUser(){
        try{
            def data = scimService.createSCIMUser(request.JSON,params.id)
            response.status = 200
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }catch(Exception ex){
            log.info('Exception : '+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }


}
