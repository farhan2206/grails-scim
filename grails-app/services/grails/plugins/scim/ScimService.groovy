package grails.plugins.scim

import grails.converters.JSON
import grails.plugins.scim.dto.ScimEmailDTO
import grails.plugins.scim.dto.ScimGroupDTO
import grails.plugins.scim.dto.ScimMetaDTO
import grails.plugins.scim.dto.ScimNameDTO
import grails.plugins.scim.dto.ScimPhotoDTO
import grails.plugins.scim.dto.ScimResourceDTO
import grails.plugins.scim.dto.ScimResponseDTO
import grails.util.Holders
import org.joda.time.DateTimeZone


class ScimService {

    def scimInterface

    def SCIM_SCHEMAS = Holders.config.grails.scim.schemas
    final String LOCATION = Holders.config.grails.serverURL



    def checkUser(String username) {
        ScimResponseDTO scimResponseDTO = new ScimResponseDTO()
        def user =  scimInterface.searchUserByUsernameOrEmail(username)
        bindUserResponse(user,scimResponseDTO)

    }

    def bindUserResponse(def user,ScimResponseDTO scimResponseDTO){

        ArrayList<ScimResourceDTO> scimResourceDTOList = []
        ScimResourceDTO scimResourceDTO = []
        ScimMetaDTO scimMetaDTO = new ScimMetaDTO()
        ArrayList<ScimEmailDTO> emails = []
        ArrayList<ScimPhotoDTO> photos = []
        ArrayList<ScimGroupDTO> groups = []
        scimResponseDTO.totalResults = 1
        scimResponseDTO.itemsPerPage = 10
        scimResponseDTO.startIndex = 1
        scimResponseDTO.schemas = SCIM_SCHEMAS

        println "SCIM_SCHEMAS "+SCIM_SCHEMAS

        if(user){
            scimResourceDTO.schemas = SCIM_SCHEMAS
            scimMetaDTO.created = new Date()
            scimMetaDTO.location = LOCATION + '/scim/Users/'+user?.id
            scimResourceDTO.id = user?.id
            scimResourceDTO.externalId = user?.id
            scimResourceDTO.userName = user?.username
            scimResourceDTO.nickName = ''
            scimResourceDTO.name.givenName = user?.fullName.split("\\s")[0] ?:''
            scimResourceDTO.name.familyName = user?.fullName.split("\\s")[1] ?:''
            scimResourceDTO.displayName = user?.fullName
            scimResourceDTO.active = user?.enabled
            scimResourceDTOList.add(scimResourceDTO)
            scimResponseDTO.resources = scimResourceDTOList
        } else {
            scimResourceDTOList = []
            scimResponseDTO.resources = scimResourceDTOList
        }
        scimResponseDTO
    }

    def getUser(Long id){
        ScimResponseDTO scimResponseDTO = new ScimResponseDTO()
        def user = scimInterface.getUserBySCIMId(id)
        bindUserResponse(user,scimResponseDTO)
    }



    def createSCIMUser(def params, Long id = null){
        Map data = [:]
        Map scimUser = [
                username : params.userName,
                fullName : params.name.givenName +" "+params.name.familyName,
                email : params.emails.value
        ]
        log.info("Map SCIM User -----------"+scimUser)
        def user = null
        if(id){
            user = scimInterface.updateSCIMUser(scimUser,id)
        } else {
            user = scimInterface.saveSCIMUser(scimUser)
        }
        if(user){
            ScimResourceDTO scimResourceDTO = new ScimResourceDTO()
            ArrayList<String> schemasResource = []
            ArrayList<ScimEmailDTO> emails = []
            ArrayList<ScimPhotoDTO> photos = []
            ArrayList<ScimGroupDTO> groups = []
            ScimMetaDTO scimMetaDTO = new ScimMetaDTO()
            ScimNameDTO scimNameDTO = new ScimNameDTO()
            scimMetaDTO.created = new Date()
            scimMetaDTO.location = LOCATION + '/scim/Users/'+user?.id
            scimResourceDTO.meta = scimMetaDTO
            scimNameDTO.familyName = params.name.familyName
            scimNameDTO.givenName = params.name.givenName

            // Data as List containing Object
            data = [
                    schemas : SCIM_SCHEMAS,
                    id : user?.id,
                    externalId : user?.id,
                    meta : scimMetaDTO,
                    name : scimNameDTO,
                    userName : user?.username,
                    displayname : user?.username,
                    timezone : DateTimeZone.UTC.ID,
                    active : true,
                    photos : photos,
                    emails : emails,
                    groups : groups

            ]
        }
        data
    }

    void deleteUserById(Long id){
        scimInterface.deleteSCIMUser(id)
    }

    def getUsers(Integer count, Integer startIndex){
        def users = scimInterface.getUsersList(count,startIndex)
        log.info('Users:'+users)
        ScimResponseDTO scimResponseDTO = []
        ArrayList<ScimResourceDTO> scimResourceDTOArrayList = []
        ScimResourceDTO scimResourceDTO = []
        ArrayList<String> schemasResource = []
        ArrayList<ScimEmailDTO> emails = []
        ArrayList<ScimPhotoDTO> photos = []
        ArrayList<ScimGroupDTO> groups = []
        scimResponseDTO.totalResults = users.size()
        scimResponseDTO.itemsPerPage = count?:10
        scimResponseDTO.startIndex = startIndex?:0
        scimResponseDTO.schemas = SCIM_SCHEMAS

        users.each{
            scimResourceDTO.schemas = SCIM_SCHEMAS


            scimResourceDTO.id = it?.id
            scimResourceDTO.externalId = it?.id
            scimResourceDTO.userName = it?.username
            scimResourceDTO.displayName = it?.fullName
            scimResourceDTO.active = true
            scimResourceDTOArrayList.add(scimResourceDTO)

        }

        scimResponseDTO.resources = scimResourceDTOArrayList

        println scimResponseDTO as JSON
        scimResponseDTO


    }



}
