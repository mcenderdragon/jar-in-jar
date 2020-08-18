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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
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
		
		System.out.println("Testing: " + f1);
		checkIfZipContentIsSame(f1);
		System.out.println("Testing: " + f2);
		checkIfZipContentIsSame(f2);
		System.out.println("Testing: " + f3);
		checkIfZipContentIsSame(f3);
		
	}
	
	public static void checkIfZipContentIsSame(File f) throws URISyntaxException, IOException
	{
		URI jarURI = new URI("jar:" + f.toURI() + "!/");
		URI jarInJarURI = new URI("jarInJar:/?"+ f.toURI());
		
		try
		{
			FileSystem jarFS = FileSystems.newFileSystem(jarURI, Collections.emptyMap());
			FileSystem jarInJarFS = FileSystems.newFileSystem(jarInJarURI,  Collections.emptyMap());
		
			HashSet<String> paths1 = getAllPaths(jarFS);
			HashSet<String> paths2 = getAllPaths(jarInJarFS);
			
			boolean sameSize = paths1.size() == paths2.size();
			System.out.println("Does directory count match: " + sameSize);
			
			if(!sameSize)
			{
				System.err.println("Directory count does not match");
				System.exit(1);
			}
			
			System.out.println(Arrays.toString(paths1.toArray()));
			System.out.println(Arrays.toString(paths2.toArray()));
			
			boolean sameDirectoryList = true;
			
			for(String base : paths1)
			{
				if(!paths2.contains(base))
					sameDirectoryList = false;
			}
			
			if(!sameDirectoryList)
			{
				System.err.println("Directorylist does not match");
				System.exit(1);
			}
			
			for(String base : paths2)
			{
				Path file1 = jarFS.getPath(base);
				Path file2 = jarInJarFS.getPath(base);
				
				BasicFileAttributes attr1 = Files.getFileAttributeView(file1, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
				BasicFileAttributes attr2 = Files.getFileAttributeView(file2, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
				
				if(attr1.isDirectory() != attr2.isDirectory() || attr1.isRegularFile() != attr2.isRegularFile())
				{
					System.err.println("Attribute mismatch (directory or file) " + base);
					System.exit(1);
				}
				
				if(attr1.isRegularFile())
				{
					if(attr1.size() != attr2.size())
					{
						System.err.println("Size mismatch at files " + base + " " + attr1.size()  + "!=" + attr2.size());
						System.exit(1);
					}
					
					if(!checkFileDataMatch(file1, file2))
					{
						System.err.println("Files did not match data " + base);
						System.exit(1);
					}
					else
					{
						System.out.println(base + ": OK");
					}
				}
			}
			
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
	
	private static boolean checkFileDataMatch(Path a, Path b) throws IOException
	{
		byte[] dataA = Files.readAllBytes(a);
		byte[] dataB = Files.readAllBytes(b);
		
		return Arrays.equals(dataA, dataB);
	}
}
