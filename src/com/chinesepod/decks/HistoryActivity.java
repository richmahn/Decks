package com.chinesepod.decks;

import android.app.ListActivity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;
import java.util.Map;

/**
 * This activity displays the history of past translations.
 *
 * @author Cedric Beust
 * @author Daniel Rall
 */
public class HistoryActivity extends ListActivity implements OnItemClickListener {
    private SimpleAdapter mAdapter;
    private List<Map<String, String>> mListData;
    private History mHistory;

    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String FROM_SHORT_NAME = "from-short-name";
    private static final String TO_SHORT_NAME = "to-short-name";

    // These constants are used to bind the adapter to the list view
    private static final String[] COLUMN_NAMES = { INPUT, OUTPUT, FROM, TO };
    private static final int[] VIEW_IDS = { R.id.input, R.id.output, R.id.from, R.id.to };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.history_activity);
        
        mHistory = new History(CPDecksApplication.getPrefs(this));
        initializeAdapter(mHistory.getHistoryRecordsMostRecentFirst());
        getListView().setEmptyView(findViewById(R.id.empty));
    }
    
    private void initializeAdapter(List<HistoryRecord> historyRecords) {
        mListData = Lists.newArrayList();
        
        for (HistoryRecord hr : historyRecords) {
            Map<String, String> data = Maps.newHashMap();

            // Values that are bound to views
            data.put(INPUT, hr.mTranslation.getSource());
            data.put(OUTPUT, hr.mTranslation.getTarget());
            data.put(FROM, hr.mTranslation.getSourceLanguage().name());
            data.put(TO, hr.mTranslation.getTargetLanguage().name());

            // Extra values we keep around for convenience
            data.put(FROM_SHORT_NAME, hr.mTranslation.getSourceLanguage().getShortName());
            data.put(TO_SHORT_NAME, hr.mTranslation.getTargetLanguage().getShortName());
            mListData.add(data);
        }
        
        mAdapter = new SimpleAdapter(this, mListData, R.layout.history_record,
                COLUMN_NAMES, VIEW_IDS);
        getListView().setAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.most_recent:
            initializeAdapter(mHistory.getHistoryRecordsMostRecentFirst());
            break;

        case R.id.languages:
            initializeAdapter(mHistory.getHistoryRecordsByLanguages());
            break;

        case R.id.clear_history:
            mHistory.clear(this);
            initializeAdapter(mHistory.getHistoryRecordsByLanguages());
            break;
        }
        
        return true;
    }

    @SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, String> data = (Map<String, String>) parent.getItemAtPosition(position);
        Editor edit = CPDecksApplication.getPrefs(this).edit();
        CPDecksTranslateActivity.savePreferences(edit,
                data.get(FROM_SHORT_NAME), data.get(TO_SHORT_NAME), 
                data.get(INPUT), data.get(OUTPUT));
        finish();
    }
}
