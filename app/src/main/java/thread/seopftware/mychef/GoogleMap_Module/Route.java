package thread.seopftware.mychef.GoogleMap_Module;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by MSI on 2017-08-02.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
