package eu.chessdata.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bogda on 29/11/2015.
 */
public class MyGlobalSharedObjects {
    public static final String ROOT_URL = "https://chess-data.appspot.com/_ah/api/";
    public static Map<String,Long>managedClubs;

    /**
     * first string is the profile id from datastore
     * second string is the name of the profile
     */
    public static Map<String,String> memberSqlIdToProfileName = new HashMap<>();

    public static void addToMembersSqlIdToProfileName(String id,String name){
        memberSqlIdToProfileName.put(id,name);
    }
    public static String getNameByProfileId(String profileId){
        return memberSqlIdToProfileName.get(profileId);
    }
}
