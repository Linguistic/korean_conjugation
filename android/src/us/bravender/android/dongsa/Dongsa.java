package us.bravender.android.dongsa;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.Toast;
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
    private CheckBox regular;
    private VerbDatabase verbDatabase;
    private EditText edittext;
    private WebView engine;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        verbDatabase = new VerbDatabase(this);
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        engine = new WebView(this);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        this.regular = (CheckBox) findViewById(R.id.regular);
        edittext = (EditText) findViewById(R.id.searchEdit);
        
        this.regular.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateConjugations();
			}
        });
        
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateConjugations();
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
             public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            	 HashMap<String, String> conjugation = conjugations.get(position);
            	 if (conjugation.get("clickable").equals("no")) {
            		 return;
            	 }
            	 Intent next = new Intent();
                 next.setClass(Dongsa.this, ConjugationDetailActivity.class);
                 
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

    public void updateConjugations() {
    	String infinitive = edittext.getText().toString();
    	engine.loadUrl("javascript:update('" + infinitive + "', false);");
    }
    
    public void say(String text) {
    	Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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

    public boolean getRegular() {
        return this.regular.isChecked();
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
                			JSONObject conjugation = (JSONObject)conjugationArray.get(i);
                			HashMap<String,String> definitionItem = new HashMap<String,String>();
                			if (i == 0) {
                				if (!Dongsa.this.verbDatabase.verbExists(conjugation.getString("infinitive"))) {
                					say("entered verb not in database");
                				}
                				String definition = Dongsa.this.verbDatabase.verbDefinition(conjugation.getString("infinitive"));
                				if (!definition.equals("")) {
                					definitionItem.put("clickable", "no");
                					definitionItem.put("conjugation_name", "definition");
                					definitionItem.put("conjugated", definition);
                					Dongsa.this.conjugations.add(definitionItem);
                					adapter.notifyDataSetChanged();
                				}
                			}
                			HashMap<String,String> item = new HashMap<String,String>();
                			item.put("clickable", "yes");
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
            return ((Dongsa)mContext).getRegular();
        }

        public void showVerb(String json) {
            ((Dongsa)mContext).showVerb(json);
        }
    }
}
