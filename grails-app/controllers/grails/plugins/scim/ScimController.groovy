package grails.plugins.scim

import grails.converters.JSON

class ScimController {

    def scimService

    static allowedMethods = [findUsers: 'GET',createUser:'POST',getUser:'GET',deleteUser:'DELETE',updateUser:'PUT',addScimGroup: 'POST',groupOperations:'PATCH',getGroups:'GET']

    def findUsers(String filter,Integer count, Integer startIndex) {
        try{
            if (filter) {
                String userName = ScimUtil.getuserNamefromFilter(filter)
                log.info('Username :'+userName)
                if (userName) {
                    def responseData = scimService.checkUser(userName)
                    if (responseData?.resources) {
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
            Map responseData = scimService.getUser(params.id)
            if(responseData){
            response.status = 201
            response.setHeader('Content-Type','application/json')
            render (responseData as JSON)
            } else {
                response.status = 404
            }
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
        }catch(Exception ex) {
            log.info('Exception : '+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

    //For groups
    //To Create Group
    def addScimGroup(){
        try{
            println "Request :: "+request.JSON
            println "Params :: "+params
            def data = scimService.createSCIMGroup(request.JSON)
            println "data :: "+data
            if(data){
                response.status = 201
                response.setHeader('Content-Type','application/json')
                render (data as JSON)
            } else {
                response.status = 400
                response.setHeader('Content-Type','application/json')
                data = [
                        "Errors" :[
                                "description": "name_already_exists",
                                "code" : "400"
                        ]
                ]
                render (data as JSON)
            }

        }catch(Exception ex){
            ex.printStackTrace()
        }
    }

    def groupOperations(){
        try{
            println "Request ::"+request.JSON
            println "Params ::"+params
            def data = scimService.performGroupOperations(params.id,request.JSON)
            response.status = 201
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }catch(Exception ex){
            ex.printStackTrace()
            Map data = [:]
            response.status = 500
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }
    }

    def getGroups(){
        try{
            def data = scimService.getSCIMGroups(request.JSON)
            response.status = 201
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }catch (Exception ex){
            ex.printStackTrace()
            Map data = [:]
            response.status = 500
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }
    }

}
