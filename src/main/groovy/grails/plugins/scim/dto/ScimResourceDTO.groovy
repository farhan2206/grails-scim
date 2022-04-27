package grails.plugins.scim.dto


class ScimResourceDTO {

    public ArrayList<String> schemas
    public String id
    public String externalId
    public ScimMetaDTO meta
    public String userName
    public String nickName
    public ScimNameDTO name
    public String displayName
    public String profileUrl
    public String title
    public String timezone
    public boolean active
    public ArrayList<ScimEmailDTO> emails
    public ArrayList<ScimPhotoDTO> photos
    public ArrayList<ScimGroupDTO> groups
}
