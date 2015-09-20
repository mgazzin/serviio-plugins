import org.serviio.library.metadata.*
import org.serviio.library.online.*
import org.serviio.util.*

/********************************************************************
 * Content URL extractor for Deutsche Welle
 * 
 * @author  mgazzin
 *
 * Must be installed as a Video WebResource
 * URLs: 
 *    Live TV: http://www.dw.com/en/media-center/live-tv/s-100825
 * Version:
 *    V1: 23 August 2015 - Initial Release
 *
 ********************************************************************/
 
class DWLive extends WebResourceUrlExtractor {
     
    final VALID_FEED_URL = '^http://www.dw.com/en/media-center/live-tv/s-100825.*$'
    final METAFILE_GENERATOR = 'http://www.metafilegenerator.de/DWelle/tv-asia/flv/tv.smil'
    // Catching the following line
    // <video bandwidth="medium" src="dw_fl_tv-asiaplus_m@s12813?auth=daEa7aLdtdxbedGcMczbtbQdNa3dWb4dHcO-bv2CXj-b4-vFvxsEtCroFCv&aifp=001" />
    final MFG_PATTERN_MEDIUM = '<video bandwidth=\"medium\"[^s]+src=\"([^<]+)\" />'
    final MFG_PATTERN_HIGH = '<video bandwidth=\"high\"[^s]+src=\"([^<]+)\" />'
    final MFG_PATTERN_LOW = '<video bandwidth=\"low\"[^s]+src=\"([^<]+)\" />'
    final USER_AGENT = 'Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)'
    final SWF_URL = 'http://www.dw.com/js/jwplayer/player.swf'
    final DW_THUMB_URL = 'http://www.dw.com/image/0%2C%2C18452774_302%2C00.jpg'
    final DEBUG = 1



    String getExtractorName() {
        return 'DWLive'
    }
    
    boolean extractorMatches(URL feedUrl) {
        return feedUrl ==~ VALID_FEED_URL
    }
  
    public int getVersion() {
                return 1;
    }
 
    @Override
    WebResourceContainer extractItems(URL resourceUrl, int maxItemsToRetrieve) {
        maxItemsToRetrieve = 1

        List<WebResourceItem> items = []


        def rUrl = "rtmp://cp135358.live.edgefcs.net/live/"

        WebResourceItem item = new WebResourceItem(title: 'DWLive', additionalInfo: ['resourceUrl':rUrl, 'thumbUrl':DW_THUMB_URL])
        items.add(item)
        def containerThumbnailUrl = items?.find { it -> it.additionalInfo['thumbUrl'] != null }?.additionalInfo['thumbUrl']
        return new WebResourceContainer(items: items, thumbnailUrl: containerThumbnailUrl)
    }


    @Override
    protected ContentURLContainer extractUrl(WebResourceItem wrItem, PreferredQuality requestedQuality) {

        def keyDW  
        def matcher 
        
        log ("[DWLive] Entered extractUrl");         
        if ( DEBUG > 10 ) { println  ("[DWLive] Entered extractUrl"); } 
        else if ( DEBUG > 0 ) { log ("[DWLive] Entered extractUrl"); }
  
        def metaUrl = new URL(METAFILE_GENERATOR);
        if ( DEBUG > 10 ) { println "[DWLive] Metafile Gen url: " + metaUrl; }
        else if ( DEBUG > 0 ) { log ("[DWLive] Metafile Gen url: " + metaUrl); }

        def metaResponse = openURL(metaUrl, USER_AGENT);
        if ( DEBUG > 10 ) { println "[DWLive] Get web page: "+ metaResponse; }
        else if ( DEBUG > 0 ) { log ("[DWLive] Get web page: "+ metaResponse); }
   
   
        if ( DEBUG > 10 ) { println "[DWLive] requested quality: "+ requestedQuality; }
        else if ( DEBUG > 0 ) { log ("[DWLive] requested quality: "+ requestedQuality); }

        switch (requestedQuality){
            case requestedQuality.HIGH:
                matcher = metaResponse =~ MFG_PATTERN_HIGH;
                break
            case requestedQuality.MEDIUM:
                matcher = metaResponse =~ MFG_PATTERN_MEDIUM;
                break
            case requestedQuality.LOW:
                matcher = metaResponse =~ MFG_PATTERN_LOW;
                break
        }

        matcher.each { matched, kDW ->
                       keyDW = kDW;
                       if ( DEBUG > 10) { println "[DWLive] Key found :" + keyDW; }
                       else if ( DEBUG > 0 ) { log("[DWLive] Key found :" + keyDW); }
        } 

        def resourceUrl = "rtmp://cp135358.live.edgefcs.net/live/"
        def cacheKey = "DW_" + keyDW
        def playPath = keyDW
        def swfUrl = SWF_URL
        def swfVfy = 1

        
        def contentUrl = resourceUrl + keyDW + " playpath=" + keyDW + " swfUrl=" + SWF_URL + " swfVfy=1"
        if ( DEBUG > 10 ) { println "[DWLive] contentUrl: "+ contentUrl; }
        else if ( DEBUG > 0 ) { log ("[DWLive] contentUrl: "+ contentUrl); }
        
        return new ContentURLContainer(fileType: MediaFileType.VIDEO, contentUrl: contentUrl, thumbnailUrl: DW_THUMB_URL, expiresImmediately: true, cacheKey: cacheKey)
    }
    
    static void main(args) {
        // this is just to test
        DWLive extractor = new DWLive()
        
        assert extractor.extractorMatches( new URL("http://www.dw.com/en/media-center/live-tv/s-100825") )
        assert !extractor.extractorMatches( new URL("http://google.com/feeds/api/standardfeeds/top_rated?time=today") )
        
        WebResourceContainer container = extractor.extractItems( new URL("http://www.dw.com/en/media-center/live-tv/s-100825"), 1)
        println container
        ContentURLContainer result = extractor.extractUrl(container.getItems()[1], PreferredQuality.MEDIUM)
        println result
        ContentURLContainer result2 = extractor.extractUrl(container.getItems()[1], PreferredQuality.HIGH)
        println result2

        
    }  
 }
