package mahmoud.com.atglance.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetRemoteViewsService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.e(" onGetViewFactory: ", "called");
		return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
	}

}
