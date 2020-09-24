# SunsetWx Binding

The [SunsetWx](http://www.sunsetwx.com) binding is used predict quality of the upcoming sunrise and sunset.

##Overview

The SunsetWx service is different from most sunrise and sunset applications.  Most sunrise and sunset applications provide the time of day when the sunrise and sunset occurs.  In contrast, SunsetWx actually predicts the expected quality of the sunrise and sunset.  They do that by running some very heavy duty algorithms on multiple weather models and weather data sources. From the results of those algorithms, they produce maps on their web site, as well as expose the algorithm's output through their *sunburst* API. It's much akin to forecasting the weather, but with a sharp focus on predicting the quality of the sunrise or sunset.

## Registering with SunsetWx

Here's an example of how to register your email address and password with SunsetWx so that you can access their API.

```
curl -X "POST" -d "email=your@emailaddress.com" -d "password=yoursecret" -d "key=Jiukh87LbGFDL1EW877GN" "https://sunburst.sunsetwx.com/v1/register"
```

Note that the above key is a dummy key.  It won't work.  You need to request your own key from SunsetWx.com before using the above command.

## Thing Configuration

Automated discovery creates a sunrise and a sunset thing, then attempts to determine your geolocation from your IP address.  The geolocation, as well as other parameters, can be changed in the thing configuration.

#### Manual Thing Creation

Things can be manually created in the *PaperUI* or *HABmin*, or by placing a *.things* file in the *conf/things* directory.  See example below.

#### Binding Dependencies

The GlobalCache binding uses the **transform** binding to map commands to IR and serial codes.  See example below.

## Channels

*Channels* follow a naming convention that relates to the physical configuration of the Global Cache device -- specifically the **module** and **connector** numbers.  For example, the channel name **m2c3** refers to connector 3 on module 2.



## Example Configuration

### Items File

```
String SunsetQuality                "Sunset Quality [%s]"                       <sunset>        { channel="sunsetwx:sunset:local:quality" }
Number SunsetQualityPercent         "Sunset Quality Percent [%.1f %%]"          <sunset>        { channel="sunsetwx:sunset:local:qualityPercent" }
Number SunsetQualityValue           "Sunset Quality Value [%.1f]"               <sunset>        { channel="sunsetwx:sunset:local:qualityValue" }
DateTime SunsetLastUpdated          "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunset:local:lastUpdated"}
DateTime SunsetImportedAt           "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunset:local:importedAt"}
DateTime SunsetValidAt              "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunset:local:validAt"}
String SunsetLocale                 "Locale [%s]"                                               { channel="sunsetwx:sunset:local:locale"}
String SunsetRegion                 "Region [%s]"                                               { channel="sunsetwx:sunset:local:region"}
String SunsetCountry                "Country [%s]"                                              { channel="sunsetwx:sunset:local:country"}
String SunsetSource                 "Source [%s]"                                               { channel="sunsetwx:sunset:local:source"}
String SunsetRawResponse            "Raw JSON Response [%s]"                                    { channel="sunsetwx:sunset:local:rawResponse" }
DateTime SunsetLastReportTime       "Date [%1$tm/%1$td/%1$tY %1$tT]"            <calendar>      { channel="sunsetwx:sunset:local:lastReportTime"}

String SunriseQuality               "Sunrise Quality [%s]"                      <sunrise>       { channel="sunsetwx:sunrise:local:quality" }
Number SunriseQualityPercent        "Sunrise Quality Percent [%.1f %%]"         <sunrise>       { channel="sunsetwx:sunrise:local:qualityPercent" }
Number SunriseQualityValue          "Sunrise Quality Value [%.1f]"              <sunrise>       { channel="sunsetwx:sunrise:local:qualityValue" }
DateTime SunriseLastUpdated         "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunrise:local:lastUpdated"}
DateTime SunriseImportedAt          "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunrise:local:importedAt"}
DateTime SunriseValidAt             "Date [%1$tA, %1$tm/%1$td/%1$tY %1$tT]"     <calendar>      { channel="sunsetwx:sunrise:local:validAt"}
String SunriseLocale                "Locale [%s]"                                               { channel="sunsetwx:sunrise:local:locale"}
String SunriseRegion                "Region [%s]"                                               { channel="sunsetwx:sunrise:local:region"}
String SunriseCountry               "Country [%s]"                                              { channel="sunsetwx:sunrise:local:country"}
String SunriseSource                "Source [%s]"                                               { channel="sunsetwx:sunrise:local:source"}
String SunriseRawResponse           "Raw JSON Response [%s]"                                    { channel="sunsetwx:sunrise:local:rawResponse" }
DateTime SunriseLastReportTime      "Date [%1$tm/%1$td/%1$tY %1$tT]"            <calendar>      { channel="sunsetwx:sunrise:local:lastReportTime"}
```

 
### Sitemap File

```
Frame label="SunsetWx Sunrise Forcast" {
        Text item=SunriseQuality label="Quality [%s]"
        Text item=SunriseQualityPercent label="Quality Percent [%.0f %%]"
        Text item=SunriseValidAt label="Conditions Will Occur At [%1$tm/%1$td/%1$tY %1$tl:%1$tM %1$tp]" icon="clock"
        Text item=SunriseLastReportTime label="Last Report At [%1$tm/%1$td/%1$tY %1$tl:%1$tM %1$tp]" icon="clock"
        Text item=SunriseLocale label="Locale [%s]"
}
Frame label="SunsetWx Sunset Forcast" {
        Text item=SunsetQuality label="Quality [%s]"
        Text item=SunsetQualityPercent label="Quality Percent [%.0f %%]"
        Text item=SunsetValidAt label="Conditions Will Occur At [%1$tm/%1$td/%1$tY %1$tl:%1$tM %1$tp]" icon="clock"
        Text item=SunsetLastReportTime label="Last Report At [%1$tm/%1$td/%1$tY %1$tl:%1$tM %1$tp]" icon="clock"
        Text item=SunsetLocale label="Locale [%s]"
}
```

### Rules file

```
rule "Great Sunrise Alert"
when
    Item SunriseQuality changed to Great or
    Item TestSunriseAlertRule received command ON
then
    val RFN = "rule:sunrise-alert"

    var String modelType = "sunrise"
    var SimpleDateFormat df = new SimpleDateFormat( "MM/dd/YYYY hh:mm a" )
    var String validAt = df.format( (SunriseValidAt.state as DateTimeType).calendar.timeInMillis )
    var String lastUpdated = df.format( (SunriseLastUpdated.state as DateTimeType).calendar.timeInMillis )
    var String percent = String.format("%.1f", (SunriseQualityPercent.state as DecimalType).floatValue())
    var String region = (SunriseRegion.state as StringType).toString()
    var String website = "http://www.sunsetwx.com/sunrise/sunrise_et.png"
    
    val String mailTo = "mark@hilbush.com"
    var String subject = "ALERT: Great " + modelType + " predicted in " + region
    
    var String body = ""
    body = body + "Great " + modelType + " predicted in " + region + "!\n\n"
    body = body + "Quality Percent is " + percent + "%\n"
    body = body + "Conditions predicted to occur at " + validAt + "\n"
    body = body + "Model last updated at " + lastUpdated + "\n\n"
    body = body + "Check " + website + "\n"

    logInfo(RFN, "===========> SUNRISE ALERT: quality is forecast to be " + SunriseQuality.state)

    sendMail(mailTo, subject, body)
end


rule "Great Sunset Alert"
when
    Item SunsetQuality changed to Great or
    Item TestSunsetAlertRule received command ON

then
    val RFN = "rule:sunset-alert"

    var String modelType = "sunset"
    var SimpleDateFormat df = new SimpleDateFormat( "MM/dd/YYYY hh:mm a" )
    var String validAt = df.format( (SunsetValidAt.state as DateTimeType).calendar.timeInMillis )
    var String lastUpdated = df.format( (SunsetLastUpdated.state as DateTimeType).calendar.timeInMillis )
    var String percent = String.format("%.1f", (SunsetQualityPercent.state as DecimalType).floatValue())
    var String region = (SunsetRegion.state as StringType).toString()
    var String website = "http://www.sunsetwx.com/sunset/sunset_et.png"
        
    val String mailTo = "mark@hilbush.com"
    var String subject = "ALERT: Great " + modelType + " predicted in " + region
    
    var String body = ""
    body = body + "Great " + modelType + " predicted in " + region + "!\n\n"
    body = body + "Quality Percent is " + percent + "%\n"
    body = body + "Conditions predicted to occur at " + validAt + "\n"
    body = body + "Model last updated at " + lastUpdated + "\n\n"
    body = body + "Check " + website + "\n"

    logInfo(RFN, "===========> SUNSET ALERT: quality is forecast to be " + SunsetQuality.state)

    sendMail(mailTo, subject, body)
end
```

### Manual Thing Creation

Place a file named *sunsetwx.things* in the *conf/things* directory.  The file should contain lines formatted like this.

```
Thing sunsetwx:sunrise:home [ geoLocation="-77.8600012,40.7933949", radius=50.0, limit=40, updateFrequency=20, location="northamerica", emailAddress="test@yourdomain.com", password="secret" ]
Thing sunsetwx:sunset:home [ geoLocation="-77.8600012,40.7933949", radius=50.0, limit=40, updateFrequency=20, location="northamerica", emailAddress="test@yourdomain.com", password="secret" ]
```
