package mcenderdragon.nio.jarInjar;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class HelperURIFormatter 
{
	public static final String sheme = "jarInJar";
	
	public static URI asJarInJarURI(Path p) throws URISyntaxException
	{
		return asJarInJarURI(p.toUri());
	}
	
	public static URI asJarInJarURI(File p) throws URISyntaxException
	{
		return asJarInJarURI(p.toURI());
	}
	
	public static URI asJarInJarURI(URI p) throws URISyntaxException
	{
		return new URI(sheme+":/?" + p.toString());
	}
}
