package mcenderdragon.nio.jarInjar;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractPath<T extends FileSystem> implements Path 
{
	protected final T fs;
	protected final String path;
	
	public AbstractPath(T fs, String path) 
	{
		this.fs = fs;
		this.path = path;
	}
	
	@Override
	public T getFileSystem() 
	{
		return fs;
	}

	@Override
	public boolean isAbsolute() 
	{
		return path.length() >0 && path.charAt(0) == '/';
	}

	@Override
	public int compareTo(Path other) 
	{
		return path.compareTo(other.toString());
	}
	
	@Override
	public String toString() 
	{
		return path;
	}

	public static String toAbsolutePath(String path, String separator)
	{
		ArrayList<String> parts = new ArrayList<>(Arrays.asList(path.split(separator)));
		for(int i=0;i<parts.size();i++)
		{
			if(parts.get(i).equals(".") || parts.get(i).equals(""))
			{
				parts.remove(i);
				i--;
			}
			else if(parts.get(i).equals(".."))
			{
				parts.remove(i);
				i--;
				if(i>=0)
				{
					parts.remove(i);
					i--;
				}
			}
		}
		return parts.stream().collect(Collectors.joining(separator));
	}
}
