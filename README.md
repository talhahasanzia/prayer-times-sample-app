# prayer-times-sample-app
Prayer times sample app uses json data from server xhanch.com/xhanch-api-islamic-get-prayer-time/ using following query parameters:

- Year and month.
- Longitude and Latitude.
- Timezone.
- response mode (json/xml).


## Url format:
http://api.xhanch.com/islamic-get-prayer-time.php?lng=longitude&lat=latitude&yy=year&mm=month&gmt=gmt&m=mode

    <longitude>; required; longitude coordinate of a location
    <latitude>; required; latitude coordinate of a location
    <year>; required; year of the prayer time
    <month>; required; month of the prayer time
    <gmt>; required; timezone of a location
    <mode>; xml or json; optional; default is xml; api result mode that you prefer

## Example url:
http://api.xhanch.com/islamic-get-prayer-time.php?lng=127&lat=90&yy=2010&mm=3&gmt=2&m=json
