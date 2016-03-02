package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by jitin on 14-02-2016.
 */
public class ScoreWidgetIntentService extends IntentService{
    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.LEAGUE_COL
    };

    private static final int INDEX_TIME_COL = 0;
    private static final int INDEX_HOME_COL = 1;
    private static final int INDEX_AWAY_COL = 2;
    private static final int INDEX_HOME_GOALS_COL = 3;
    private static final int INDEX_AWAY_GOALS_COL = 4;
    private static final int INDEX_MATCH_ID = 5;
    private static final int INDEX_LEAGUE_COL = 6;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ScoreWidgetProvider.class));

        Uri scoreUri = DatabaseContract.scores_table.buildScoreWithDate();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] queryDate = new String[]{dateFormat.format(date)};
        Cursor data = getContentResolver().query(
                scoreUri,
                SCORE_COLUMNS,
                "date = ?",
                queryDate,
                null
        );

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String homeName = data.getString(INDEX_HOME_COL);
        String awayName = data.getString(INDEX_AWAY_COL);
        int homeScore = data.getInt(INDEX_HOME_GOALS_COL);
        int awayScore = data.getInt(INDEX_AWAY_GOALS_COL);
        int matchId = data.getInt(INDEX_MATCH_ID);
        String league = data.getString(INDEX_LEAGUE_COL);
        String testDate = data.getString(INDEX_TIME_COL);

        data.close();

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_score);
            String scores = Utilies.getScores(homeScore, awayScore);
            views.setTextViewText(R.id.home_name, homeName);
            views.setTextViewText(R.id.away_name, awayName);
            views.setTextViewText(R.id.score_textview, scores);
            views.setTextViewText(R.id.data_textview, testDate);

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
