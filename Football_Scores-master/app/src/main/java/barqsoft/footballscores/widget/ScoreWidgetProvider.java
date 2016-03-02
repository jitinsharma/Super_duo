package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jitin on 14-02-2016.
 */
public class ScoreWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ScoreWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, ScoreWidgetIntentService.class));
    }
}
