package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.chessdata.R;
import eu.chessdata.backend.tournamentEndpoint.TournamentEndpoint;
import eu.chessdata.backend.tournamentEndpoint.model.Club;
import eu.chessdata.backend.tournamentEndpoint.model.ClubCollection;
import eu.chessdata.backend.tournamentEndpoint.model.ClubMember;
import eu.chessdata.backend.tournamentEndpoint.model.ClubMemberCollection;
import eu.chessdata.backend.tournamentEndpoint.model.Profile;
import eu.chessdata.backend.tournamentEndpoint.model.ProfileCollection;
import eu.chessdata.backend.tournamentEndpoint.model.Round;
import eu.chessdata.backend.tournamentEndpoint.model.RoundCollection;
import eu.chessdata.backend.tournamentEndpoint.model.SupportObject;
import eu.chessdata.backend.tournamentEndpoint.model.Tournament;
import eu.chessdata.backend.tournamentEndpoint.model.TournamentCollection;
import eu.chessdata.backend.tournamentEndpoint.model.TournamentPlayer;
import eu.chessdata.backend.tournamentEndpoint.model.TournamentPlayerCollection;
import eu.chessdata.data.simplesql.ClubMemberSql;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ClubSql;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.data.simplesql.ProfileSql;
import eu.chessdata.data.simplesql.ProfileTable;
import eu.chessdata.data.simplesql.RoundPlayerSql;
import eu.chessdata.data.simplesql.RoundPlayerTable;
import eu.chessdata.data.simplesql.RoundSql;
import eu.chessdata.data.simplesql.RoundTable;
import eu.chessdata.data.simplesql.TournamentPlayerSql;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.data.simplesql.TournamentSql;
import eu.chessdata.data.simplesql.TournamentTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class TournamentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CREATE_TOURNAMENT = "eu.chessdata.services.action.ACTION_CREATE_TOURNAMENT";
    private static final String ACTION_TOURNAMENT_ADD_PLAYER = "eu.chessdata.services.action.ACTION_TOURNAMENT_ADD_PLAYER";
    private static final String ACTION_SYNCHRONIZE_ALL = "eu.chessdata.services.action.ACTION_SYNCHRONIZE_ALL";
    private static final String ACTION_CREATE_ROUND_PLAYER = "eu.chessdata.services.ACTION_CREATE_ROUND_PLAYER";

    private static final String EXTRA_JSON_TOURNAMENT = "eu.chessdata.services.extra.JSON_TOURNAMENT";
    private static final String EXTRA_TOURNAMENT_SQL_ID = "eu.chessdata.services.EXTRA_TOURNAMENT_SQL_ID";
    private static final String EXTRA_PLAYER_SQL_ID = "eu.chessdata.services.EXTRA_PLAYER_SQL_ID";
    private static final String EXTRA_ROUND_ID = "eu.chessdata.services.EXTRA_ROUND_ID";
    private static final String EXTRA_TOURNAMENT_ID = "eu.chessdata.services.EXTRA_TOURNAMENT_ID";
    private static final String EXTRA_TOURNAMENT_PLAYER_SQL_ID = "eu.chessdata.services.EXTRA_TOURNAMENT_PLAYER_SQL_ID";

    private static TournamentEndpoint sTournamentEndpoint = buildTournamentEndpoint();
    private static GsonFactory sGsonFactory = new GsonFactory();
    private static Context mSynchronizeAllContext;
    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPreferences;
    private ContentResolver mContentResolver;
    private String mIdTokenString;
    private String mProfileId;
    private Long mClubEndpointId;

    public TournamentService() {
        super("TournamentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCreateTournament(Context context, Tournament tournament) {
        String jsonTournament = serializeToJson(tournament);
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_CREATE_TOURNAMENT);
        intent.putExtra(EXTRA_JSON_TOURNAMENT, jsonTournament);
        context.startService(intent);
    }

    public static void startActionCreateRoundPlayer(Context context, String roundId, String tournamentId,
                                                    String tournamentPlayerSqlId) {
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_CREATE_ROUND_PLAYER);
        intent.putExtra(EXTRA_ROUND_ID, roundId);
        intent.putExtra(EXTRA_TOURNAMENT_ID, tournamentId);
        intent.putExtra(EXTRA_TOURNAMENT_PLAYER_SQL_ID, tournamentPlayerSqlId);
        context.startService(intent);
    }

    public static void startActionSynchronizeAll(Context context) {
        mSynchronizeAllContext = context;
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_SYNCHRONIZE_ALL);
        context.startService(intent);
    }

    public static void startActionTournamentAddPlayer(Context context, Long tournamentSqlId, Long
            playerSqlId) {
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_TOURNAMENT_ADD_PLAYER);
        intent.putExtra(EXTRA_TOURNAMENT_SQL_ID, tournamentSqlId);
        intent.putExtra(EXTRA_PLAYER_SQL_ID, playerSqlId);

        context.startService(intent);
    }

    /**
     * creates json from tournament
     *
     * @param tournament
     * @return
     */
    private static String serializeToJson(Tournament tournament) {
        try {
            String jsonTournament = sGsonFactory.toString(tournament);
            return jsonTournament;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create json");
        }
    }

    private static Tournament deserializeFromJson(String jsonString) {
        try {
            return sGsonFactory.fromString(jsonString, Tournament.class);
        } catch (IOException e) {
            throw new IllegalStateException("Not able to deserialize json");
        }
    }

    private static TournamentEndpoint buildTournamentEndpoint() {
        TournamentEndpoint.Builder builder = new TournamentEndpoint.Builder(AndroidHttp
                .newCompatibleTransport(), new AndroidJsonFactory(), null).setRootUrl
                (MyGlobalTools.ROOT_URL);
        return builder.build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            mContentResolver = getContentResolver();
            mIdTokenString = mSharedPreferences.getString(getString(R.string.pref_security_id_token_string), "defaultValue");
            mProfileId = mSharedPreferences.getString(getString(R.string.pref_profile_profileId), "defaultValue");
            long clubSqlId = mSharedPreferences.getLong(getString(R.string
                    .pref_managed_club_sqlId), 0L);
            mClubEndpointId = getClubEndpointId(clubSqlId);

            final String action = intent.getAction();
            if (ACTION_CREATE_TOURNAMENT.equals(action)) {
                final String jsonTournament = intent.getStringExtra(EXTRA_JSON_TOURNAMENT);
                handleActionCreateTournament(jsonTournament);
            } else if (ACTION_TOURNAMENT_ADD_PLAYER.equals(action)) {
                final Long tournamentSqlId = intent.getLongExtra(EXTRA_TOURNAMENT_SQL_ID, -1L);
                final Long playerSqlId = intent.getLongExtra(EXTRA_PLAYER_SQL_ID, -1l);
                handleActionTournamentAddPlayer(tournamentSqlId, playerSqlId);
            } else if (ACTION_SYNCHRONIZE_ALL.equals(action)) {
                handleActionSynchronizeAll();
            } else if (ACTION_CREATE_ROUND_PLAYER.equals(action)) {
                final String roundId = intent.getStringExtra(EXTRA_ROUND_ID);
                final String tournamentId = intent.getStringExtra(EXTRA_TOURNAMENT_ID);
                final String tournamentPlayerSqlId = intent.getStringExtra(EXTRA_TOURNAMENT_PLAYER_SQL_ID);
                handleActionCreateRoundPlayer(roundId, tournamentId, tournamentPlayerSqlId);
            }
        }
    }

    private void handleActionSynchronizeAll() {
        synchronizeClubs();
        synchronizeClubMembers();
        synchronizeProfiles();
        ProfileService.startActionUpdateAllMembersMap(mSynchronizeAllContext);
        synchronizeTournaments();
        synchronizeTournamentPlayers();
        synchronizeRounds();
    }

    private void synchronizeClubMembers() {
        //select all the local clubs
        Uri uri = ClubTable.CONTENT_URI;
        String[] projection = {ClubTable.FIELD_CLUBID};
        int idx_clubId = 0;
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        List<Long> clubIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            clubIds.add(cursor.getLong(idx_clubId));
        }
        cursor.close();
        if (clubIds.size() == 0) {
            Log.d(TAG, "No clubs to sync members for");
            return;
        }
        SupportObject supportObject = new SupportObject();
        supportObject.setMessage("Get club members");
        supportObject.setLongList(clubIds);

        try {
            ClubMemberCollection supportMemberCollection = sTournamentEndpoint.getAllMembers(supportObject).execute();
            List<ClubMember> clubMembers = supportMemberCollection.getItems();
            if (clubMembers.size() == 0) {
                return;
            }
            ClubMember illegalMember = clubMembers.get(0);
            String message = illegalMember.getProfileId();
            String illegalMessage = message.split(":")[0];
            if (illegalMessage.equals("Something is wrong")) {
                Log.e(TAG, "synchronizeClubMembers " + message);
                return;
            }
            Log.d(TAG, "Received members = " + clubMembers.size());
            //wee have the members. Next step is update sql members
            for (ClubMember member : clubMembers) {
                Uri uriMembers = ClubMemberTable.CONTENT_URI;
                String selectionMembers = ClubMemberTable.FIELD_CLUBMEMBERID + " =?";
                String selectionMembersArgs[] = {member.getClubMemberId().toString()};
                Cursor memberCursor = mContentResolver.query(uriMembers, null, selectionMembers, selectionMembersArgs, null);
                int count = memberCursor.getCount();
                if (count > 1) {
                    Log.e(TAG, "More then one member with the same id: " + member.getProfileId());
                    throw new IllegalStateException("More then one member with the same id: " + member.getProfileId());
                } else if (count == 0) {
                    //time to insert the member
                    mContentResolver.insert(ClubMemberTable.CONTENT_URI, ClubMemberTable.getContentValues(new ClubMemberSql(member), false));
                } else if (count == 1) {
                    memberCursor.moveToFirst();
                    int idx_updateStamp = memberCursor.getColumnIndex(ClubTable.FIELD_UPDATESTAMP);
                    long stampLocal = memberCursor.getLong(idx_updateStamp);
                    long stampCloud = member.getUpdateStamp();
                    //time to compare time stamps and decide
                    if (stampLocal < stampCloud) {
                        //update local
                        int rowsUpdated = mContentResolver.update(ClubMemberTable.CONTENT_URI,
                                ClubMemberTable.getContentValues(new ClubMemberSql(member), false),
                                selectionMembers, selectionMembersArgs);
                        if (rowsUpdated != 1) {
                            Log.e(TAG, "You should update only one row");
                            throw new IllegalStateException("You should update only one row");
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. code = 002");
                    } else if (stampLocal == stampCloud) {
                        //(TAG, "Nothing to update for this member");
                    }
                }
                memberCursor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Some error on server side: " + e);
            e.printStackTrace();
        }
    }

    /**
     * gets all profiles from server and updates data locally
     */
    private void synchronizeProfiles() {
        //select all the local members;
        Uri memberUri = ClubMemberTable.CONTENT_URI;
        String[] memberProjection = {ClubMemberTable.FIELD_PROFILEID};
        int idx_profileId = 0;
        Cursor memberCursor = mContentResolver.query(memberUri, memberProjection, null, null, null);
        List<String> profileIds = new ArrayList<>();
        while (memberCursor.moveToNext()) {
            profileIds.add(memberCursor.getString(idx_profileId));
        }
        memberCursor.close();
        if (profileIds.size() == 0) {
            Log.d(TAG, "No profiles to sync");
            return;
        }
        SupportObject supportObject = new SupportObject();
        supportObject.setMessage("Get profiles");
        supportObject.setStringList(profileIds);

        try {
            ProfileCollection profileCollection = sTournamentEndpoint.getProfileListByProfileIdList(supportObject).execute();
            List<Profile> profiles = profileCollection.getItems();
            if (profiles.size() == 0) {
                return;
            }
            Log.d(TAG, "Received profiles: " + profiles.size());

            //wee have the profiles. Next step is to updated the sql profiles
            for (Profile profile : profiles) {
                //update globalMap
                //Map<String,String> profileNames = MyGlobalTools.

                Uri profileUri = ProfileTable.CONTENT_URI;
                String selectionProfile = ProfileTable.FIELD_PROFILEID + " =?";
                String selectionProfileArgs[] = {profile.getProfileId()};
                Cursor profileCursor = mContentResolver.query(profileUri, null, selectionProfile, selectionProfileArgs, null);
                int count = profileCursor.getCount();
                if (count > 1) {
                    Log.e(TAG, "More then one profile with the same id: " + profile.getProfileId());
                    throw new IllegalStateException("More then one profile with the same id: " + profile.getProfileId());
                } else if (count == 0) {
                    //time to insert the profile
                    mContentResolver.insert(profileUri, ProfileTable.getContentValues(new ProfileSql(profile), false));
                } else if (count == 1) {
                    profileCursor.moveToFirst();
                    int idx_updateStamp = profileCursor.getColumnIndex(ClubTable.FIELD_UPDATESTAMP);
                    long stampLocal = profileCursor.getLong(idx_updateStamp);
                    long stampCloud = profile.getUpdateStamp();
                    //time to compare time stamps and decide
                    if (stampLocal < stampCloud) {
                        //update local
                        int rowsUpdated = mContentResolver.update(profileUri,
                                ProfileTable.getContentValues(new ProfileSql(profile), false),
                                selectionProfile, selectionProfileArgs);
                        if (rowsUpdated != 1) {
                            Log.e(TAG, "You should update only one row");
                            throw new IllegalStateException("You should update only one row");
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. code = 003");
                    }
                }
                profileCursor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Some error on server side: " + e);
            e.printStackTrace();
        }

    }

    private void synchronizeTournaments() {
        //select all the local clubs
        Uri uri = ClubTable.CONTENT_URI;
        String[] projection = {ClubTable.FIELD_CLUBID};
        int idx_clubId = 0;
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        List<Long> clubIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            clubIds.add(cursor.getLong(idx_clubId));
        }
        cursor.close();
        if (clubIds.size() == 0) {
            Log.d(TAG, "No clubs to sync tournaments for");
            return;
        }
        SupportObject supportObject = new SupportObject();
        supportObject.setMessage("Get tournaments");
        supportObject.setLongList(clubIds);

        try {
            TournamentCollection tournamentCollection = sTournamentEndpoint.getTournamentsByClubIds(supportObject).execute();
            List<Tournament> tournaments = tournamentCollection.getItems();
            if (tournaments == null) {
                tournaments = new ArrayList<>();
            }
            Log.d(TAG, "Received tournaments = " + tournaments.size());
            for (Tournament tournament : tournaments) {
                Uri uriTournaments = TournamentTable.CONTENT_URI;
                String selectionTournaments = TournamentTable.FIELD_TOURNAMENTID + " =?";
                String selectionTournamentsArgs[] = {tournament.getTournamentId().toString()};
                Cursor tournamentCursor = mContentResolver.query(uriTournaments, null, selectionTournaments, selectionTournamentsArgs, null);
                int count = tournamentCursor.getCount();
                if (count > 1) {
                    Log.e(TAG, "More then one tournament with the same id: " + tournament.getTournamentId());
                    throw new IllegalStateException("More then one tournament with the same id: " + tournament.getTournamentId());
                } else if (count == 0) {
                    //time to insert the tournament
                    mContentResolver.insert(TournamentTable.CONTENT_URI, TournamentTable.getContentValues(new TournamentSql(tournament), false));
                } else if (count == 1) {
                    tournamentCursor.moveToFirst();
                    int idx_updateStamp = tournamentCursor.getColumnIndex(TournamentTable.FIELD_UPDATESTAMP);
                    long stampLocal = tournamentCursor.getLong(idx_updateStamp);
                    long stampCloud = tournament.getUpdateStamp();
                    //time to compare time stamps and decide
                    if (stampLocal < stampCloud) {
                        //update local
                        int rowsUpdated = mContentResolver.update(uriTournaments,
                                TournamentTable.getContentValues(new TournamentSql(tournament), false),
                                selectionTournaments, selectionTournamentsArgs);
                        if (rowsUpdated != 1) {
                            Log.e(TAG, "You should update only one row");
                            throw new IllegalStateException("You should update only one TournamentTable");
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. TournamentTable code = 004");
                    } else if (stampLocal == stampCloud) {
                        //nothing to update for this tournament
                    }
                }
                tournamentCursor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Some error on server side: " + e);
            e.printStackTrace();
        }
    }

    /**
     * gets all the clubs from the server and updates data locally
     */
    private void synchronizeClubs() {
        try {
            ClubCollection collection = sTournamentEndpoint.getAllClubsUserIsMember
                    (mIdTokenString).execute();
            if (collection == null) {
                return;
            }
            List<Club> clubs = collection.getItems();
            if (clubs.size() == 0) {

                return;
            }
            Club illegalClub = clubs.get(0);
            String message = illegalClub.getName();
            String illegalMessage = message.split(":")[0];
            if (illegalMessage.equals("Something is wrong")) {
                Log.e(TAG, message);
                return;
            }

            //Wee have the clubs. next step is to update the clubs in the sqlite
            for (Club club : clubs) {
                Uri uri = ClubTable.CONTENT_URI;
                String selection = ClubTable.FIELD_CLUBID + " =?";
                String selectionArgs[] = {club.getClubId().toString()};

                Cursor cursor = mContentResolver.query(uri, null, selection, selectionArgs, null);
                int count = cursor.getCount();
                if (count > 1) {
                    Log.e(TAG, "More then one club with the same id: " + club.getShortName());
                    throw new IllegalStateException("Should not have more then one club with the same id in sqlite");
                } else if (count == 0) {
                    //time to insert the club in sqlite
                    mContentResolver.insert(ClubTable.CONTENT_URI, ClubTable.getContentValues(new ClubSql(club), false));
                } else if (count == 1) {
                    cursor.moveToFirst();
                    int idx_updateStamp = cursor.getColumnIndex(ClubTable.FIELD_UPDATESTAMP);
                    long stampLocal = cursor.getLong(idx_updateStamp);
                    long stampCloud = club.getUpdateStamp();


                    if (stampLocal < stampCloud) {
                        //time to check time stamp and decide
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ClubTable.FIELD_NAME, club.getName());
                        contentValues.put(ClubTable.FIELD_SHORTNAME, club.getShortName());
                        contentValues.put(ClubTable.FIELD_EMAIL, club.getEmail().getEmail());
                        contentValues.put(ClubTable.FIELD_COUNTRY, club.getCountry());
                        contentValues.put(ClubTable.FIELD_CITY, club.getCity());
                        contentValues.put(ClubTable.FIELD_HOMEPAGE, club.getHomePage().getValue());
                        contentValues.put(ClubTable.FIELD_DESCRIPTION, club.getDescription());
                        contentValues.put(ClubTable.FIELD_UPDATESTAMP, club.getUpdateStamp());

                        String updateSelection = ClubTable.FIELD__ID + " =?";
                        Long myId = cursor.getLong(0);
                        String selectionAgs[] = {myId.toString()};
                        int rowsUpdated = mContentResolver.update(
                                ClubTable.CONTENT_URI,
                                contentValues,
                                updateSelection,
                                selectionArgs
                        );
                        if (rowsUpdated != 1) {
                            Log.e(TAG, "You should update only one row");
                            throw new IllegalStateException("You should update only one row");
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. code = 001");
                    } else if (stampLocal == stampCloud) {
                        //Nothing to update. club up to date!
                    }
                }
                cursor.close();
            }

        } catch (IOException e) {
            Log.e(TAG, "Not able to send request synchronizeClubs");
            throw new IllegalStateException("Not able to send/process request to/on server side"
                    + e);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCreateTournament(String jsonTournament) {
        Tournament tournament = deserializeFromJson(jsonTournament);

        if (tournament != null && mClubEndpointId != null) {
            try {
                //set the id
                tournament.setClubId(mClubEndpointId);
                //insert tournament in datastore
                Tournament vipTournament = sTournamentEndpoint.create(mIdTokenString, tournament)
                        .execute();
                if (vipTournament != null) {
                    String description = vipTournament.getDescription();
                    String message = description.split(":")[0];
                    if (message.equals("Not created")) {
                        Log.d(TAG, "Something happened: " + description);
                        return;
                    }
                    //insert in sqlite
                    TournamentSql tournamentSql = new TournamentSql(vipTournament);
                    Uri newUri = mContentResolver.insert(TournamentTable.CONTENT_URI,
                            TournamentTable.getContentValues(tournamentSql, false));
                    Log.d(TAG, "Tournament uri: " + newUri.toString());

                }
            } catch (IOException e) {
                Log.d(TAG, "Not able to create vipTournament from: " + tournament);
            }
        }
    }

    /**
     * It creates the sqlite tournament player first and then tries to sink it with the cloud
     * endpoints
     *
     * @param tournamentSqlId
     * @param playerSqlId
     */
    private void handleActionTournamentAddPlayer(Long tournamentSqlId, Long playerSqlId) {

        Long tournamentId = MyGlobalTools.getTournamentCloudIdBySqlId(tournamentSqlId,
                mContentResolver);
        String profileId = MyGlobalTools.getProfileCloudIdBySqlId(playerSqlId, mContentResolver);
        String profileName = MyGlobalTools.getNameByProfileId(profileId);

        //create the sql tournament first
        TournamentPlayerSql tournamentPlayerSql = new TournamentPlayerSql(tournamentId,
                profileId, profileName);
        Uri newUri = mContentResolver.insert(TournamentPlayerTable.CONTENT_URI,
                TournamentPlayerTable.getContentValues(tournamentPlayerSql, false));

        MyGlobalTools.syncLocalTournamentPlayers(mContentResolver, mIdTokenString);
    }

    private Long getClubEndpointId(long clubSqlId) {
        Uri clubUri = ClubTable.CONTENT_URI;
        String[] projection = {ClubTable.FIELD__ID, ClubTable.FIELD_CLUBID};
        int COL_CLUBID = 1;
        String selection = ClubTable.FIELD__ID + " = ?";
        String stringClubSqlId = Long.toString(clubSqlId);
        String[] selectionArguments = {stringClubSqlId};

        Cursor cursor = mContentResolver.query(clubUri, projection, selection,
                selectionArguments, null);
        while (cursor.moveToNext()) {
            long endPointId = cursor.getLong(COL_CLUBID);
            return endPointId;
        }
        int count = cursor.getCount();
        Log.d(TAG, "Not able to find club: getClubEndpointId: " + clubSqlId);
        return -1L;

    }

    private void synchronizeTournamentPlayers() {
        //select all the local tournaments
        Uri uri = TournamentTable.CONTENT_URI;
        String[] projection = {TournamentTable.FIELD_TOURNAMENTID};
        int idx_tournamentId = 0;
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        List<Long> tournamentIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            tournamentIds.add(cursor.getLong(idx_tournamentId));
        }
        cursor.close();
        if (tournamentIds.size() == 0) {
            return;
        }
        SupportObject supportObject = new SupportObject();
        supportObject.setMessage("Get tournament players");
        supportObject.setLongList(tournamentIds);

        try {
            TournamentPlayerCollection playerCollection = sTournamentEndpoint.getTournamentPlayersByTournamentIds(supportObject).execute();
            List<TournamentPlayer> tournamentPlayers = playerCollection.getItems();
            if (tournamentPlayers == null) {
                tournamentPlayers = new ArrayList<>();
            }
            Log.d(TAG, "Received players = " + tournamentPlayers.size());
            for (TournamentPlayer player : tournamentPlayers) {
                Uri playerUri = TournamentPlayerTable.CONTENT_URI;
                String playerSelection = TournamentPlayerTable.FIELD_TOURNAMENTPLAYERID + " =?";
                String playerArgs[] = {player.getTournamentPlayerId().toString()};
                Cursor playerCursor = mContentResolver.query(playerUri, null, playerSelection, playerArgs, null);
                int count = playerCursor.getCount();
                if (count > 1) {
                    String problems = "More then one player with the same id: " + player.getTournamentPlayerId();
                    Log.e(TAG, problems);
                    throw new IllegalStateException(problems);
                } else if (count == 0) {
                    //time to insert the tournament
                    mContentResolver.insert(playerUri, TournamentPlayerTable.getContentValues(new TournamentPlayerSql(player), false));
                } else if (count == 1) {
                    playerCursor.moveToFirst();
                    int idx_updateStamp = playerCursor.getColumnIndex(TournamentPlayerTable.FIELD_UPDATESTAMP);
                    long stampLocal = playerCursor.getLong(idx_updateStamp);
                    long stampCloud = player.getUpdateStamp();
                    //time to compare time stamps and decide
                    if (stampLocal < stampCloud) {
                        //update local
                        int rowsUpdated = mContentResolver.update(playerUri,
                                TournamentPlayerTable.getContentValues(new TournamentPlayerSql(player), false),
                                playerSelection, playerArgs);
                        if (rowsUpdated != 1) {
                            String problem = "You should update only one tournamentPlayerId: " + player.getTournamentPlayerId();
                            Log.e(TAG, problem);
                            throw new IllegalStateException(problem);
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. Update cloud tournament player");
                    } else if (stampLocal == stampCloud) {
                        //nothing to update for this player
                    }
                }
                playerCursor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Some error on server side: " + e);
            e.printStackTrace();
        }
    }

    private void synchronizeRounds() {
        //select all the local tournaments
        Uri uri = TournamentTable.CONTENT_URI;
        String[] projection = {TournamentTable.FIELD_TOURNAMENTID};
        int idx_tournamentId = 0;
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        List<Long> tournamentIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            tournamentIds.add(cursor.getLong(idx_tournamentId));
        }
        cursor.close();
        if (tournamentIds.size() == 0) {
            return;
        }
        SupportObject supportObject = new SupportObject();
        supportObject.setMessage("Get tournament players");
        supportObject.setLongList(tournamentIds);
        try {
            RoundCollection roundCollection = sTournamentEndpoint.getRoundsByTournamentIds(supportObject).execute();
            List<Round> rounds = roundCollection.getItems();
            if (rounds == null) {
                rounds = new ArrayList<>();
            }
            Log.d(TAG, "Received rounds = " + rounds.size());
            for (Round round : rounds) {
                Uri roundUri = RoundTable.CONTENT_URI;
                String roundSelection = RoundTable.FIELD_ROUNDID + " =?";
                String roundArgs[] = {round.getRoundId().toString()};
                Cursor roundCursor = mContentResolver.query(roundUri, null, roundSelection, roundArgs, null);
                int count = roundCursor.getCount();
                if (count > 1) {
                    String problems = "More then one round with the same id";
                    Log.e(TAG, problems);
                    throw new IllegalStateException(problems);
                } else if (count == 0) {
                    //time to insert round
                    mContentResolver.insert(roundUri, RoundTable.getContentValues(new RoundSql(round), false));
                } else if (count == 1) {
                    roundCursor.moveToFirst();
                    int idx_updateStamp = roundCursor.getColumnIndex(RoundTable.FIELD_UPDATESTAMP);
                    long stampLocal = roundCursor.getLong(idx_updateStamp);
                    long stampCloud = round.getUpdateStamp();
                    //time to compare stamps and decide
                    if (stampLocal < stampCloud) {
                        //update local
                        int rowsUpdated = mContentResolver.update(roundUri,
                                RoundTable.getContentValues(new RoundSql(round), false),
                                roundSelection, roundArgs);
                        if (rowsUpdated != 1) {
                            String problem = "You should update only one roundId" + round.getRoundId();
                            Log.e(TAG, problem);
                            throw new IllegalStateException(problem);
                        }
                    } else if (stampLocal > stampCloud) {
                        Log.e(TAG, "Please implement this. Update cloud round");
                    }
                }
                roundCursor.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Some error on server side: " + e);
            e.printStackTrace();
        }
    }

    public void handleActionCreateRoundPlayer(String roundId,
                                              String tournamentId,
                                              String tournamentPlayerSqlId) {
        Log.d(TAG, "handleActionCreateRoundPlayer: roundId=" + roundId + " tournamentId=" + tournamentId + " tournamentPlayerSqlId=" + tournamentPlayerSqlId);
        if (!MyGlobalTools.profileCanManageClubByTournamentId(mContentResolver, mProfileId, tournamentId)) {
            return;
        }

        if (MyGlobalTools.playerIsPresentInRound(mContentResolver, roundId, tournamentPlayerSqlId)) {
            return;
        }
        Log.d(TAG, "Player not present wee are admin and wee should at it");
        Long lRoundId = Long.parseLong(roundId);
        String profileId = MyGlobalTools.getProfileIdByTournamentPlayerSqlId(mContentResolver, tournamentPlayerSqlId);
        String name = MyGlobalTools.getNameByProfileId(profileId);
        RoundPlayerSql roundPlayerSql = new RoundPlayerSql(lRoundId, mProfileId, name);

        Uri newUri = mContentResolver.insert(
                RoundPlayerTable.CONTENT_URI,
                RoundPlayerTable.getContentValues(roundPlayerSql,false)
        );
        MyGlobalTools.syncLocalRoundPlayers(mContentResolver, mIdTokenString);
        //TODO finish this?
    }
}
