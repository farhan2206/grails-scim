package grails.plugins.scim

import grails.converters.JSON
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping

//import grails.plugins.scim.dto.ScimResponseDTO

class ScimController {

    def scimService

//    static allowedMethods = [Users: ['GET','PUT','DELETE']]

//    def index() { }

//    @GetMapping('/users')
    def findUser(String userName,Integer count, Integer startIndex) {
        log.info('Username :' + userName)
        try{
            if (userName) {
                def responseData = scimService.checkUser(userName)
                if(responseData?.resources){
                    response.status = 200
                } else {
                    response.status = 404
                }
                respond responseData, [formats: ['json']]
            } else {
                def data = scimService.getUsers(count,startIndex)

                response.status = 200
                render (data as JSON)
            }
        }catch(Exception ex){
            log.info('Exception :'+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

//    @PostMapping('/users')
    def createUser() {
        println request.JSON
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

//    @GetMapping('/users/$id')
    def getUser(Long id) {
        try {
            def responseData = scimService.getUser(id)
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

//    @DeleteMapping('/users/$id')
    def deleteUser(Long id){
        try{
            scimService.deleteUserById(id)
            response.status = 204
        }catch(Exception ex){
            log.info('Exception : '+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }

//    @PutMapping('/users/$id')
    def updateUser(Long id){
        try{
            log.info('request ------'+request)
            log.info('Params -------------'+params)
            println request.JSON
            def data = scimService.createSCIMUser(request.JSON,id)
            response.status = 201
            response.setHeader('Content-Type','application/json')
            render (data as JSON)
        }catch(Exception ex){
            log.info('Exception : '+ex)
            ex.printStackTrace()
            response.status = 404
        }
    }


}
