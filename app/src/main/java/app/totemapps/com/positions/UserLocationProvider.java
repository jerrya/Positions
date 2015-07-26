package app.totemapps.com.positions;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class UserLocationProvider extends ItemProvider {

    @Override
    public Item parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        boolean stop = false;
        int eventType;
        String openTag = null;
        String id = null;

        String sender = "";
        String latitude = "";
        String longitude = "";
        String relationshipStatus = "";

        while(!stop) {
            eventType = parser.next();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    openTag = parser.getName();
                    if("geoloc".equals(openTag)) {
                        id = parser.getAttributeValue("", "id");
                    }
                    break;
                case XmlPullParser.TEXT:
                    if("sender".equals(openTag)) {
                        sender = parser.getText();
                    } else if("lat".equals(openTag)) {
                        latitude = parser.getText();
                    } else if ("lon".equals(openTag)) {
                        longitude = parser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    stop = "geoloc".equals(parser.getName());
                    openTag = null;
                    break;
            }
        }
//        Log.d("Provider", "Logged: " + sender + ", " + latitude + ", " + longitude);
        return new UserLocation(sender, latitude, longitude);
    }
}
