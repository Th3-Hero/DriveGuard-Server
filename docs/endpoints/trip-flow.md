## Trip flow
All require auth (driver id and session token)

### List trips
Get a list of trips with basic info for a specific driver. This is made for past trips list

### Get Trip Summary
Get detailed information about a specific trip. Used for when selecting a past trip to view details.

### Get current trip
Returns the current trip the driver is on. If the driver is not on a trip a `404` will be returned.
Useful for if the app is closed before a trip is ended and context is lost. The driver cannot have more than one active trip.

### Start trip
Starting a trip will return basic info about the trip. \
**NOTE: You will want to save the trip id for adding events and ending the trip**

### End trip
Ending the trip returns the completed trip data to be used in trip summery.