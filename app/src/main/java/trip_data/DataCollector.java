package trip_data;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/* Class Name: DataCollector
 * Class Author: Brooke Cronin
 * Date: November 14, 2024
 * Course: SENG71000
 *
 * Description: This class is responsible for collecting location and sensor data,
 *              including speed, g-force, and turning rate, from the device's GPS and sensors.
 */
public class DataCollector
{

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Location lastLocation;

    private float currentSpeedKmh = 0f;      // Speed in km/h
    private float currentGForce = 0f;        // G-force in m/s^2
    private float turningRateDps = 0f;       // Turning rate in degrees per second

    /* Method Name: DataCollector
     * Method Author: Brooke Cronin
     * Description: Constructor for the DataCollector class. Initializes the location and sensor managers.
     * Parameters: Context context (application context to access system services)
     * Returns: N/A
     */
    public DataCollector(Context context)
    {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /* Method Name: startDataCollection
     * Method Author: Brooke Cronin
     * Description: Starts data collection by registering location and sensor listeners.
     * Parameters: N/A
     * Returns: N/A
     */
    public void startDataCollection()
    {
        // Register location updates
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        // Register sensor listeners for accelerometer and gyroscope
        Sensor accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        this.sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* Method Name: stopDataCollection
     * Method Author: Brooke Cronin
     * Description: Stops data collection by unregistering location and sensor listeners.
     * Parameters: N/A
     * Returns: N/A
     */
    public void stopDataCollection()
    {
        // Unregister location and sensor listeners
        this.locationManager.removeUpdates(locationListener);
        this.sensorManager.unregisterListener(sensorEventListener);
    }

    /* Method Name: getCurrentSpeedKmh
     * Method Author: Brooke Cronin
     * Description: Returns the current speed in kilometers per hour.
     * Parameters: N/A
     * Returns: float (the current speed in km/h)
     */
    public float getCurrentSpeedKmh()
    {
        return this.currentSpeedKmh;
    }

    /* Method Name: getCurrentGForce
     * Method Author: Brooke Cronin
     * Description: Returns the current g-force.
     * Parameters: N/A
     * Returns: float (the current g-force)
     */
    public float getCurrentGForce()
    {
        return this.currentGForce;
    }

    /* Method Name: getTurningRateDps
     * Method Author: Brooke Cronin
     * Description: Returns the current turning rate in degrees per second.
     * Parameters: N/A
     * Returns: float (the turning rate in degrees per second)
     */
    public float getTurningRateDps()
    {
        return this.turningRateDps;
    }

    // Location listener for speed calculation
    private final LocationListener locationListener = new LocationListener()
    {
        /* Method Name: onLocationChanged
         * Method Author: Brooke Cronin
         * Description: Updates the speed based on the change in location.
         * Parameters: Location location (the new location data)
         * Returns: N/A
         */
        @Override
        public void onLocationChanged(Location location)
        {
            if (lastLocation != null)
            {
                // Calculate speed (m/s) using distance/time
                float distance = location.distanceTo(lastLocation);
                float timeElapsed = (location.getTime() - lastLocation.getTime()) / 1000.0f; // seconds
                float speedMps = distance / timeElapsed;
                currentSpeedKmh = speedMps * 3.6f; // Convert m/s to km/h
            }
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    // Sensor listener for g-force and turning rate calculation
    private final SensorEventListener sensorEventListener = new SensorEventListener()
    {
        /* Method Name: onSensorChanged
         * Method Author: Brooke Cronin
         * Description: Updates the g-force and turning rate based on sensor data.
         * Parameters: SensorEvent event (the sensor data event)
         * Returns: N/A
         */
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                // Calculate g-force based on x, y, z acceleration
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                currentGForce = (float) Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;
            }
            else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                // Gyroscope measures angular velocity (rad/s) for rotation on x, y, z axes
                float omegaX = event.values[0];
                float omegaY = event.values[1];
                float omegaZ = event.values[2];
                // Calculate the magnitude of the angular velocity vector
                float omegaMagnitude = (float) Math.sqrt(omegaX * omegaX + omegaY * omegaY + omegaZ * omegaZ);
                // Convert radians per second to degrees per second
                turningRateDps = (float) Math.toDegrees(omegaMagnitude);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
}
