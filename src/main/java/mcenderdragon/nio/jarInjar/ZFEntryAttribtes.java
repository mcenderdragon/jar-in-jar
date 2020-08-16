package mcenderdragon.nio.jarInjar;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.zip.ZipEntry;

public class ZFEntryAttribtes implements BasicFileAttributes 
{
	private final ZipEntry entry;
	
	

	public ZFEntryAttribtes(ZipEntry entry) 
	{
		super();
		this.entry = entry;
	}

	@Override
	public FileTime lastModifiedTime() 
	{
		return entry.getLastModifiedTime();
	}

	@Override
	public FileTime lastAccessTime() 
	{
		return entry.getLastAccessTime();
	}

	@Override
	public FileTime creationTime() 
	{
		return entry.getCreationTime();
	}

	@Override
	public boolean isRegularFile() 
	{
		return !entry.isDirectory();
	}

	@Override
	public boolean isDirectory() 
	{
		return entry.isDirectory();
	}

	@Override
	public boolean isSymbolicLink() 
	{
		return false;
	}

	@Override
	public boolean isOther() 
	{
		return false;
	}

	@Override
	public long size() 
	{
		return entry.getSize();
	}

	@Override
	public Object fileKey() 
	{
		return null;
	}

}
