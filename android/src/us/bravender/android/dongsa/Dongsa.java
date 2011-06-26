package us.bravender.android.dongsa;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dongsa extends Activity {
    private ArrayList<HashMap<String,String>> conjugations = new ArrayList<HashMap<String,String>>();
    private ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final WebView engine = new WebView(this);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        final EditText edittext = (EditText) findViewById(R.id.searchEdit);
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                engine.loadUrl("javascript:update('" + v.getText() + "', false);");
                return true;
            }
        });

        this.list = (ListView) findViewById(R.id.listview);
        this.list.setAdapter(new SimpleAdapter(
            this,
            this.conjugations,
            R.layout.simple_expandable_list_item_2,
            new String[] { "conjugation_name", "conjugated" },
            new int[] { R.id.text1, R.id.text2 }
        ));
        this.list.setOnItemClickListener(new OnItemClickListener() {
            // @Override
             public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                 Intent next = new Intent();
                 next.setClass(Dongsa.this, ConjugationDetailActivity.class);
                 HashMap<String, String> conjugation = conjugations.get(position);
                 next.putExtra("infinitive", conjugation.get("infinitive"));
                 next.putExtra("conjugation_name", conjugation.get("conjugation_name"));
                 next.putExtra("conjugated", conjugation.get("conjugated"));
                 next.putExtra("pronunciation", conjugation.get("pronunciation"));
                 next.putExtra("romanized", conjugation.get("romanized"));
                 next.putExtra("reasons", conjugation.get("reasons"));
                 startActivity(next);
             }
        });

        edittext.setText("\ud558\ub2e4");
        engine.loadUrl("file:///android_asset/html/android.html");
    }

    public void clearList() {
        synchronized (this.conjugations) {
            final SimpleAdapter adapter = (SimpleAdapter)this.list.getAdapter();
            this.list.post(new Runnable() {
                public void run() {
                    Dongsa.this.conjugations.clear();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public boolean regular() {
        // TODO: UI for toggling this
        return true;
    }

    public void showVerb(final String json) {
        synchronized (this.conjugations) {
            final SimpleAdapter adapter = (SimpleAdapter)this.list.getAdapter();
            this.list.post(new Runnable() {
                public void run() {
                    Dongsa.this.conjugations.clear();
                    adapter.notifyDataSetChanged();
                	JSONArray conjugationArray;
					try {
						conjugationArray = new JSONArray(json);
						adapter.notifyDataSetChanged();
	                    for (int i=0; i<conjugationArray.length(); i++) {
							try {
								JSONObject conjugation = (JSONObject)conjugationArray.get(i);
								HashMap<String,String> item = new HashMap<String,String>();
								item.put("infinitive", conjugation.getString("infinitive"));
								item.put("conjugation_name", conjugation.getString("conjugation_name"));
								item.put("conjugated", conjugation.getString("conjugated"));
								item.put("pronunciation", conjugation.getString("pronunciation"));
								item.put("romanized", conjugation.getString("romanized"));
								StringBuffer reasons = new StringBuffer();
								JSONArray reasonArray = conjugation.getJSONArray("reasons");
								for (int j=0; j<reasonArray.length(); j++) {
									reasons.append(reasonArray.get(j) + "\n");
								}
								item.put("reasons", reasons.toString());
								Dongsa.this.conjugations.add(item);
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
							}
	                	}
					} catch (JSONException e) {
					}
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class JavaScriptInterface {
        Context mContext;
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public boolean regular() {
            return ((Dongsa)mContext).regular();
        }

        public void showVerb(String json) {
            ((Dongsa)mContext).showVerb(json);
        }
    }
}
