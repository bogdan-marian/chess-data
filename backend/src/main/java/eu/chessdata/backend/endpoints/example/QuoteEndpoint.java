package eu.chessdata.backend.endpoints.example;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.googlecode.objectify.Key;

import eu.chessdata.backend.entities.Quote;
import eu.chessdata.backend.tools.MySecurityService;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by bogdan on 25/11/2015.
 */
@Api(
        name = "quoteEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class QuoteEndpoint {

    @ApiMethod(name = "getRandomQuote")
    public Quote getRandomQuote(@Named("idToken") @Nullable String idToken) {
        Quote quote = new Quote();
        quote.setWho("Charles Buxton");
        String what = "In life, as in chess, forethought wins";
        if (idToken != null) {
            quote.setWhat(MySecurityService.getGoogleObjectId(idToken));
        } else {
            quote.setWhat("(no user yet) - " + what);
        }
        return quote;
    }

    @ApiMethod(name = "createNewQuote")
    public Quote createNewQuote(@Named("idToken") String idToken) {

        final Key<Quote> quoteKey = factory().allocateId(Quote.class);
        final long quoteId = quoteKey.getId();

        Quote quote = new Quote(quoteId, "new person", "Some famous quote");
        ofy().save().entity(quote).now();

        return quote;
    }

    @ApiMethod(name = "findAndIncrementCount")
    public Quote findAndIncrementCount(Quote quote){
        Key quoteKey = Key.create(Quote.class,quote.getQuoteId());
        Quote dbQuote = (Quote) ofy().load().key(quoteKey).now();
        if (dbQuote != null){
            Long count = dbQuote.getCounter();
            dbQuote.setCounter(++count);
            ofy().save().entity(dbQuote).now();
            return dbQuote;
        }
        throw new IllegalStateException("You are trying to do something strange");
    }
}
