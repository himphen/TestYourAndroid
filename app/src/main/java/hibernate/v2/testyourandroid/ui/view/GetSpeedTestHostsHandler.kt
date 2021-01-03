package hibernate.v2.testyourandroid.ui.view

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

object GetSpeedTestHostsHandler {

    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseClient(inputStream: InputStream): Client? {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                if (parser.name == "client") {
                    val lat = parser.getAttributeValue(ns, "lat")
                    val lon = parser.getAttributeValue(ns, "lon")
                    return Client(lat, lon)
                }
            }
        }

        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseServer(inputStream: InputStream): List<Server> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            val entries = mutableListOf<Server>()
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                if (parser.name == "servers") {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }
                        if (parser.name == "server") {
                            val id = parser.getAttributeValue(ns, "id").toInt()
                            val url = parser.getAttributeValue(ns, "url")
                            val lat = parser.getAttributeValue(ns, "lat").toDouble()
                            val lon = parser.getAttributeValue(ns, "lon").toDouble()
                            val name = parser.getAttributeValue(ns, "name")
                            val country = parser.getAttributeValue(ns, "country")
                            val cc = parser.getAttributeValue(ns, "cc")
                            val sponsor = parser.getAttributeValue(ns, "sponsor")
                            val host = parser.getAttributeValue(ns, "host")

                            entries.add(Server(id, url, lat, lon, name, country, cc, sponsor, host))

                            parser.nextTag()
                        }
                    }
                }
            }
            return entries
        }
    }
}

data class Server(
    val id: Int, val uploadAddress: String,
    val lat: Double, val lon: Double,
    val name: String, val country: String,
    val cc: String, val sponsor: String,
    val host: String
)

data class Client(val lat: String, val lon: String)