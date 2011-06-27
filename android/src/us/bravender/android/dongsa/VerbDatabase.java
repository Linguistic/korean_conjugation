package us.bravender.android.dongsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VerbDatabase {
	private static final String DB_NAME = "korean-verbs.sqlite";
    private static final String DB_PATH = "/data/data/us.bravender.android.dongsa/databases/";
	private SQLiteDatabase db;

	public VerbDatabase(Context mContext) {
		File dbFile = new File(DB_PATH + DB_NAME);
		try {
			if (!dbFile.exists()) {
		    	Log.i(VerbDatabase.class.getName(), "copying database chunks");
		       
		        AssetManager am = mContext.getAssets();
		        OutputStream os = new FileOutputStream(dbFile);
		        dbFile.createNewFile();
		        byte []b = new byte[1024];
		        int i, r;
		        String []Files = am.list("");
		        Arrays.sort(Files);
		        for(i=0;i<10;i++) {
		            String fn = String.format("%s%02d", DB_NAME, i);
		            Log.i(VerbDatabase.class.getName(), fn);
		            if(Arrays.binarySearch(Files, fn) < 0)
		                   break;
		            InputStream is = am.open(fn);
		            Log.i(VerbDatabase.class.getName(), "copying " + fn);
		            while((r = is.read(b)) != -1)
		                os.write(b, 0, r);
		            is.close();
		        }
		        os.flush();
		        os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	public boolean verbExists(String verb) {
		String[] parameters = new String[] {verb};
		Cursor c = db.rawQuery("SELECT * FROM valid_verbs WHERE infinitive = ?", parameters);
		boolean exists = false;
		if (c != null) {
			exists = (c.getCount() != 0);
		}
		return exists;
	}
	
	public String verbDefinition(String verb) {
		String[] parameters = new String[] {verb};
		Cursor c = db.rawQuery("SELECT definition FROM verbs WHERE infinitive = ?", parameters);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			return c.getString(c.getColumnIndex("definition"));
		}
		return "";
	}
}
