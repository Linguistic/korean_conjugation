package us.bravender.android.dongsa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;

/**
 * Displays a word and its definition.
 */
public class ConjugationDetailActivity extends Activity {

	private TextView mInfinitive;
	private TextView mConjugationName;
    private TextView mConjugated;
    private TextView mPronunciation;
    private TextView mRomanized;
    private TextView mReasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conjugation_detail);

        mInfinitive = (TextView) findViewById(R.id.infinitive);
        mConjugationName = (TextView) findViewById(R.id.conjugation);
        mConjugated = (TextView) findViewById(R.id.conjugated);
        mPronunciation = (TextView) findViewById(R.id.pronunciation);
        mRomanized = (TextView) findViewById(R.id.romanized);
        mReasons = (TextView) findViewById(R.id.reasons);
        
        Intent intent = getIntent();
        
        mInfinitive.setText(intent.getStringExtra("infinitive"));
        mConjugationName.setText(intent.getStringExtra("conjugation_name"));
        mConjugated.setText(intent.getStringExtra("conjugated"));
        mPronunciation.setText(intent.getStringExtra("pronunciation"));
        mRomanized.setText(intent.getStringExtra("romanized"));
        mReasons.setText(intent.getStringExtra("reasons"));
    }
}
