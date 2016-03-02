package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by jitin on 15-02-2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoreListRemoteViewsService extends RemoteViewsService{
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

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri scoreUri = DatabaseContract.scores_table.buildScoreWithDate();
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] queryDate = new String[]{dateFormat.format(date)};
                data = getContentResolver().query(
                        scoreUri,
                        SCORE_COLUMNS,
                        "date = ?",
                        queryDate,
                        null
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_score);

                String homeName = data.getString(INDEX_HOME_COL);
                String awayName = data.getString(INDEX_AWAY_COL);
                int homeScore = data.getInt(INDEX_HOME_GOALS_COL);
                int awayScore = data.getInt(INDEX_AWAY_GOALS_COL);
                String time = data.getString(INDEX_TIME_COL);

                views.setTextViewText(R.id.home_name, homeName);
                views.setTextViewText(R.id.away_name, awayName);

                String scores = Utilies.getScores(homeScore, awayScore);
                views.setTextViewText(R.id.score_textview, scores);
                views.setTextViewText(R.id.data_textview, time);

                final Intent fillInIntent = new Intent();

                Uri scoreUri = DatabaseContract.scores_table.buildScoreWithDate();

                fillInIntent.setData(scoreUri);
                views.setOnClickFillInIntent(R.id.widget, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                // views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(1);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
