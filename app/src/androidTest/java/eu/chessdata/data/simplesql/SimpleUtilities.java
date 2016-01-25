package eu.chessdata.data.simplesql;

import android.test.AndroidTestCase;

import java.util.Date;

/**
 * Created by bogda on 07/12/2015.
 */
public class SimpleUtilities extends AndroidTestCase{
    static ProfileSql createProfileVipValues(){
        ProfileSql vip = new ProfileSql();
        vip.profileId = "profileVip1";
        vip.email = "testmail@mail.com";
        vip.name = "Test Profile Name";
        Date dateValue = new Date();
        long date = dateValue.getTime();
        vip.dateOfBirth = date;
        vip.elo = 0;
        vip.altElo = 0;
        vip.dateCreated = date;
        vip.updateStamp = date;
        return vip;
    }

    static void compareProfilesNoId(ProfileSql profileA, ProfileSql profileB){
        assertTrue("Profile not the same ", profileA.profileId.equals(profileB.profileId));
        assertTrue("email not the same ", profileA.email.equals(profileB.email));
        assertTrue("name not the same ", profileA.name.equals(profileB.name));
        assertTrue("dateOfBirth not the same", profileA.dateOfBirth == profileB.dateOfBirth);
        assertTrue("elo not the same ", profileA.elo == profileB.elo);
        assertTrue("dateCreate not the same ", profileA.dateCreated == profileB.dateCreated);
        assertTrue("updateStamp not the same ", profileA.updateStamp == profileB.updateStamp);
    }

    static void compareTournamentsNoId(TournamentSql tournamentA, TournamentSql tournamentB){
        assertTrue("tournamentId not the same ", tournamentA.getTournamentId().equals(
            tournamentB.getTournamentId()));
        assertTrue("clubId not the same ", (long)tournamentA.getClubId() ==
                (long)tournamentB.getClubId());
        assertTrue("name not the same", tournamentA.getName().equals(tournamentB.getName()));
        assertTrue("description not the same", tournamentA.getDescription()
                .equals(tournamentB.getDescription()));
    }

    static ClubSql createClubVipValues(){
        ClubSql clubSql = new ClubSql();
        clubSql.clubId = 101L;
        clubSql.name = "debug1";
        clubSql.shortName = "debug1";
        clubSql.email = "debug1";
        clubSql.country = "debug1";
        clubSql.city = "debug1";
        clubSql.homePage = "debug1";
        clubSql.description = "debug1";

        Date dateValue = new Date();
        long date = dateValue.getTime();

        clubSql.dateCreated = date;
        clubSql.updateStamp = date;

        return clubSql;
    }

    static TournamentSql createTournamentVipValues(){
        long time = (new Date()).getTime();
        TournamentSql tournamentSql = new TournamentSql();
        tournamentSql.setTournamentId(201L);
        tournamentSql.setClubId(201L);
        tournamentSql.setName("debug2");
        tournamentSql.setDescription("debug2");
        tournamentSql.setTotalRounds(201);
        tournamentSql.setStartDate(time);
        tournamentSql.setEndDate(time);
        tournamentSql.setLocation("Liege");
        tournamentSql.setDateCreated(time);
        tournamentSql.setUpdateStamp(time);
        return tournamentSql;
    }

    static void compareClubsNoId(ClubSql clubA, ClubSql clubB){
        //TODO finish implementing this compareson
        assertTrue("Profile not the same ", clubA.clubId.equals(clubB.clubId));
        assertTrue("email not the same ", clubA.email.equals(clubB.email));
        assertTrue("name not the same ", clubA.name.equals(clubB.name));
        assertTrue("dateCreate not the same ", clubA.dateCreated == clubB.dateCreated);
        assertTrue("updateStamp not the same ", clubA.updateStamp == clubB.updateStamp);
    }
}
