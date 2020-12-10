import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainDataStore {

	static String filename = "DataStore.txt";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		String dataFilePath;
		String defaultDirectory = "E:\\";
		Scanner scan = new Scanner(System.in);
		System.out.println("Provide the absolute file path or press 'Enter' key to select the default file path ("
				+ defaultDirectory + filename + ")");
		dataFilePath = scan.nextLine();
		if (dataFilePath.isEmpty()) {
			dataFilePath = defaultDirectory + filename;
		}

		File file = DataStore.createFile(dataFilePath);
		if (file != null) {
			try {
				DataStore.readFile(dataFilePath);
				
				while(true) {
					System.out.println("1.create\n2.read\n3.delete\n4.exit");
					String opt = scan.nextLine();
					switch(opt) 
					{
						case "1":
						{
							// CREATE
							System.out.print("Enter key : ");
							String key = scan.nextLine();

							System.out.print("Enter value : ");
							String value = scan.nextLine();

							System.out.print("Enter time to live in ms (1s = 1000ms): ");
							String ttlString = scan.nextLine();
							long ttl = 0;
							try {
								ttl = Integer.parseInt(ttlString);
							}catch(NumberFormatException e) {
							
							}
							DataStore.create(key, value, ttl);break;
						}
						case "2": 
						{
							// READ
							System.out.print("Enter key : ");
							String key = scan.nextLine();
							DataStore.read(key);break;
						} 
						case "3": 
						{
							// DELETE
							System.out.print("Enter key : ");
							String key = scan.nextLine();
							DataStore.delete(key);break;
						}
						case "4": 
						{
							// EXIT
							System.out.println("Exit");
							DataStore.writeFile(dataFilePath);
							return;
						}
						default: System.out.println("Invalid Choice");
					}
					
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return;
	}


}
