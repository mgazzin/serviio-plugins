# serviio-plugins
This repository contains my Serviio scripts developed in Groovy

In this repository the following plugins are included:

- DWLive.groovy


---------------------------------------------------------------------


You may need to add the following statement to your profile.xml for the profile you are using:

```xml
  <OnlineTranscoding>
                <Video targetContainer="mpegts" targetVCodec="mpeg2video" maxVBitrate="30000" targetACodec="ac3" aBitrate="384">
                       <Matches container="flv" vCodec="h264" />
                </Video>      
  </OnlineTranscoding>
```




