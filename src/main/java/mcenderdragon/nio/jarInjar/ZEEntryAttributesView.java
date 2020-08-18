package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;

public class ZEEntryAttributesView implements BasicFileAttributeView, FileAttributeView
{
	private final BasicFileAttributes attributes;
	
	public ZEEntryAttributesView(BasicFileAttributes attributes) 
	{
		super();
		if(attributes==null)
			throw new NullPointerException();
		
		this.attributes = attributes;
	}

	@Override
	public String name() 
	{
		return "basic";
	}

	@Override
	public BasicFileAttributes readAttributes() throws IOException 
	{
		return attributes;
	}

	@Override
	public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException 
	{
		throw new SecurityException(new UnsupportedOperationException("Write is not supported for this FIleSystem"));
	}

}
