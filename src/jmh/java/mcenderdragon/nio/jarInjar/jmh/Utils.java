package mcenderdragon.nio.jarInjar.jmh;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openjdk.jmh.infra.Blackhole;

public class Utils 	
{
	public static List<String> filesSequential(Path path) throws IOException
	{
		ArrayList<String> entries = new ArrayList<String>(); 
		Files.walk(path).map(Path::toString).forEach(entries::add);
		entries.trimToSize();
		return entries;
	}
	
	public static List<String> filesRandomOrder(Path path) throws IOException
	{
		List<String> l = filesSequential(path);
		Collections.shuffle(l);
		return l;
	}
	
	public static void readEveryFile(FileSystem fs, List<String> files, Blackhole hole) throws IOException
	{
		Path p = null;
		try
		{
			for(String path : files)
			{
				p = fs.getPath(path);
				if(Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
				{
					hole.consume(Files.readAllBytes(p));
				}
			}
		}
		catch(NoSuchFileException e)
		{
			System.out.println(p);
			throw(e);
		}
	}
}
