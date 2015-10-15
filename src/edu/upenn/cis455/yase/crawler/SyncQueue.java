package edu.upenn.cis455.YASE.crawler;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

class KeyComparator implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = 1L;

	public int compare(byte[] key1, byte[] key2) {
		return new BigInteger(key1).compareTo(new BigInteger(key2));
	}
}

public class SyncQueue {
	private final Environment dbEnv;
	private final Database db;
	private final int cacheSize;
	private int cacheCounter;

	public SyncQueue(String dbPath, String dbName,
			int cacheSize) {

		new File(dbPath).mkdirs();
		EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
		dbEnvConfig.setTransactional(false);
		dbEnvConfig.setAllowCreate(true);

		this.dbEnv = new Environment(new File(dbPath), dbEnvConfig);
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setDeferredWrite(true);
		dbConfig.setTransactional(false);
		dbConfig.setBtreeComparator(new KeyComparator());
		dbConfig.setAllowCreate(true);
		this.db = dbEnv.openDatabase(null, dbName, dbConfig);
		this.cacheSize = cacheSize;
		this.cacheCounter = 0;

	}

	public synchronized String poll() throws IOException {
		final DatabaseEntry key = new DatabaseEntry();
		final DatabaseEntry data = new DatabaseEntry();
		final Cursor cursor = db.openCursor(null, null);
		try {
			cursor.getFirst(key, data, LockMode.RMW);
			if (data.getData() == null)
				return null;
			final String res = new String(data.getData(), "UTF-8");
			cursor.delete();
			cacheCounter++;
			if (cacheCounter >= cacheSize) {
				db.sync();
				cacheCounter = 0;
			}
			return res;
		} finally {
			cursor.close();
		}
	}

	public synchronized void addOne(final String element) throws IOException {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();
		Cursor cursor = db.openCursor(null, null);
		try {
			cursor.getLast(key, value, LockMode.RMW);
			BigInteger prevKeyValue;
			if (key.getData() == null) {
				prevKeyValue = BigInteger.valueOf(-1);
			} else {
				prevKeyValue = new BigInteger(key.getData());
			}
			BigInteger newKeyValue = prevKeyValue.add(BigInteger.ONE);

			try {
				final DatabaseEntry newKey = new DatabaseEntry(
						newKeyValue.toByteArray());
				final DatabaseEntry newData = new DatabaseEntry(
						element.getBytes("UTF-8"));
				db.put(null, newKey, newData);
				cacheCounter++;
				if (cacheCounter >= cacheSize) {
					db.sync();
					cacheCounter = 0;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			cursor.close();
		}
	}

	public synchronized void addBatch(LinkedList<String> urlList)
			throws IOException {
		for (String s : urlList)
			this.addOne(s);

	}

	public synchronized boolean isEmpty() {
		return db.count() <= 0;
	}

	public synchronized void sync(){
		db.sync();
	}
	public synchronized void close() {
		db.close();
		dbEnv.close();
	}
}
