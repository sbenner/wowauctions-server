import com.heim.wowauctions.common.persistence.models.Realm;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.heim.wowauctions.common.utils.AuctionUtils.lookupRealmConnections;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sbenner on 04/07/2017.
 */

@RunWith(SpringRunner.class)
public class TestRealmsParser {

    @Test
    public void testRealmsParser() throws Exception {
        String fl =
                IOUtils.toString(
                        this.getClass().getResourceAsStream("realms.json"),
                        "UTF-8"
                );
        if (!StringUtils.isEmpty(fl)) {
            List<Realm> realmList = AuctionUtils.parseRealms(fl);
            assertTrue(realmList.size() > 0);

            assertFalse(lookupRealmConnections("aegwinn", "ursin", realmList));

            assertTrue(lookupRealmConnections("aegwynn", "bonechewer", realmList));

            lookupRealmConnections("andorhal", realmList);
        }


    }


}
