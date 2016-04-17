package eu.chessdata.tools;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.chessdata.data.simplesql.ProfileTable;

/**
 * When you have time you should delete the usage of this class.
 *
 * Created by bogdan on 10/12/2015.
 */
@Deprecated
public class Params {
    private @NonNull Uri uri;
    private @Nullable String[] projection = null;
    private @Nullable String selection = null;
    private @Nullable String[] selectionArgs = null;
    private @Nullable String sortOrder = null;

    private Params(){}
    public static Params getProfileById(String id){
        Params params = new Params();

        String[] mSelectionArgs = {""};

        String mSelectionClause = ProfileTable.FIELD_PROFILEID + " = ?";
        mSelectionArgs[0]= id;

        String mSortOrder =  ProfileTable.FIELD__ID +" ASC";

        params.uri = ProfileTable.CONTENT_URI;
        params.selection = mSelectionClause;
        params.selectionArgs = mSelectionArgs;

        return params;
    }

    //generated getters
    @NonNull
    public Uri getUri() {
        return uri;
    }

    @Nullable
    public String[] getProjection() {
        return projection;
    }

    @Nullable
    public String getSelection() {
        return selection;
    }

    @Nullable
    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    @Nullable
    public String getSortOrder() {
        return sortOrder;
    }
}
