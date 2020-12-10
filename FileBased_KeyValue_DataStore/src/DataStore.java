import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataStore {
	
	static JSONObject data;
	
	public static File createFile(String filePath) {
		System.out.println("filePath " + filePath);
		File file = null;
		if (isValidPath(filePath)) {
			file = new File(filePath);
			if (!file.exists()) {
				try {
					file.createNewFile();
					System.out.println("File Created");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Invalid file path");
		}

		return file;
	}

	public static JSONObject readFile(String filePath) throws IOException{
		data = new JSONObject();
		JSONParser parser = new JSONParser();
		 
            Object obj = new JSONObject();
			try {
				obj = parser.parse(new FileReader(filePath));
			} catch (ParseException e) {
				
			}
			data = (JSONObject) obj;
			System.out.println("data --> " + data);
        return data;
	}
	
	public static void writeFile(String filePath) {
		try {
			FileWriter file = new FileWriter(filePath);
            file.write(data.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}

	public static void create(String key, String value, long timeout) {
		if(timeout !=0) {
			timeout += System.currentTimeMillis();
		}

		if (data.containsKey(key)) {
			System.out.println(key + " already exists");
		} else if (value.length() > 16 * 1024) {
			System.out.println(value + " exceeds 16KB");
		} else {
			JSONParser parser = new JSONParser();
			JSONArray objToWrite = new JSONArray();
			try {
				//check if value is of type JSONObject
				JSONObject json = (JSONObject) parser.parse(value);
				objToWrite.add(json);
			} catch (ParseException e) {				
				//value is not json
				objToWrite.add(value);
			}
			catch (ClassCastException e) {				
				//value may be of JSONArray
				try {
					JSONArray json = (JSONArray) parser.parse(value);
					objToWrite.add(json);
				} catch (ParseException pe) {				
					//value is not json
					objToWrite.add(value);
				}
				
			}
			objToWrite.add(timeout);
			data.put(key, objToWrite);
			// if data exceed 1GB after adding new value, remove that.
			if (data.size() > 1 * 1024 * 1024 * 1024) {
				data.remove(key);
				System.out.println("File size exceeds 1GB");
			}
		}
		System.out.println("data --> " + data);
	}

	public static Object read(String key) {
		Object value = null;		
		if (data.containsKey(key)) {
			value = data.get(key);
			try {
				JSONArray arrayValue = (JSONArray) value;
				if(arrayValue.size() == 2) {
					long timeout = (long) arrayValue.get(1);
					if(System.currentTimeMillis() < timeout || timeout == 0) {
						value = arrayValue.get(0);
						System.out.println("Value : " + value);
					}else {
						System.out.println(key + " has expired");
						return null;
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(key + " not exists");
		}
		
		return value;
	}

	public static void delete(String key) {	
		if (data.containsKey(key)) {
			Object value = data.get(key);
			try {
				JSONArray arrayValue = (JSONArray) value;
				if(arrayValue.size() == 2) {
					long timeout = (long) arrayValue.get(1);
					if(System.currentTimeMillis() < timeout || timeout == 0) {
						data.remove(key);
						System.out.println("Deleted " + key);
						System.out.println("Value : " + value);
					}else {
						System.out.println(key + " has expired");
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			System.out.println("data --> " + data);
		} else {
			System.out.println(key + " not exists");
		}
	}

}