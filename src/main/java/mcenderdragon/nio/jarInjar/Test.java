package mcenderdragon.nio.jarInjar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipInputStream;

import javax.swing.text.StyleContext.SmallAttributeSet;

public class Test 
{
	public static void main(String[] args) throws Exception 
	{
//		new ZipFSProvider();
//		
//		
		File f1 = new File("./src/main/resources/ZipWithFile.zip").getAbsoluteFile();
		File f2 = new File("./src/main/resources/ZipWithZip.zip").getAbsoluteFile();
		File f3 = new File("./src/main/resources/test.jar").getAbsoluteFile();
		
		checkIfZipContentIsSame(f1);
		
	}
	
	public static void checkIfZipContentIsSame(File f) throws URISyntaxException, IOException
	{
		URI jarURI = new URI("jar:" + f.toURI() + "!/");
		URI jarInJarURI = new URI("jarInJar:/?"+ f.toURI());
		
		try
		{
			FileSystem jarFS = FileSystems.newFileSystem(jarURI, Collections.emptyMap());
			FileSystem jrInJarFS = FileSystems.newFileSystem(jarInJarURI,  Collections.emptyMap());
		
			HashSet<String> paths1 = getAllPaths(jarFS);
			HashSet<String> paths2 = getAllPaths(jrInJarFS);
			
			boolean sameSize = paths1.size() == paths2.size();
			System.out.println("Does Size Match: " + sameSize);
			
			System.out.println(Arrays.toString(paths1.toArray()));
			System.out.println(Arrays.toString(paths2.toArray()));
			
			
			if(!sameSize)
				System.exit(1);
		}
		catch(FileSystemNotFoundException e)
		{
			System.err.println("JarInJar FileSystem Provider is not correctly installed");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static HashSet<String> getAllPaths(FileSystem fs)
	{
		HashSet<String> paths = new HashSet<>();
		
		fs.getRootDirectories().forEach(p -> {
				try 
				{
					Files.walk(p).forEach(pp -> 
					{
						paths.add(pp.toString());
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		
		return paths;
	}
}
