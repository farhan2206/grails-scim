package grails.plugins.scim

class ScimUtil {

    /*
    *  Get Username from filter?userName eq ''
    */
    static String getuserNamefromFilter(String filter) {
        String[] words = filter.split(" ")
        removeFirstandLast(words[2])
    }

    // Function to remove the first and
    // the last character of a string
    static String removeFirstandLast(String str) {
        // Removing first and last character
        // of a string using substring() method
        str = str.substring(1, str.length() - 1)
        // Return the modified string
        str
    }

}
