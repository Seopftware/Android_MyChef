package thread.seopftware.mychef.GoogleMap_Module;

import java.util.List;

/**
 * Created by MSI on 2017-08-02.
 */

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
