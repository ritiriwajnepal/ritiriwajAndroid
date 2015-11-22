package co.ritiriwaj.android.db.tables;

public final class TblRitual {

	public static String TABLE_NAME = "ritual";

	public static String ID = "id";
	public static String NAME = "name";
	public static String DATE = "date";
	public static String NEP_DATE = "nep_date";
	public static String WHAT = "what";
	public static String IMAGE = "image";
	public static String VIDEOURL = "videoURL";
	public static String HISTORY = "history";
	public static String SIGNIFICANCE = "significance";
	public static String INTERESTING_FACTS = "interesting_facts";
	public static String SOURCE = "source";
	public static String LAST_MODIFIED = "last_modified";
	public static String STATUS = "status";
	
	/*
	 * WARNING: only two type of string: set('public', 'private')
	 */
	public static String TYPE = "type";

	public static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID
			+ " INTEGER PRIMARY KEY," + NAME + " TEXT," + DATE + " TEXT,"
			+ NEP_DATE + " TEXT," + WHAT + " TEXT," + IMAGE + " TEXT,"
			+ VIDEOURL + " TEXT," + HISTORY + " TEXT," + SIGNIFICANCE
			+ " TEXT," + INTERESTING_FACTS + " TEXT," + SOURCE + " TEXT,"
			+ TYPE + " TEXT," + LAST_MODIFIED + " TEXT,"+STATUS +" INTEGER"+ ")";

	/*
	 * `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(255) NOT NULL,
	 * `date` text NOT NULL, `nep_date` date NOT NULL, `what` text NOT NULL,
	 * `image` text NOT NULL, `videoURL` text NOT NULL, `history` text NOT NULL,
	 * `significance` text NOT NULL, `interesting_facts` text NOT NULL, `source`
	 * text NOT NULL, `type` set('public','private') NOT NULL, PRIMARY KEY
	 * (`id`)
	 */

}
