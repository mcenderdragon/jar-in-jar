package mcenderdragon.nio.jarInjar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipInputStream;

public class Test 
{
	public static void main(String[] args) 
	{
		File f1 = new File("./src/jarInJar/resources/ZipWithFile.zip").getAbsoluteFile();
		File f2 = new File("./src/jarInJar/resources/ZipWithZip.zip").getAbsoluteFile();
		File f3 = new File("./src/jarInJar/resources/test.jar").getAbsoluteFile();
		
//		try
//		{
//		listFileInZip(f1.toPath());
//		listFileInZip(f2.toPath());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			System.exit(1);
//		}
		
		try {
			new ZipArchive(new FileInputStream(f3));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void listFileInZip(Path path) throws IOException
	{
		System.out.println(path);
		
		FileSystem fs = FileSystems.newFileSystem(path, Test.class.getClassLoader());
		
		fs.getRootDirectories().forEach(p -> {
			try 
			{
				Files.walk(p).forEach(pp -> 
				{
					System.out.println(pp + "\t" + pp.toUri());
//					if(pp.toString().endsWith(".zip"))
//					{
//						try {
//							listFileInZip(pp);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		
		fs.close();
	}
	
	public static void listSubPaths(Path p)
	{
		System.out.println(p.toUri());
		
	}
}
