package grails.plugins.scim

import grails.config.Config
import grails.converters.JSON
import grails.core.support.GrailsConfigurationAware
import grails.plugins.scim.dto.ScimEmailDTO
import grails.plugins.scim.dto.ScimGroupDTO
import grails.plugins.scim.dto.ScimGroupMetaDTO
import grails.plugins.scim.dto.ScimGroupResponseDTO
import grails.plugins.scim.dto.ScimGroupsResourcesDTO
import grails.plugins.scim.dto.ScimMetaDTO
import grails.plugins.scim.dto.ScimNameDTO
import grails.plugins.scim.dto.ScimPhotoDTO
import grails.plugins.scim.dto.ScimResourceDTO
import grails.plugins.scim.dto.ScimResponseDTO
import org.joda.time.DateTimeZone


class ScimService implements GrailsConfigurationAware{

    ScimInterface scimInterface

    private String scimSchemas
    private String scimLocation
    private String scimUserUrl


    ScimResponseDTO checkUser(String username) {
        ScimResponseDTO scimResponseDTO = new ScimResponseDTO()
        def user =  scimInterface.searchUserByUsername(username)
        println ">>>>>>>>>>>> User >>>>>>>>>>>>>>"+user
        bindUserResponse(user,scimResponseDTO)

    }

    ScimResponseDTO bindUserResponse(def user,ScimResponseDTO scimResponseDTO){
        ArrayList<ScimResourceDTO> scimResourceDTOList = []
        ScimResourceDTO scimResourceDTO = []
        ScimMetaDTO scimMetaDTO = new ScimMetaDTO()
        ScimNameDTO scimNameDTO = new ScimNameDTO()
        ArrayList<ScimEmailDTO> emails = []
        ArrayList<ScimGroupDTO> groups = []
        ScimEmailDTO scimEmailDTO = new ScimEmailDTO()

        scimResponseDTO.totalResults = 1
        scimResponseDTO.itemsPerPage = 10
        scimResponseDTO.startIndex = 1
        scimResponseDTO.schemas = Arrays.asList(scimSchemas.split(","))
        if(user){
            scimResourceDTO.schemas = Arrays.asList(scimSchemas.split(","))
            scimMetaDTO.created = new Date()
            scimMetaDTO.location = scimLocation +scimUserUrl +user?.scimId
            scimResourceDTO.id = user?.scimId
            scimResourceDTO.externalId = user?.id
            scimResourceDTO.meta = scimMetaDTO
            scimResourceDTO.userName = user?.username
            scimResourceDTO.nickName = ''
            scimNameDTO.givenName = user?.fullName.split("\\s")[0] ?:''
            scimNameDTO.familyName = user?.fullName.split("\\s")[1] ?:''
            scimResourceDTO.name = scimNameDTO
            scimResourceDTO.displayName = user?.fullName
            scimResourceDTO.active = user?.enabled
            scimEmailDTO.value = user?.email
            scimEmailDTO.primary = true
            emails.add(scimEmailDTO)
            scimResourceDTO.emails = emails
            //def userGroup = scimInterface.getUserGroupsByUser(user)
            def userGroups = user?.userGroups
            userGroups.each{
                ScimGroupDTO scimGroupDTO = new ScimGroupDTO()
                scimGroupDTO.value = it.id
                scimGroupDTO.display = it.name
                groups.add(scimGroupDTO)
            }

            scimResourceDTO.groups = groups

            scimResourceDTOList.add(scimResourceDTO)
            scimResponseDTO.resources = scimResourceDTOList
        } else {
            scimResourceDTOList = []
            scimResponseDTO.resources = scimResourceDTOList
        }
        scimResponseDTO
    }

    def getUser(String id){
        def user = scimInterface.getUserBySCIMId(id)
        Map data = [:]
        if(user) {
            ScimResourceDTO scimResourceDTO = new ScimResourceDTO()
            ArrayList<String> schemasResource = []
            ArrayList<ScimEmailDTO> emails = []
            ArrayList<ScimPhotoDTO> photos = []
            ArrayList<ScimGroupDTO> groups = []
            ScimMetaDTO scimMetaDTO = new ScimMetaDTO()
            ScimNameDTO scimNameDTO = new ScimNameDTO()
            scimMetaDTO.created = new Date()
            scimMetaDTO.location = scimLocation + scimUserUrl+user?.scimId
            scimResourceDTO.meta = scimMetaDTO
            scimNameDTO.givenName = user?.fullName.split("\\s")[0] ?:''
            scimNameDTO.familyName = user?.fullName.split("\\s")[1] ?:''
            ScimEmailDTO scimEmailDTO = new ScimEmailDTO()
            scimEmailDTO.value = user?.email
            scimEmailDTO.primary = true
            emails.add(scimEmailDTO)
            def userGroups = user?.userGroups
            userGroups.each{
                ScimGroupDTO scimGroupDTO = new ScimGroupDTO()
                scimGroupDTO.value = it.id
                scimGroupDTO.display = it.name
                groups.add(scimGroupDTO)
            }

            scimResourceDTO.groups = groups

            // Data as List containing Object
            data = [
                    schemas : Arrays.asList(scimSchemas.split(",")),
                    id : user?.scimId,
                    externalId : user?.id,
                    meta : scimMetaDTO,
                    userName : user?.username,
                    nickName : '',
                    name : scimNameDTO,
                    displayname : user?.username,
                    profileUrl : scimLocation + scimUserUrl+user?.scimId,
                    title : '',
                    timezone : DateTimeZone.UTC.ID,
                    active : true,
                    photos : photos,
                    emails : emails,
                    groups : groups

            ]
            data
        } else {
            data
        }

    }



    Map createSCIMUser(def params, String id = null){
        Map data = [:]
        Map scimUser = [
                username : params.userName,
                fullName : params.name.givenName +" "+params.name.familyName,
                email : params.emails[0].value,
                scimId : UUID.randomUUID().toString(),
                active : params.active,
                groups : params.groups
        ]
        def user = scimInterface.searchUserByUsername(params.userName)
        if(user){
            id = user?.scimId
        }
        if(id && id != ''){
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
            scimMetaDTO.location = scimLocation + scimUserUrl+user?.scimId
            scimResourceDTO.meta = scimMetaDTO
            scimNameDTO.familyName = params.name.familyName
            scimNameDTO.givenName = params.name.givenName
            ScimEmailDTO scimEmailDTO = new ScimEmailDTO()
            scimEmailDTO.value = user?.email
            scimEmailDTO.primary = true
            emails.add(scimEmailDTO)

            // Data as List containing Object
            data = [
                    schemas : Arrays.asList(scimSchemas.split(",")),
                    id : user?.scimId,
                    externalId : user?.id,
                    meta : scimMetaDTO,
                    userName : user?.username,
                    nickName : '',
                    name : scimNameDTO,
                    displayname : user?.username,
                    profileUrl : scimLocation + scimUserUrl+user?.scimId,
                    title : '',
                    timezone : DateTimeZone.UTC.ID,
                    active : true,
                    photos : photos,
                    emails : emails,
                    groups : groups

            ]
        }
        data
    }

    void deleteUserById(String id){
        scimInterface.deleteSCIMUser(id)
    }

    ScimResponseDTO getUsers(Integer count, Integer startIndex){
        def users = scimInterface.getUsersList(count,startIndex)
        log.info('Users:'+users)
        ScimResponseDTO scimResponseDTO = []
        ArrayList<ScimResourceDTO> scimResourceDTOArrayList = []
        ScimResourceDTO scimResourceDTO = []
        ArrayList<ScimEmailDTO> emails = []
        scimResponseDTO.totalResults = users?.size()
        scimResponseDTO.itemsPerPage = count?:10
        scimResponseDTO.startIndex = startIndex?:0
        scimResponseDTO.schemas = Arrays.asList(scimSchemas.split(","))
        ScimMetaDTO scimMetaDTO = new ScimMetaDTO()
        ScimNameDTO scimNameDTO = new ScimNameDTO()
        users?.each{
            scimResourceDTO.schemas = Arrays.asList(scimSchemas.split(","))
            scimMetaDTO.created = it?.dateCreated
            scimMetaDTO.location = scimLocation + scimUserUrl+it?.scimId
            scimResourceDTO.meta = scimMetaDTO
            scimResourceDTO.id = it?.scimId
            scimResourceDTO.externalId = it?.id
            scimResourceDTO.userName = it?.username
            scimResourceDTO.displayName = it?.fullName
            scimResourceDTO.nickName = ''
            scimNameDTO.givenName = it?.fullName.split("\\s")[0] ?:''
            scimNameDTO.familyName = it?.fullName.split("\\s")[1] ?:''
            scimResourceDTO.name = scimNameDTO
            scimResourceDTO.active = true
            scimResourceDTOArrayList.add(scimResourceDTO)

        }

        scimResponseDTO.resources = scimResourceDTOArrayList

        println scimResponseDTO as JSON
        scimResponseDTO


    }

    @Override
    void setConfiguration(Config config) {
        this.scimLocation = config.getProperty('grails.serverURL')
        this.scimSchemas = config.getProperty('grails.scim.schemas')
        this.scimUserUrl = config.getProperty('grails.scim.user_resource_url')
    }

    //Create Group
    def createSCIMGroup(def params) {
        println params
        Map data = [:]
        Boolean result = scimInterface.checkGroupExists(params.displayName)
        if(!result){
            Map scimGroup = [
                    displayName : params.displayName
                    //scimId : UUID.randomUUID().toString()
            ]
            def group = scimInterface.createSCIMGroup(scimGroup)
            if(group){
                ScimGroupMetaDTO scimGroupMetaDTO = new ScimGroupMetaDTO()
                scimGroupMetaDTO.lastCreated = group.dateCreated
                scimGroupMetaDTO.lastModified = group.lastUpdated
                scimGroupMetaDTO.resourceType = "Group"
                // Data as List containing Object
                data = [
                        schemas : ["urn:ietf:params:scim:schemas:core:2.0:Group"],
                        id : group?.id,
                        externalId : group?.id,
                        meta : scimGroupMetaDTO,
                        displayName: group?.name,
                        members: []
                ]
            }
            data
        }
    }

    def performGroupOperations(def id, def params) {
        Map data = [:]
        def group = scimInterface.checkGroupExistsById(id)
        if(group){
            println params
            params.members.each{
                println it.value
                println "Value s::"+it.value.toString()
                def user = scimInterface.getUserBySCIMId(it.value as String)
                   println "User :: "+user
                if(user){
                    def userGroupUser = scimInterface.addScimUserGroupUser(group,user)
                }

            }
        }
        data
    }

    def getSCIMGroups(def p) {
        Map data = [:]
        def userGroups = scimInterface.getAllUserGroups()
        if(userGroups){
            ArrayList<ScimGroupsResourcesDTO> scimGroupsResourcesDTOArrayList = []
            userGroups.each {
                ScimGroupsResourcesDTO scimGroupsResourcesDTO = new ScimGroupsResourcesDTO()
                def schemas = ["urn:ietf:params:scim:schemas:core:2.0:Group"];
                scimGroupsResourcesDTO.schemas = schemas
                scimGroupsResourcesDTO.id = it.id
                scimGroupsResourcesDTO.externalId = it.id
                scimGroupsResourcesDTO.displayName = it.name
                ScimGroupMetaDTO scimGroupMetaDTO = new ScimGroupMetaDTO()
                scimGroupMetaDTO.lastCreated = it.dateCreated
                scimGroupMetaDTO.lastModified = it.lastUpdated
                scimGroupMetaDTO.resourceType = "Group"
                scimGroupsResourcesDTO.meta = scimGroupMetaDTO
                scimGroupsResourcesDTOArrayList.add(scimGroupsResourcesDTO)
            }
            data = [
                    "schemas" : ["urn:ietf:params:scim:api:messages:2.0:ListResponse"],
                    "totalResults": userGroups.size(),
                    "Resources":scimGroupsResourcesDTOArrayList,
                    "startIndex":1,
                    "itemsPerPage":20
            ]
        }
        data

    }
}
