package grails.plugins.scim.dto

class ScimResponseDTO {

    public int totalResults
    public int itemsPerPage
    public int startIndex
    public ArrayList<String> schemas
//    @JsonProperty("Resources")
    public ArrayList<ScimResourceDTO> resources
}


